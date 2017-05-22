package com.example.android.event;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.android.event.Model.BuatAcara;
import com.example.android.event.ViewHolder.ListAcaraPeserta;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.zip.Inflater;

import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;

public class PesertaActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabaseReference,acaraRef;
    private FirebaseRecyclerAdapter<BuatAcara,ListAcaraPeserta> mAdapter;
    private RecyclerView mRecyclerView;

    public static final String KEY_LIST_ACARA_PESERTA = "ACARA PESERTA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peserta);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_listAcaraPeserta);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Acara");
        acaraRef = mDatabaseReference.child(uid);




        mAdapter = new FirebaseRecyclerAdapter<BuatAcara, ListAcaraPeserta>
                (BuatAcara.class,R.layout.list_acara_peserta_item,ListAcaraPeserta.class,mDatabaseReference) {
            @Override
            protected void populateViewHolder(final ListAcaraPeserta viewHolder, BuatAcara model, int position) {
                final DatabaseReference acaraRef = getRef(position);
                final String keyAcara = acaraRef.getKey();

                viewHolder.mTvJudulAcaraPeserta.setText(model.getJudul());
                viewHolder.mTvOrganisasiPeserta.setText(model.getOrganisasi());
                viewHolder.mTvTanggalPeserta.setText(model.getWaktu());
                viewHolder.mTvKapasitasPeserta.setText(String.valueOf(model.getKapasitas()));

                Glide.with(getApplicationContext())
                        .load(model.getPhotoAcara())
                        .into(viewHolder.mIvPosterAcaraPeserta);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(PesertaActivity.this,DetailAcaraPeserta.class);
                        i.putExtra(KEY_LIST_ACARA_PESERTA,keyAcara);
                        startActivity(i);

                    }
                });





            }

        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Logut_id:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}

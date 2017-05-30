package com.example.android.event;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.event.Model.BuatAcara;
import com.example.android.event.ViewHolder.ListAcaraPenyedia;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Ref;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.key;
import static android.R.attr.thickness;

public class PenyediaActivity extends BaseActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private DatabaseReference mDatabaseReference,mDataref,mDatareference;
    private StorageReference mStorageReference;
    private FirebaseRecyclerAdapter<BuatAcara,ListAcaraPenyedia> mAdapter;
    private RecyclerView mRecyclerView;
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton mFloatingAction;


    private Uri mImageData;

    public static final String KEY_LIST_ACARA_PENYEDIA = "ACARA PENYEDIA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penyedia);

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Penyedia Acara").child(uid);
        mDataref = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();



        mRecyclerView = (RecyclerView)findViewById(R.id.rv_listAcara);
        mFloatingAction = (FloatingActionButton)findViewById(R.id.fa_create);
        mFloatingAction.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


    mAdapter = new FirebaseRecyclerAdapter<BuatAcara, ListAcaraPenyedia>(BuatAcara.class,R.layout.list_acara_item,ListAcaraPenyedia.class,mDatabaseReference) {
        @Override
        protected void populateViewHolder(final ListAcaraPenyedia viewHolder, BuatAcara model, final int position) {
                final DatabaseReference penyediaAcaraRef = getRef(position);
                final String key = penyediaAcaraRef.getKey();

                viewHolder.mTvJudulAcara.setText(model.getJudul());
                viewHolder.mTvTanggal.setText(model.getWaktu());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(PenyediaActivity.this, DetailAcaraPenyedia.class);
                        i.putExtra(KEY_LIST_ACARA_PENYEDIA, key);
                        startActivity(i);

                    }
                });
            viewHolder.dotsEdit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
               showPopUpMenu(viewHolder.dotsEdit,key);
                }
            });


            }
        };


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    mRecyclerView.setAdapter(mAdapter);


    }

    public void showPopUpMenu(View view,String key){
        PopupMenu popupMenu = new PopupMenu(PenyediaActivity.this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.edit_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new listAcaraPenyediaMenuClickListener(key));
        popupMenu.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logut_id:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fa_create:
                startActivity(new Intent(getApplicationContext(),BuatAcaraActivity.class));
                break;
    }
}

    private void deleteAcara(String Ref){
        final String uidStorage = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference mDeleteRef = mStorageReference.child(getUid()).child(Ref);
        mDataref.child("Acara").child(Ref).removeValue();
        mDataref.child("Penyedia Acara").child(getUid()).child(Ref).removeValue();
        mDataref.child("Peserta Acara").child(Ref).removeValue();
        mDeleteRef.delete();


    }

    class listAcaraPenyediaMenuClickListener implements PopupMenu.OnMenuItemClickListener{

        String Ref;
        public  listAcaraPenyediaMenuClickListener(String key){
            Ref = key;}

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editAcaraPenyedia:
                    Intent i = new Intent(getApplicationContext(), BuatAcaraActivity.class);
                    i.putExtra(PenyediaActivity.KEY_LIST_ACARA_PENYEDIA, Ref);
                    startActivity(i);
                    return true;
                case R.id.hapusAcaraPenyedia:
                    deleteAcara(Ref);
                    return true;
                default:
                return true;
            }
        }
    }
}

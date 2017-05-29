package com.example.android.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.event.Model.BuatAcara;
import com.example.android.event.Model.Peserta;
import com.example.android.event.ViewHolder.ListKehadiranPeserta;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.key;
import static android.R.attr.sharedUserId;

public class DetailAcaraPenyedia extends BaseActivity {

    private TextView mTvJudulAcara;
    private ImageView mIvGambarAcara;

    private RecyclerView  mRecyclerview;
    private FirebaseRecyclerAdapter<Peserta,ListKehadiranPeserta> mAdapter;

    private String key;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef,mkehadiranRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_acara_penyedia);

       key = getIntent().getStringExtra(PenyediaActivity.KEY_LIST_ACARA_PENYEDIA);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mkehadiranRef = mDatabaseRef.child("Peserta Acara");
        firebaseAuth = FirebaseAuth.getInstance();


        mTvJudulAcara = (TextView) findViewById(R.id.tv_judulDetailAcaraPenyedia);
        mIvGambarAcara = (ImageView) findViewById(R.id.iv_detailAcaraPenyedia);

        mRecyclerview = (RecyclerView) findViewById(R.id.rv_listPesertaDetailAcaraPenyedia);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerview.getContext()
        ,linearLayoutManager.getOrientation());
        mRecyclerview.addItemDecoration(dividerItemDecoration);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        final DatabaseReference pesertaRef = mkehadiranRef.child(key);

        mAdapter = new FirebaseRecyclerAdapter<Peserta, ListKehadiranPeserta>
                (Peserta.class, R.layout.list_detail_acara_peserta_item,ListKehadiranPeserta.class,pesertaRef) {
            @Override
            protected void populateViewHolder(final ListKehadiranPeserta viewHolder, Peserta model, final int position) {
                final DatabaseReference kehadiranRef = getRef(position);
                final String keyPeserta = kehadiranRef.getKey();

                Glide.with(getApplicationContext())
                        .load(model.getPhotoUrlPeserta())
                        .into(viewHolder.mCiPeserta);
                viewHolder.mTvNamaPeserta.setText(model.getNamaPeserta());

                viewHolder.mIvHapusPeserta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRef(position).removeValue();
                    }
                });
            }
        };
        mRecyclerview.setAdapter(mAdapter);


        mDatabaseRef.child("Acara").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(key)){
                    BuatAcara buatAcara = dataSnapshot.child(key).getValue(BuatAcara.class);
                    mTvJudulAcara.setText(buatAcara.getJudul());
                    Glide.with(getApplicationContext())
                            .load(buatAcara.getPhotoAcara())
                            .into(mIvGambarAcara);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

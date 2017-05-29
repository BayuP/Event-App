package com.example.android.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.event.Model.BuatAcara;
import com.example.android.event.Model.Peserta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.button;

public class DetailAcaraPeserta extends BaseActivity implements View.OnClickListener {

    private TextView mTvJudulDetailAcaraPeserta,mTvDEskripsiDetailAcaraPeserta,
    mTvTanggalDanHariDetailAcaraPeserta,mTvTempatDetailAcaraPeserta,mTvKapasitasDetailAcaraPeserta,mTvPenyelenggaraDetailAcaraPeserta;

    private ImageView mIvGambarDetailAcaraPeserta;
    private Button mBtnPesanTiket;

    private String key;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_acara_peserta);

        key = getIntent().getStringExtra(PesertaActivity.KEY_LIST_ACARA_PESERTA);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mBtnPesanTiket = (Button) findViewById(R.id.btn_pesanTiketPeserta);
        mBtnPesanTiket.setOnClickListener(this);

        mTvJudulDetailAcaraPeserta = (TextView) findViewById(R.id.tv_judulDetailAcaraPeserta);
        mTvDEskripsiDetailAcaraPeserta = (TextView) findViewById(R.id.tv_deskripsiDetailAcaraPeserta);
        mTvTanggalDanHariDetailAcaraPeserta = (TextView) findViewById(R.id.tv_tanggalDanHariDetailAcaraPeserta);
        mTvTempatDetailAcaraPeserta = (TextView) findViewById(R.id.tv_tempatDetailAcaraPeserta);
        mTvKapasitasDetailAcaraPeserta = (TextView) findViewById(R.id.tv_kapasitasDetailAcaraPeserta);
        mTvPenyelenggaraDetailAcaraPeserta = (TextView) findViewById(R.id.tv_penyelenggaraDetailAcaraPeserta);
        mIvGambarDetailAcaraPeserta = (ImageView) findViewById(R.id.iv_detaiAcaraPeserta);

        mDatabaseRef.child("Acara").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(key)) {
                    BuatAcara buatAcara = dataSnapshot.child(key).getValue(BuatAcara.class);

                    mTvJudulDetailAcaraPeserta.setText(buatAcara.getJudul());
                    mTvDEskripsiDetailAcaraPeserta.setText(buatAcara.getDeskripsi());
                    mTvTanggalDanHariDetailAcaraPeserta.setText(buatAcara.getWaktu());
                    mTvTempatDetailAcaraPeserta.setText(buatAcara.getTempat());
                    mTvKapasitasDetailAcaraPeserta.setText(String.valueOf(buatAcara.getKapasitas()));
                    mTvPenyelenggaraDetailAcaraPeserta.setText(buatAcara.getOrganisasi());

                    Glide.with(getApplicationContext())
                            .load(buatAcara.getPhotoAcara())
                            .into(mIvGambarDetailAcaraPeserta);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

 private void pesanTiket(String keys){
     showProgressDialog();
     final FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
     mDatabaseRef.child("Acara").addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             if(dataSnapshot.hasChild(key)){
                 BuatAcara buatAcara = dataSnapshot.child(key).getValue(BuatAcara.class);
                 if(buatAcara.getKapasitas() != 0){
                     Peserta peserta = new Peserta(mUser.getPhotoUrl().toString(),mUser.getDisplayName());
                     mDatabaseRef.child("Peserta Acara").child(key).child(getUid()).setValue(peserta);
                     hideProgressDialog();
                 }
             }
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });
 }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pesanTiketPeserta:
                pesanTiket(key);
                break;
        }
    }
}

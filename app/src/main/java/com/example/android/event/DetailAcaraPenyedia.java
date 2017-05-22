package com.example.android.event;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.event.Model.BuatAcara;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.key;
import static android.R.attr.sharedUserId;

public class DetailAcaraPenyedia extends AppCompatActivity {

    private TextView mTvJudulAcara;
    private ImageView mIvGambarAcara;

    private String key;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_acara_penyedia);

       key = getIntent().getStringExtra(PenyediaActivity.KEY_LIST_ACARA_PENYEDIA);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();


        mTvJudulAcara = (TextView) findViewById(R.id.tv_judulDetailAcaraPenyedia);
        mIvGambarAcara = (ImageView) findViewById(R.id.iv_detailAcaraPenyedia);

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

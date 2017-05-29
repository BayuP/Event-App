package com.example.android.event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.event.Model.BuatAcara;

import com.example.android.event.Model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.moreIcon;
import static com.bumptech.glide.Glide.with;

public class BuatAcaraActivity extends BaseActivity implements View.OnClickListener{

    private static final int FOTO_ACARA = 1113;
    private static final String REQUIRED = "Harus Di isi";
    private static  final String ERROR = "ada error";

    private String photoAcara,judulAcara,deskripsiAcara,tempatAcara,waktuAcara,organisasi;
    private Uri mImageData = null;
    private int kapasitasAcara;
    private String keyBawaan,key;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageREf;
    private DatabaseReference mDatabaseReference;

    private ImageButton mImageButton;
    private EditText mEtJudul,mEtDeskripsi,mEtTempat,mEtWaktu,mEtOrganisasi,mEtKapasitas;
    private FloatingActionButton mFloatingAction;
    private String urlTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_acara);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageREf = FirebaseStorage.getInstance().getReference();

        mImageButton = (ImageButton) findViewById(R.id.ib_posterAcara);
        mImageButton.setOnClickListener(this);

        mEtJudul = (EditText) findViewById(R.id.et_title);
        mEtDeskripsi = (EditText) findViewById(R.id.et_description);
        mEtTempat = (EditText) findViewById(R.id.et_place);
        mEtWaktu = (EditText) findViewById(R.id.et_date);
        mEtOrganisasi = (EditText)findViewById(R.id.et_organization);
        mEtKapasitas = (EditText)findViewById(R.id.et_capacity);

        mFloatingAction = (FloatingActionButton) findViewById(R.id.fa_Done);
        mFloatingAction.setOnClickListener(this);

         key = mDatabaseReference.child("Acara").push().getKey();

        if(getIntent().hasExtra(PenyediaActivity.KEY_LIST_ACARA_PENYEDIA)){
            keyBawaan = getIntent().getStringExtra(PenyediaActivity.KEY_LIST_ACARA_PENYEDIA);
            mDatabaseReference.child("Penyedia Acara").child(getUid()).child(keyBawaan).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    BuatAcara buatAcara = dataSnapshot.getValue(BuatAcara.class);
                    mEtJudul.setText(buatAcara.getJudul());
                    mEtDeskripsi.setText(buatAcara.getDeskripsi());
                    mEtWaktu.setText(buatAcara.getWaktu());
                    mEtTempat.setText(buatAcara.getTempat());
                    mEtKapasitas.setText(String.valueOf(buatAcara.getKapasitas()));
                    mEtOrganisasi.setText(buatAcara.getOrganisasi());
                    urlTemp = buatAcara.getPhotoAcara();
                    Glide.with(getApplicationContext())
                            .load(buatAcara.getPhotoAcara())
                            .into(mImageButton);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void inputPhoto(){
        Intent inputPhoto = new Intent(Intent.ACTION_GET_CONTENT);
        inputPhoto.setType("image/*");
        startActivityForResult(Intent.createChooser(inputPhoto,"Pilih foto acara"),FOTO_ACARA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FOTO_ACARA) {
            if (resultCode == RESULT_OK) {
                try {
                    mImageData = data.getData();
                    InputStream imageSteam = getApplicationContext().getContentResolver().openInputStream(mImageData);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageSteam);
                    mImageButton.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "tidak bisa mengambil gambar", Toast.LENGTH_SHORT).show();
                }
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"batal mengambil gambar",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadDatabase() {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String mJudul = mEtJudul.getText().toString();
        final String mDeskripsi = mEtDeskripsi.getText().toString();
        final String mTempat = mEtTempat.getText().toString();
        final String mWaktu = mEtWaktu.getText().toString();
        final String mKapasitas = mEtKapasitas.getText().toString().trim();
        final String mOrganisasi = mEtOrganisasi.getText().toString();


        if (TextUtils.isEmpty(mJudul)) {
            mEtJudul.setError(REQUIRED);
            Toast.makeText(this, "Tidak dapat input data, masih ada field kosong", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(mDeskripsi)) {
            mEtDeskripsi.setError(REQUIRED);

        }
        if (TextUtils.isEmpty(mTempat)) {
            mEtTempat.setError(REQUIRED);

        }
        if (TextUtils.isEmpty(mWaktu)) {
            mEtWaktu.setError(REQUIRED);

        }
        if (TextUtils.isEmpty(mKapasitas)) {
            mEtKapasitas.setError(REQUIRED);

        }
        if (TextUtils.isEmpty(mOrganisasi)) {
            mEtOrganisasi.setError(REQUIRED);

        }
        if ((TextUtils.isEmpty(mJudul)) || (TextUtils.isEmpty(mDeskripsi)) || (TextUtils.isEmpty(mTempat)) || (TextUtils.isEmpty(mWaktu)) || (TextUtils.isEmpty(mEtKapasitas.getText().toString()))
                || (TextUtils.isEmpty(mOrganisasi))) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
        }

        setEnable(true);
        if (mImageData != null) {
            Log.e(ERROR, "Di image data");
            showProgressDialog();
            String idFile = mImageData.getLastPathSegment();
            mStorageREf.child(uid).child(key).child(idFile).putFile(mImageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri urlPhoto = taskSnapshot.getDownloadUrl();
                            String urlphoto = urlPhoto.toString();
                            updateAcara(uid, mJudul, mDeskripsi, mWaktu, mTempat, mOrganisasi, Integer.parseInt(mKapasitas), urlphoto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Terdapat kesalahan format data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if ((urlTemp == null) && (mImageData == null)) {
            Toast.makeText(getApplicationContext(), "Masukan gambar", Toast.LENGTH_SHORT).show();
        }
        if ((urlTemp != null) && (mImageData == null)) {
                showProgressDialog();
                updateAcara(uid, mJudul, mDeskripsi, mWaktu, mTempat, mOrganisasi, Integer.parseInt(mKapasitas), urlTemp);
            }
        }


    private void setEnable(boolean b) {
        mEtJudul.setEnabled(b);
        mEtDeskripsi.setEnabled(b);
        mEtWaktu.setEnabled(b);
        mEtTempat.setEnabled(b);
        mEtKapasitas.setEnabled(b);
        mEtOrganisasi.setEnabled(b);
        mImageButton.setEnabled(b);
    }

    private void updateAcara(final String uid,final String judul,final String deskripsi,final String waktu,final String tempat,final String organisasi,final int kapasitas,final String urlphoto){
        mDatabaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);


                if(keyBawaan != null){
                    key = keyBawaan;
                }

                BuatAcara ba = new BuatAcara(getUid(),users.getNama(),judul,deskripsi,waktu,tempat,organisasi,kapasitas,urlphoto);
                Map<String, Object> buatacara = ba.toMap();
                Map<String,Object> updateAcara = new HashMap<>();
                updateAcara.put("/Acara/"+key,buatacara);
                updateAcara.put("/Penyedia Acara/"+getUid()+"/"+key,buatacara);
                mDatabaseReference.updateChildren(updateAcara);
                hideProgressDialog();
                finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.ib_posterAcara:
            inputPhoto();
            break;
        case R.id.fa_Done:
            uploadDatabase();

    }
    }


}

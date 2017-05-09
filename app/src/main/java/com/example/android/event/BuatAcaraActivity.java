package com.example.android.event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.event.Model.BuatAcara;

import com.example.android.event.Model.Users;
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

import static android.R.attr.id;
import static android.R.attr.moreIcon;
import static com.bumptech.glide.Glide.with;

public class BuatAcaraActivity extends BaseActivity implements View.OnClickListener{

    private static final int FOTO_ACARA = 1113;
    private static final String REQUIRED = "Harus Di isi";

    private String photoAcara,judulAcara,deskripsiAcara,tempatAcara,waktuAcara,organisasi;
    private Uri mImageData;
    private int kapasitasAcara;
    private String keyBawaan;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageREf;
    private DatabaseReference mDatabaseReference;

    private ImageButton mImageButton;
    private EditText mEtJudul,mEtDeskripsi,mEtTempat,mEtWaktu,mEtOrganisasi,mEtKapasitas;
    private FloatingActionButton mFloatingAction;


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
        mEtTempat = (EditText) findViewById(R.id.et_Place);
        mEtWaktu = (EditText) findViewById(R.id.et_Date);
        mEtOrganisasi = (EditText)findViewById(R.id.et_Organization);
        mEtKapasitas = (EditText)findViewById(R.id.et_capacity);

        mFloatingAction = (FloatingActionButton) findViewById(R.id.fa_Done);
        mFloatingAction.setOnClickListener(this);


    }

    private void inputPhoto(){
        Intent inputPhoto = new Intent(Intent.ACTION_GET_CONTENT);
        inputPhoto.setType("image/*");
        startActivityForResult(Intent.createChooser(inputPhoto,"Pilih foto acara"),FOTO_ACARA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == FOTO_ACARA){
                try{
                    mImageData = data.getData();
                    InputStream imageSteam = getApplicationContext().getContentResolver().openInputStream(mImageData);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageSteam);
                    mImageButton.setImageBitmap(selectedImage);
                }catch (FileNotFoundException e){
                    Toast.makeText(getApplicationContext(),"tidak bisa mengambil gambar",Toast.LENGTH_SHORT).show();
                }
            }
        }
    private void uploadDatabase(){
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String mJudul = mEtJudul.getText().toString();
        final String mDeskripsi = mEtDeskripsi.getText().toString();
        final String mTempat = mEtTempat.getText().toString();
        final String mWaktu = mEtWaktu.getText().toString().trim();
        final int mKapasitas = Integer.parseInt(mEtKapasitas.getText().toString());
        final String mOrganisasi = mEtOrganisasi.getText().toString();
        final String idFile = mImageData.getLastPathSegment();

        if (TextUtils.isEmpty(mJudul)){
            mEtJudul.setError(REQUIRED);
            Toast.makeText(this,"Tidak dapat input data, masih ada field kosong",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(mDeskripsi)){
            mEtDeskripsi.setError(REQUIRED);
            Toast.makeText(this,"Tidak dapat input data, masih ada field kosong",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(mTempat)){
            mEtTempat.setError(REQUIRED);
            Toast.makeText(this,"Tidak dapat input data, masih ada field kosong",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(mWaktu)){
            mEtWaktu.setError(REQUIRED);
            Toast.makeText(this,"Tidak dapat input data, masih ada field kosong",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(mEtKapasitas.getText().toString())){
            mEtKapasitas.setError(REQUIRED);
            Toast.makeText(this,"Tidak dapat input data, masih ada field kosong",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(mOrganisasi)){
            mEtOrganisasi.setError(REQUIRED);
            Toast.makeText(this,"Tidak dapat input data, masih ada field kosong",Toast.LENGTH_SHORT).show();
            return;
        }

        setEnable(true);
        if(mImageData != null){
            showProgressDialog();
           mStorageREf.child("Acara").child(uid).child(idFile).putFile(mImageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri urlPhoto = taskSnapshot.getDownloadUrl();
                            String urlphoto = urlPhoto.toString();
                            updateAcara(uid,mJudul,mDeskripsi,mWaktu,mTempat,mOrganisasi,mKapasitas,urlphoto);
                        }
                    });
        }else{
            Toast.makeText(this,"Masukan gambar",Toast.LENGTH_LONG).show();
        }

    }

    private void setEnable(boolean b) {
        mEtJudul.setEnabled(b);
        mEtDeskripsi.setEnabled(b);
        mEtWaktu.setEnabled(b);
        mEtTempat.setEnabled(b);
        mEtKapasitas.setEnabled(b);
        mEtOrganisasi.setEnabled(b);
    }

    private void updateAcara(final String uid,final String judul,final String deskripsi,final String waktu,final String tempat,final String organisasi,final int kapasitas,final String urlphoto){
        mDatabaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                String key = mDatabaseReference.child("Acara").child(getUid()).push().getKey();

                if(keyBawaan != null){
                    key = keyBawaan;
                }


                BuatAcara ba = new BuatAcara(getUid(),users.getNama(),judul,deskripsi,waktu,tempat,organisasi,kapasitas,urlphoto);
                Map<String, Object> buatacara = ba.toMap();
                Map<String,Object> updateAcara = new HashMap<>();
                updateAcara.put("/Acara/"+getUid()+"/"+key,buatacara);
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

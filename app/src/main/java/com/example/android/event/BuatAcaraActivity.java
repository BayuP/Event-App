package com.example.android.event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.moreIcon;
import static com.bumptech.glide.Glide.with;

public class BuatAcaraActivity extends BaseActivity implements View.OnClickListener{

    private static EditText mEtWaktu;
    private static EditText mEtJam;
    private static final int FOTO_ACARA = 1113;
    private static final String REQUIRED = "Harus Di isi";
    private static  final String ERROR = "ada error";

    private Uri mImageData = null;
    private int kapasitasAcara;
    private String keyBawaan,key;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageREf;
    private DatabaseReference mDatabaseReference;

    private ImageButton mImageButton;
    private EditText mEtJudul,mEtDeskripsi,mEtTempat,mEtOrganisasi,mEtKapasitas;

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
        mEtWaktu.setOnClickListener(this);
        mEtJam = (EditText) findViewById(R.id.et_time);
        mEtJam.setOnClickListener(this);
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
                    mEtJam.setText(buatAcara.getJam());
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
        final String mJam = mEtJam.getText().toString();


        if (TextUtils.isEmpty(mJudul)) {
            mEtJudul.setError(REQUIRED);

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
        if (TextUtils.isEmpty(mJam)) {
            mEtJam.setError(REQUIRED);

        }
        if (TextUtils.isEmpty(mKapasitas)) {
            mEtKapasitas.setError(REQUIRED);

        }
        if (TextUtils.isEmpty(mOrganisasi)) {
            mEtOrganisasi.setError(REQUIRED);

        }
        if ((TextUtils.isEmpty(mJudul)) || (TextUtils.isEmpty(mDeskripsi)) || (TextUtils.isEmpty(mTempat)) || (TextUtils.isEmpty(mWaktu)) || (TextUtils.isEmpty(mEtKapasitas.getText().toString()))
                || (TextUtils.isEmpty(mOrganisasi))) {
            mEtJudul.setError(REQUIRED);
            mEtDeskripsi.setError(REQUIRED);
            mEtTempat.setError(REQUIRED);
            mEtWaktu.setError(REQUIRED);
            mEtKapasitas.setError(REQUIRED);
            mEtOrganisasi.setError(REQUIRED);

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
                            updateAcara(uid, mJudul, mDeskripsi, mWaktu, mTempat, mOrganisasi, Integer.parseInt(mKapasitas), urlphoto,mJam);
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
                updateAcara(uid, mJudul, mDeskripsi, mWaktu, mTempat, mOrganisasi, Integer.parseInt(mKapasitas), urlTemp,mJam);
            }
        }


    private void setEnable(boolean b) {
        mEtJudul.setEnabled(b);
        mEtDeskripsi.setEnabled(b);
        mEtWaktu.setEnabled(b);
        mEtTempat.setEnabled(b);
        mEtKapasitas.setEnabled(b);
        mEtOrganisasi.setEnabled(b);
        mEtJam.setEnabled(b);
        mImageButton.setEnabled(b);

    }

    private void updateAcara(final String uid,final String judul,final String deskripsi,final String waktu,final String tempat,final String organisasi,final int kapasitas,
                             final String urlphoto,final String jam ){
        mDatabaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);


                if(keyBawaan != null){
                    key = keyBawaan;
                }

                BuatAcara ba = new BuatAcara(getUid(),users.getNama(),judul,deskripsi,waktu,tempat,organisasi,kapasitas,urlphoto,jam);
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
            break;
        case R.id.et_date:
            DatePickerFragment dp = new DatePickerFragment();
            dp.show(getSupportFragmentManager(),"Date");
            break;
        case R.id.et_time:
            TimePickerFragment tp = new TimePickerFragment();
            tp.show(getSupportFragmentManager(),"Time");
            break;

    }
    }

    public static class DatePickerFragment extends  DialogFragment
    implements  DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month =c.get(Calendar.MONTH);
             int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),this,year,month,day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String sDay =""+day;
            String sMonth = ""+month;

            if(day < 10){
                sDay = "0" +sDay;
            }
            if(month == 0){sMonth = "Januari";}
            if(month == 1){sMonth = "Febuari";}
            if(month == 2){sMonth = "Maret";}
            if(month == 3){sMonth = "April";}
            if(month == 4){sMonth = "Mei";}
            if(month == 5){sMonth = "Juni";}
            if(month == 6){sMonth = "Juli";}
            if(month == 7){sMonth = "Agustus";}
            if(month == 8){sMonth = "September";}
            if(month == 9){sMonth = "Oktober";}
            if(month == 10){sMonth = "November";}
            if(month == 11){sMonth = "Desember";}
            mEtWaktu.setText(sDay+ " "+sMonth+" "+year);
        }
    }
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            // Get a Calendar instance
            final Calendar calendar = Calendar.getInstance();
            // Get the current hour and minute
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

        /*
            Creates a new time picker dialog with the specified theme.

                TimePickerDialog(Context context, int themeResId,
                    TimePickerDialog.OnTimeSetListener listener,
                    int hourOfDay, int minute, boolean is24HourView)
         */

            // TimePickerDialog Theme : THEME_DEVICE_DEFAULT_LIGHT
            TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this,hour,minute,false);

            // TimePickerDialog Theme : THEME_DEVICE_DEFAULT_DARK
            TimePickerDialog tpd2 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,hour,minute,false);

            // TimePickerDialog Theme : THEME_HOLO_DARK
            TimePickerDialog tpd3 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_DARK,this,hour,minute,false);

            // TimePickerDialog Theme : THEME_HOLO_LIGHT
            TimePickerDialog tpd4 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,this,hour,minute,false);

            // TimePickerDialog Theme : THEME_TRADITIONAL
            TimePickerDialog tpd5 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_TRADITIONAL,this,hour,minute,false);

            // Return the TimePickerDialog
            return tpd2;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            // Do something with the returned time
            String sMinute = ""+minute;


            if(minute < 10){
                sMinute ="0"+ sMinute;
            }
            mEtJam.setText(hourOfDay + ":" + sMinute+" WIB");
        }
    }


}

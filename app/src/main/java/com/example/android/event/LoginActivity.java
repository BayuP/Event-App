package com.example.android.event;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.event.Model.Users;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.bitmap;
import static android.R.attr.data;

public class LoginActivity extends BaseActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "Login Activity";
    private static final int RC_GOOGLE= 1001;
    private static final int ROLE_PENYEDIA = 2001;
    private static final int ROLE_PESERTA = 2002;

    private int userType;

    private FirebaseAuth mfirebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mFirebaseDatabase;

    private SignInButton mSignBtnPenyedia,mSignBtnPeserta;

    private Button mBtnPenyedia,mBtnPeserta;
    private LinearLayout mLayoutView;
    private RelativeLayout mLayoutPenyedia,mLayoutPeserta;

    public GifImageView gifImageView;
    private TextView mTvPindahPenyedia,mTvPindahPeserta;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mfirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabase.getReference();



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();




        mLayoutView =(LinearLayout) findViewById(R.id.view_atau);
        mLayoutPenyedia =(RelativeLayout) findViewById(R.id.Layout_penyedia);
        mLayoutPeserta = (RelativeLayout) findViewById(R.id.Layout_peserta);

        mTvPindahPenyedia = (TextView)findViewById(R.id.tv_pindahPenyedia);
        mTvPindahPenyedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnPenyedia.setVisibility(View.VISIBLE);
                mBtnPeserta.setVisibility(View.VISIBLE);
                mLayoutView.setVisibility(View.VISIBLE);
                mLayoutPenyedia.setVisibility(View.GONE);
            }
        });
        mTvPindahPeserta = (TextView)findViewById(R.id.tv_pindahPeserta);
        mTvPindahPeserta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnPenyedia.setVisibility(View.VISIBLE);
                mBtnPeserta.setVisibility(View.VISIBLE);
                mLayoutView.setVisibility(View.VISIBLE);
                mLayoutPeserta.setVisibility(View.GONE);
            }
        });

        mBtnPenyedia = (Button) findViewById(R.id.btn_penyedia);
        mBtnPenyedia.setOnClickListener(this);
        mBtnPeserta = (Button) findViewById(R.id.btn_peserta);
        mBtnPeserta.setOnClickListener(this);
        mSignBtnPeserta = (SignInButton) findViewById(R.id.google_signBtnPeserta);
        mSignBtnPeserta.setOnClickListener(this);
        mSignBtnPenyedia = (SignInButton) findViewById(R.id.google_signBtnPenyedia);
        mSignBtnPenyedia.setOnClickListener(this);
    }

    private void signInGoogle() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent, RC_GOOGLE);
    }

    private void rolePenyedia(){
        userType = ROLE_PENYEDIA;
        setLayout(userType);
    }
    private void rolePeserta(){
        userType =ROLE_PESERTA;
        setLayout(userType);
    }

    private void setLayout(int userType){
        showProgressDialog();
        switch (userType){
            case ROLE_PENYEDIA:
                showLoginPenyedia(true);
                hideProgressDialog();
                break;
            case ROLE_PESERTA:
                showLoginPeserta(true);
                hideProgressDialog();
                break;
        }
    }

    private void showLoginPenyedia(boolean a){
        if(a){
            mBtnPenyedia.setVisibility(View.GONE);
            mBtnPeserta.setVisibility(View.GONE);
            mLayoutView.setVisibility(View.GONE);
            mLayoutPenyedia.setVisibility(View.VISIBLE);
        }else{

        }
    }

    private void showLoginPeserta(boolean a){
        if(a){
            mBtnPenyedia.setVisibility(View.GONE);
            mBtnPeserta.setVisibility(View.GONE);
            mLayoutView.setVisibility(View.GONE);
            mLayoutPeserta.setVisibility(View.VISIBLE);
        }else{

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser mUser = mfirebaseAuth.getCurrentUser();
            if (mUser != null) {
                showProgressDialog();
                mDatabaseRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users user = dataSnapshot.child(mUser.getUid()).getValue(Users.class);
                        Log.d(TAG,user.getNama());
                        hideProgressDialog();
                        if(user.Penyedia == false){
                            startActivity(new Intent(getApplicationContext(),PesertaActivity.class));
                            finish();
                        }else if(user.Penyedia == true) {
                            startActivity(new Intent(getApplicationContext(),PenyediaActivity.class));
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acc= result.getSignInAccount();
                firebaseAuthWithGoogle(acc);
            } else {
                Log.e(TAG, "Error sign google");
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mfirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "SignInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        } else {
                            final FirebaseUser mUser = mfirebaseAuth.getCurrentUser();
                            if (userType == ROLE_PENYEDIA) {
                                Users user = new Users(mUser.getPhotoUrl().toString(),mUser.getDisplayName().toString(),true);
                                mDatabaseRef.child("Users").child(getUid()).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(getApplicationContext(),PenyediaActivity.class));
                                            finish();
                                        }
                                    }
                                });

                            } else {
                                Users user = new Users(mUser.getPhotoUrl().toString(), mUser.getDisplayName().toString(),false);
                                mDatabaseRef.child("Users").child(getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       startActivity(new Intent(getApplicationContext(), PesertaActivity.class));
                                       finish();
                                   }
                                    }
                                });

                            }
                        }
                    }


                });}

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_penyedia:
                rolePenyedia();
                break;
            case R.id.btn_peserta:
                rolePeserta();
                break;
            case R.id.google_signBtnPenyedia:
                signInGoogle();
                break;
            case R.id.google_signBtnPeserta:
                signInGoogle();
                break;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

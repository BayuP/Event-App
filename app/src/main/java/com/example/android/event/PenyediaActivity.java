package com.example.android.event;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

public class PenyediaActivity extends BaseActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private Button mBtnLogout;
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton mFloatingAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penyedia);

        mBtnLogout =(Button)findViewById(R.id.btn_penyedia_logout);
        mBtnLogout.setOnClickListener(this);

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

    }



    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_penyedia_logout:
                signOut();
                break;
            case R.id.fa_create:
                startActivity(new Intent(getApplicationContext(),BuatAcaraActivity.class));
    }
}
}

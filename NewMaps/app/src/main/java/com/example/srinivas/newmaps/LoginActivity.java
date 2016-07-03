package com.example.srinivas.newmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Srinivas on 02-07-2016.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_GOOGLE_SIGN_IN = 9;
    private GoogleApiClient mClient;
    private boolean mIdentifying;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions options=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        SignInButton button=(SignInButton)findViewById(R.id.sign_in_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIdentifying) return;
                signIn();
            }
        });
        if(LoginData.isConnected(this)) {
            Intent intent=new Intent(this,LocationMapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void signIn() {
        Intent intent=Auth.GoogleSignInApi.getSignInIntent(mClient);
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("log in", "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()) {
            Toast toast=Toast.makeText(this, "Could not get the required Information", Toast.LENGTH_SHORT);
            GoogleSignInAccount account=result.getSignInAccount();
            String email=account.getEmail();
            String personName = account.getDisplayName();
            String personPhoto = account.getPhotoUrl()!=null?account.getPhotoUrl().toString():"";
            Log.d("Log in ", personName + " " + email);
            if(TextUtils.isEmpty(email)||TextUtils.isEmpty(personName)) {
                toast.show();
            } else {
                LoginData.setUserEmail(this,email);
                LoginData.setUserName(this,personName);
                LoginData.setUserUrl(this,personPhoto);
                LoginData.setConnected(this,true);
                Intent intent=new Intent(this,LocationMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast t= Toast.makeText(this,"Connection Error",Toast.LENGTH_SHORT);
        t.show();
    }
}

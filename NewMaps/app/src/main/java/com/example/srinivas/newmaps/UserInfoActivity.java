package com.example.srinivas.newmaps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Srinivas on 26-05-2016.
 */
public class UserInfoActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView userName=(TextView) findViewById(R.id.text_user_name);
        TextView email=(TextView) findViewById(R.id.text_email);
        ImageView image=(ImageView) findViewById(R.id.image_user);
        Button logout=(Button) findViewById(R.id.button_log_out);
        setUpGoogleApi();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                } else {
                    doOnConnect();
                }
            }
        });
        userName.setText(LoginData.getUserName(this));
        email.setText(LoginData.getUserEmail(this));
        new UserImageDownloadTask(image).execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    private void setUpGoogleApi() {
        GoogleSignInOptions options=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(UserInfoActivity.this,"Connection failed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        doOnConnect();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Toast.makeText(UserInfoActivity.this,"Connection suspended",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

    private void doOnConnect() {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        resetData();
                    } else {
                        Toast.makeText(UserInfoActivity.this, "logout failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void resetUserDetails() {
        LoginData.setConnected(this,false);
        LoginData.setUserUrl(this,"");
        LoginData.setUserName(this,"");
        LoginData.setUserEmail(this,"");
        SettingsPref.setLocationUpdateStarted(this,false);
        SettingsPref.setLocationStored(this,true);
        new LocationDatabaseHelper(this).deleteLocations();
    }

    private void resetData() {
        if(SettingsPref.isLocationUpdateStarted(this)) {
            final GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText(UserInfoActivity.this, "Sign out failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build();
            apiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    removeLocationUpdates(apiClient);
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            });
            apiClient.connect();
        } else {
            resetUserDetails();
            Intent intent=new Intent(UserInfoActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.i("Logout","success");
            finish();
        }
    }


    private void removeLocationUpdates(final GoogleApiClient apiClient) {
        Intent i = new Intent(UserInfoActivity.this,FusedLocationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(UserInfoActivity.this, 0, i, 0);
        final com.google.android.gms.common.api.PendingResult<Status> result=
                LocationServices.FusedLocationApi.removeLocationUpdates(apiClient,pi);
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void[] params) {
                com.google.android.gms.common.api.Status status=result.await();
                if (status.isSuccess()) {
                    resetUserDetails();
                    Intent intent=new Intent(UserInfoActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Log.i("Logout","success");
                    finish();
                } else {
                    Log.i("Logout","fail");
                }
                return null;
            }
        }.execute();
    }

    private static File fecthUserImage(Context context,String userImage) {
        HttpURLConnection connection = null;
        BufferedInputStream input = null;
        FileOutputStream stream=null;
        try {
            Log.i("Network", "user info");
            URL url = new URL(userImage);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            Log.i("response code", connection.getResponseCode() + "");
            //connection.setDoInput(true);
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                input = new BufferedInputStream(connection.getInputStream());
                File f=getUserFileName(context);
                stream=new FileOutputStream(f);
                Log.i("response code", connection.getResponseCode() + "");
                Log.i("String", (input != null) ? input + " is there" : "input null");
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = input.read()) != -1) {
                    stream.write(ch);
                }
                if(f.exists()&& f.length()>0) {
                    return f;
                }
                Log.i("Val", "accepted"+builder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {

                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {

                }
            }
        }
        Log.i("Val", "failed");
        return null;
    }


    public static File getUserFileName(Context context) {
        File f=new File(context.getExternalFilesDir(null),"user_image.jpg");
        return f;
    }

    public class UserImageDownloadTask extends AsyncTask<Void,Void,Void> {

        private ImageView imageView;

        public UserImageDownloadTask(ImageView view) {
            this.imageView=view;
        }

        @Override
        protected Void doInBackground(Void[] params) {
            File f=getUserFileName(UserInfoActivity.this);
            Log.i("User_info", "user info");
            if(!f.exists()) {
                Log.i("User_info", "image not");
                String userImageUrl=LoginData.getUserUrl(UserInfoActivity.this);
                Log.i("User_info", "image url "+userImageUrl);
                if(TextUtils.isEmpty(userImageUrl)) return null;
                File image=fecthUserImage(UserInfoActivity.this,userImageUrl);
                if(image==null) return null;
                final Bitmap bitmap=ImageUtils.getRoundedBitmap(image.getPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                        Log.i("User_info", "image set");
                    }
                });
                return null;
            } else {
                Log.i("User_info", "image present");
                final Bitmap bitmap=ImageUtils.getRoundedBitmap(f.getPath());;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                        Log.i("User_info", "image set");
                    }
                });
            }
            return null;
        }
    }

}

package com.paulfoleyblogs.paul.homeseccontrol;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView, resultEmail;
    private EditText mPasswordView, resultPassword;
    private View mProgressView;
    private View mLoginFormView;
    RequestQueue requestQueue;
    DatabaseHelper myDb;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //create a Database
        myDb = new DatabaseHelper(this);
        //inserting raw data into database to be used for login
        myDb.insertData("Paul", "Foley", "pfoley@hotmail.com", "holyfoley");
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (mEmailSignInButton != null) {
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {


                    Cursor res = myDb.loginData();
                    String[] names = res.getColumnNames();

                    if(res.getCount()==0) {
                        Toast.makeText(LoginActivity.this, "No data was found", Toast.LENGTH_LONG).show();
                    }
                    //move pointer to the first row
                    res.moveToFirst();

                    //returns the elements from the first and second cell returned
                    String emailCheck = res.getString(0);
                    String passCheck = res.getString(1);


                    if(emailCheck.equals(mEmailView.getText().toString()))
                    {

                        if (passCheck.equals(mPasswordView.getText().toString()))
                        {
                            Intent myIntent = new Intent(LoginActivity.this, home.class);
                            LoginActivity.this.startActivity(myIntent);
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Password Not the same", Toast.LENGTH_LONG).show();

                    }
                    else
                        Toast.makeText(LoginActivity.this, "Email not the same", Toast.LENGTH_LONG).show();



                    res.close();
                }
            });
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Login Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.paulfoleyblogs.paul.homeseccontrol/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Login Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.paulfoleyblogs.paul.homeseccontrol/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}



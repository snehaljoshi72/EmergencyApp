package com.example.snehaljoshi.locationplay;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText SSN;
    private EditText pass;
    private EditText repass;
    private Button signup;
    protected Location currentLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Emergency");
        name = (EditText) findViewById(R.id.tv_name);
        email = (EditText) findViewById(R.id.tv_email);
        phone = (EditText) findViewById(R.id.tv_phone);
        SSN= (EditText) findViewById(R.id.tv_ssn);
        signup = (Button) findViewById(R.id.bt_signup);
        pass = (EditText) findViewById(R.id.tv_pass);
        repass = (EditText) findViewById(R.id.tv_repass);
        Bundle extras = getIntent().getExtras();
        currentLocation = extras.getParcelable("currentlocation");
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sighnup();}
        });
    }

    private void sighnup() {
        String username = email.getText().toString();
        String password = pass.getText().toString();
        String repassword = repass.getText().toString();
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder("Error of CODE");
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append("Blank Uname");
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append("Paasword Cant be Blank");
            }
            validationError = true;
            validationErrorMessage.append("Paasword Cant be Blank");
        }
        if (!password.equals(repassword)) {
            if (validationError) {
                validationErrorMessage.append("Password dont match");
            }
            validationError = true;
            validationErrorMessage.append("Missmatch Password");
        }
        validationErrorMessage.append("HEY DONE TO SIGNUP");
        if (validationError) {
            Toast.makeText(SignupActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }
        ParseUser user = new ParseUser();           // this create new user in db with name , phone, ssn, email and current location of user
        user.setUsername(username);
        user.setPassword(password);
        user.put("name",name.getText().toString());
        user.put("phone", phone.getText().toString());
        user.put("SSN", SSN.getText().toString());
        if(currentLocation!=null){
            ParseGeoPoint loc = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
            user.put("location",loc);
           // Log.i("My app","Currentlocation is not null");
        }
        else{
            Log.i("my app", "Currentlocation is null");
        }
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // Handle the response
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SignupActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity      // after success full sign up it redirect to login activity
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("currentlocation",currentLocation);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}

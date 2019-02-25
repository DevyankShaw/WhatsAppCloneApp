package com.example.devyankshaw.whatsappcloneapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edtEmailSignUp, edtUsernameSignUp, edtPasswordSignUp;
    private Button btnSignUpS, btnLogInS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground();

        edtEmailSignUp = findViewById(R.id.edtEmailSingUp);
        edtUsernameSignUp = findViewById(R.id.edtUsernameSignUp);
        edtPasswordSignUp = findViewById(R.id.edtPasswordSignUp);

        //Making the enter button of the mobile keyboard to be the signUp button i.e executes the task of signUp button
        edtPasswordSignUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                /*If the user presses the enter key and in order to make sure that the user
                      has really pressed the enter key then this if block executes*/
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

                    onClick(btnSignUpS);//Execute the onclick() where the view that is tapped is the btnSignUp Button
                }
                return false;
            }
        });

        btnSignUpS = findViewById(R.id.btnSignUpS);
        btnLogInS = findViewById(R.id.btnLogInS);

        btnSignUpS.setOnClickListener(MainActivity.this);
        btnLogInS.setOnClickListener(MainActivity.this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignUpS:

                if (edtEmailSignUp.getText().toString().equals("") || edtUsernameSignUp.getText().toString().equals("") ||
                        edtPasswordSignUp.getText().toString().equals("")){
                    FancyToast.makeText(MainActivity.this,  "Email, Username, Password is required",
                            FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                }else {
                    //Storing the credentials to the server
                    final ParseUser parseUser = new ParseUser();
                    parseUser.setUsername(edtUsernameSignUp.getText().toString());
                    parseUser.setPassword(edtPasswordSignUp.getText().toString());
                    parseUser.setEmail(edtEmailSignUp.getText().toString());

                    //Setting up progress while signing process is going on
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Signing up " + edtUsernameSignUp.getText().toString());
                    progressDialog.show();

                    //Signing process going to be executed
                    parseUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                FancyToast.makeText(MainActivity.this,
                                        "Sign up successful", FancyToast.LENGTH_LONG,
                                        FancyToast.SUCCESS, true).show();
                                finish();
                                ParseUser.getCurrentUser().logOut();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class).putExtra("from", "activity1");
                                startActivity(intent);
                            } else {
                                FancyToast.makeText(MainActivity.this, e.getMessage(),
                                        FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

                            }
                            //Dismiss the dialog
                            progressDialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.btnLogInS:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void rootlayoutTapped(View view){


        try{
            //Hides the mobile keyboard whenever tapped on the blank/empty screen space
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

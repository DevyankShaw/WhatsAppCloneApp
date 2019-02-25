package com.example.devyankshaw.whatsappcloneapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmailLogIn, edtPasswordLogIn;
    private Button btnSignUpL, btnLogInL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmailLogIn = findViewById(R.id.edtEmailLogIn);
        edtPasswordLogIn = findViewById(R.id.edtPasswordLogIn);

        //Making the enter button of the mobile keyboard to be the logIn button i.e executes the task of logIn button
        edtPasswordLogIn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                /*If the user presses the enter key and in order to make sure that the user
                      has really pressed the enter key then this if block executes*/
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {

                    onClick(btnLogInL);//Execute the onclick() where the view that is tapped is the btnSignUp Button
                }
                return false;
            }
        });

        btnLogInL = findViewById(R.id.btnLogInL);
        btnSignUpL = findViewById(R.id.btnSignUpL);

        btnLogInL.setOnClickListener(LoginActivity.this);
        btnSignUpL.setOnClickListener(LoginActivity.this);


        //Close the session
        if(ParseUser.getCurrentUser() != null ){
            //ParseUser.getCurrentUser().logOut();
            transitionToSocialMediaActivity();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogInL:

                if (edtEmailLogIn.getText().toString().equals("") || edtPasswordLogIn.getText().toString().equals("")) {
                    FancyToast.makeText(LoginActivity.this, "Email, Password is required",
                            FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();


                } else {
                    //Setting up progress for Login
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Logging in " + edtEmailLogIn.getText().toString());
                    progressDialog.show();

                    //Login process is being executed
                    ParseUser.logInInBackground(edtEmailLogIn.getText().toString(), edtPasswordLogIn.getText().toString(),
                            new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException ex) {
                                    if (user != null && ex == null) {
                                        transitionToSocialMediaActivity();
                                        FancyToast.makeText(LoginActivity.this, user.get("username") + " is logged in successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                                    } else {
                                        FancyToast.makeText(LoginActivity.this, ex.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
                break;
            case R.id.btnSignUpL:
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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

    private void transitionToSocialMediaActivity(){

        Intent intent = new Intent(LoginActivity.this, WhatsAppUsers.class);
        startActivity(intent);
        finish();
    }
}

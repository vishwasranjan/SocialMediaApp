package com.example.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText edtEmail,edtUsername,edtPassword;
    Button signin,signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtEmail=findViewById(R.id.edtEmail);
        edtUsername=findViewById(R.id.edtUsername);
        edtPassword=findViewById(R.id.edtPassword);
        signin=findViewById(R.id.signin);
        signup=findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signuptouser();

            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signintouser();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();
        if (currentuser!=null)
        {
            //transition to different activity
            transitiontosocialmediaactiviy();
        }
        else
        {

        }
    }
    public void signuptouser()
    {
        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Sign up is Suscessful", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("my_user")
                                    .child(task.getResult().getUser().getUid()).child("Username")
                                    .setValue(edtUsername.getText().toString());
                            transitiontosocialmediaactiviy();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Sign up is Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void signintouser()
    {
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Sign in is Suscessful", Toast.LENGTH_SHORT).show();
                    transitiontosocialmediaactiviy();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Sign in is Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void transitiontosocialmediaactiviy()
    {
        Intent intent=new Intent(this,SocialMediaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
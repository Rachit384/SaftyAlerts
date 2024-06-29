package com.example.saftyalerts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private EditText email, pwd;
    private Button proceed;
    private ProgressBar prog;
    private FirebaseAuth mAuth;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);



        email = findViewById(R.id.Email);
        pwd = findViewById(R.id.Password);
        proceed = findViewById(R.id.proceed);
        prog = findViewById(R.id.prgrss);
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = pwd.getText().toString();

                if (TextUtils.isEmpty(emailText)) {
                    email.setError("This field is required");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    email.setError("Enter a valid email");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(passwordText)) {
                    pwd.setError("Enter a password");
                    pwd.requestFocus();
                } else if (passwordText.length() < 6) {
                    pwd.setError("Password is too weak");
                    pwd.requestFocus();
                } else {
                    signup(emailText, passwordText);
                }
            }
        });








        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void signup(String emailText, String passwordText) {
        prog.setVisibility(View.VISIBLE);
        proceed.setVisibility(View.GONE);

        mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                prog.setVisibility(View.GONE);
                proceed.setVisibility(View.VISIBLE);

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user != null ? user.getUid() : "";

                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SignUp.this, UserName.class);
                   // intent.putExtra("uid", uid);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Connection Time Out", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
package com.example.saftyalerts;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class LogIn extends AppCompatActivity {

    EditText Email, pwd;
    ProgressBar prog;
    TextView forgot;
    Button next;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);






        Email = findViewById(R.id.eemaail);
        pwd = findViewById(R.id.Password);
        prog = findViewById(R.id.prgrss);
        next = findViewById(R.id.Login);
        forgot = findViewById(R.id.forgot);
        mAuth = FirebaseAuth.getInstance();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = Email.getText().toString();
                String passwordText = pwd.getText().toString();

                if (TextUtils.isEmpty(emailText)) {
                    Email.setError("This field is required");
                    Email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    Email.setError("Enter a valid email");
                    Email.requestFocus();
                } else if (TextUtils.isEmpty(passwordText)) {
                    pwd.setError("Enter a password");
                    pwd.requestFocus();
                } else if (passwordText.length() < 6) {
                    pwd.setError("Password is too weak");
                    pwd.requestFocus();
                } else {
                    signin(emailText, passwordText);
                }
            }
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void signin(String emailText, String passwordText) {
        prog.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);

        mAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                prog.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);

                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user != null ? user.getUid() : "";

                    Intent intent = new Intent(LogIn.this, UserName.class);
                    //intent.putExtra("uid", uid);
                    startActivity(intent);
                    finish(); // Optional: Call finish() if you want to close the LoginActivity
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Email.setText("");
                    pwd.setText("");
                    Toast.makeText(LogIn.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
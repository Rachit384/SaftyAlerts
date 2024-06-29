package com.example.saftyalerts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Welcome extends AppCompatActivity {

    Button signup,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);



        signup=findViewById(R.id.Signupbutton);
        login=findViewById(R.id.loginbutton);

        Intent i = new Intent(Welcome.this, bottombutton.class);
        if(FirebaseUtil.isLoggedin()){
            startActivity(i);
            onBackPressed();
        }else{
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Welcome.this, SignUp.class);
                    startActivity(intent);
                    onBackPressed();
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Welcome.this, LogIn.class);
                    startActivity(intent);
                    onBackPressed();
                }
            });
        }

















        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @Override
    public void onBackPressed() {
        // Disable back button or handle it as per your requirement
         super.onBackPressed(); // Uncomment this line to allow back navigation
    }
}
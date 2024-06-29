
package com.example.saftyalerts;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.saftyalerts.Model.usermodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UserName extends AppCompatActivity {

    private EditText usernameEditText;
    private Button letMeInButton, uploadButton;
    private ImageView imageView;
    private ProgressBar progressBar;

    private Uri imageUri;
    private Bitmap selectedImageBitmap;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;
    private StorageReference profileImagesRef;
    private static final int PICK_IMAGE_REQUEST = 1;

    private boolean isNewUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_name);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child("users");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        profileImagesRef = storage.getReference().child("profile_images");

        usernameEditText = findViewById(R.id.User);
        letMeInButton = findViewById(R.id.userproceed);
        imageView = findViewById(R.id.imagenum);
        progressBar = findViewById(R.id.userprgrss);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        letMeInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        // Load user profile data if available
        loadUserProfileData();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    private void saveProfile() {
        final String username = usernameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required.");
            usernameEditText.requestFocus();
            return;
        }

        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            StorageReference fileRef = profileImagesRef.child(currentUser.getUid() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    saveUserProfile(username, uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UserName.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (!isNewUser) {
            // Allow proceeding without updating the image if it's an existing user
            saveUserProfile(username, null);
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserProfile(String username, String profileImageUrl) {
        usermodel userModel;
        if (profileImageUrl == null) {
            usersRef.child(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    usermodel user = dataSnapshot.getValue(usermodel.class);
                    String existingImageUrl = (user != null) ? user.getProfileImageUrl() : null;
                    usermodel updatedUserModel = new usermodel(username, existingImageUrl);
                    saveToDatabase(updatedUserModel);
                }
            });
        } else {
            userModel = new usermodel(username, profileImageUrl);
            saveToDatabase(userModel);
        }
    }

    private void saveToDatabase(usermodel userModel) {
        usersRef.child(currentUser.getUid()).setValue(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UserName.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserName.this, bottombutton.class);
                        usermodel u = new usermodel();
                        intent.putExtra("USERNAME_KEY", u.getUsername());
                        startActivity(intent);
                        finish();  // Optional: close the current activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UserName.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserProfileData() {
        usersRef.child(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usermodel user = dataSnapshot.getValue(usermodel.class);
                    if (user != null) {
                        isNewUser = false;
                        usernameEditText.setText(user.getUsername());
                        if (user.getProfileImageUrl() != null) {
                            RequestOptions requestOptions = new RequestOptions()
                                    .placeholder(R.drawable.place) // Replace with your placeholder image
                                    .error(R.drawable.error)       // Replace with your error image
                                    .diskCacheStrategy(DiskCacheStrategy.ALL);

                            Glide.with(UserName.this)
                                    .load(user.getProfileImageUrl())
                                    .apply(requestOptions)
                                    .into(imageView);
                        }
                    }
                } else {

                    isNewUser = true;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error retrieving user data: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

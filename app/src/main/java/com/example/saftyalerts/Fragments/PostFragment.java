package com.example.saftyalerts.Fragments;

import static android.text.style.TtsSpan.ARG_USERNAME;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.saftyalerts.Model.Post;
import com.example.saftyalerts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostFragment extends Fragment {

    private EditText postText;
    private ImageView postImage;
    private Button postButton;
    private Uri imageUri;
    private DatabaseReference postsReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
private String fcmToken;
    private static final int PICK_IMAGE_REQUEST = 1;





    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        // Initialize Firebase
        postsReference = FirebaseDatabase.getInstance().getReference().child("posts");
        storageReference = FirebaseStorage.getInstance().getReference().child("post_images");
        auth = FirebaseAuth.getInstance();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        postText = view.findViewById(R.id.postText);
        postImage = view.findViewById(R.id.postImage);
        postButton = view.findViewById(R.id.postButton);

        postImage.setOnClickListener(v -> openFileChooser());

        postButton.setOnClickListener(v -> uploadPost());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            postImage.setImageURI(imageUri);
        }
    }

    private void uploadPost() {
        String text = postText.getText().toString().trim();
        if (text.isEmpty() && imageUri == null) {
            Toast.makeText(getActivity(), "Please enter text or choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("username").getValue(String.class);
                    String userProfileImage = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    // Log the values to debug
                    Log.d("FirebaseDebug", "Username: " + userName);
                    Log.d("FirebaseDebug", "Profile Image URL: " + userProfileImage);

                    // Display Toast messages to verify if userName and userProfileImage are retrieved
                    if (userName != null && userProfileImage != null) {
                        Toast.makeText(getActivity(), "Username: " + userName + "\nProfile Image URL: " + userProfileImage, Toast.LENGTH_LONG).show();

                        if (imageUri != null) {
                            uploadImageAndPost(userId, userName, userProfileImage, text);
                        } else {
                            createPost(userId, userName, userProfileImage, text, null);
                        }
                    } else {
                        Log.e("FirebaseDebug", "Username or Profile Image URL is null");
                        Toast.makeText(getActivity(), "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("FirebaseDebug", "User data does not exist");
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("FirebaseDebug", "DatabaseError: " + databaseError.getMessage());
                Toast.makeText(getActivity(), "Failed to read user data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadImageAndPost(String userId, String userName, String userProfileImage, String text) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference()
                .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    createPost(userId, userName, userProfileImage, text, imageUrl);
                }))
                .addOnFailureListener(e -> {
                    Log.e("FirebaseDebug", "Error uploading image: " + e.getMessage());
                    Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to get file extension


    private void createPost(String userId, String userName, String userProfileImage, String text, String imageUrl) {
        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference().child("posts");
        String postId = postsReference.push().getKey();

        Post newPost = new Post(userId, userName, userProfileImage, text, imageUrl);

        postsReference.child(postId).setValue(newPost).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseDebug", "Post created successfully");
                Toast.makeText(getActivity(), "Post created", Toast.LENGTH_SHORT).show();
                postText.setText(""); // Assuming postText is your EditText for post text
                postImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.place)); // Assuming imageView is your ImageView for the image
                sendNotificationToUsers(newPost);
            } else {
                Log.e("FirebaseDebug", "Error creating post: " + task.getException().getMessage());
                Toast.makeText(getActivity(), "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotificationToUsers(Post post) {
        // Use Firebase Cloud Messaging to send notification
       // getFCMToken();
        DatabaseReference tokensReference = FirebaseDatabase.getInstance().getReference().child("tokens");
        tokensReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String token = snapshot.getValue(String.class);
                    // Construct and send notification using FCM server key
                    sendFCMNotification(token, post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void sendFCMNotification(String token, Post post) {
        // Create notification payload
        JSONObject payload = new JSONObject();
        try {
            payload.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", "New Post Alert");
            notification.put("body", post.getuser() + " posted: " + post.getText());
            payload.put("notification", notification);

            // Send the notification using FCM API
            String url = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                    response -> {
                        // Handle success
                    },
                    error -> {
                        // Handle error
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "AIzaSyDkI9M7QaMUZjcLFway8ywp83X9vUVKHCg");
                    return headers;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
/*
   public void getFCMToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    Log.i("Mytoken",token);
                    fcmToken=token;
                }
            }
        });
    }*/
}

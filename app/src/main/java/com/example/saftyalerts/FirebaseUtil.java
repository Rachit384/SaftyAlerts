package com.example.saftyalerts;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedin(){
        if(currentUserId()!=null){
            return true;
        }else{
            return false;
        }
    }

    public static DocumentReference currentUserDetails() {
        String userId = currentUserId();
        if (userId != null) {
            return FirebaseFirestore.getInstance().collection("users").document(userId);
        } else {
            return null; // Handle this case appropriately in your activity
        }
    }
}


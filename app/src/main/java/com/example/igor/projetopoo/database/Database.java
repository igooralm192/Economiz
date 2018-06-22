package com.example.igor.projetopoo.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Database implements DatabaseRequests {
    private static FirebaseFirestore firestore;

    public static void initFirebase() {
        Database.firestore = FirebaseFirestore.getInstance();
    }
}

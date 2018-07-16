package com.example.igor.projetopoo.database;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Database implements DatabaseRequests {
    private FirebaseFirestore firestore;

    public Database(FirebaseFirestore firestore) { this.firestore = firestore; }

    public FirebaseFirestore getFirestore() { return this.firestore; }

    public Task<DocumentSnapshot> getDocument(DocumentReference documentReference) {
        Task<DocumentSnapshot> task = documentReference.get();

        while (!task.isSuccessful()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

    public Task<QuerySnapshot> getDocuments(CollectionReference collectionReference) {
        Task<QuerySnapshot> task = collectionReference.get();

        while (!task.isSuccessful()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

    public Task<QuerySnapshot> getDocuments(Query query) {
        Task<QuerySnapshot> task = query.get();

        while (!task.isSuccessful()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

    public Task<DocumentReference> addDocument(CollectionReference collectionReference, Object object) {
        Task<DocumentReference> task = collectionReference.add(object);

        while (!task.isSuccessful()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

    public Task<Void> updateDocument(DocumentReference documentReference, Object object) {
        Task<Void> task = documentReference.set(object);

        while (!task.isSuccessful()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

    public Task<Void> deleteDocument(DocumentReference documentReference) {
        Task<Void> task = documentReference.delete();

        while (!task.isSuccessful()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

}
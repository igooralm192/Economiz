package com.example.igor.projetopoo.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

// Classe para execução das operações no firebase

public class Database implements DatabaseRequests {
    private FirebaseFirestore firestore;

    public Database(FirebaseFirestore firestore) { this.firestore = firestore; }

    public FirebaseFirestore getFirestore() { return this.firestore; }

    public Task<DocumentSnapshot> getDocument(DocumentReference documentReference) {
        Task<DocumentSnapshot> task = documentReference.get();

        while (!task.isComplete()) {
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

        while (!task.isComplete()) {
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

        while (!task.isComplete()) {
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

        while (!task.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

    public Task<Void> updateDocument(DocumentReference documentReference, Map<String, Object> map) {
        Task<Void> task = documentReference.set(map);

        while (!task.isComplete()) {
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

        while (!task.isComplete()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return task;
    }

}

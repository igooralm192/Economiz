package com.example.igor.projetopoo.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface DatabaseRequests {
    Task<DocumentSnapshot> getDocument(DocumentReference documentReference);
    Task<QuerySnapshot> getDocuments(CollectionReference collectionReference);
    Task<QuerySnapshot> getDocuments(Query query);
    Task<DocumentReference> addDocument(CollectionReference collectionReference, Object object);
    Task<Void> updateDocument(DocumentReference documentReference, Object object);
    Task<Void> deleteDocument(DocumentReference documentReference);
}

package com.example.igor.projetopoo.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public interface DatabaseRequests {
    DocumentSnapshot getDocument(DocumentReference documentReference);
    QuerySnapshot getDocuments(CollectionReference collectionReference);
    QuerySnapshot getDocuments(Query query);
}

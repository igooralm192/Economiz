package com.example.igor.projetopoo.activity.product;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.igor.projetopoo.entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductModel implements ProductMVP.ModelOps {
    private ProductMVP.ReqPresenterOps reqPresenterOps;
    private Database database;

    public ProductModel (ProductMVP.ReqPresenterOps reqPresenterOps, Database database) {
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    @Override
    public void insertFeedback(Feedback feedback) {

        FirebaseFirestore firestore = database.getFirestore();
        Task<DocumentReference> task = database.addDocument(firestore.collection("feedbacks"),feedback);
        reqPresenterOps.onFeedbackInserted();
    }

    @Override
    public void deleteFeedback(Feedback feedback) {
        FirebaseFirestore firestore = database.getFirestore();

    }
}

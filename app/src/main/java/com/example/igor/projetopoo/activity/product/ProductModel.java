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
    private Task<DocumentReference> task;

    public ProductModel (ProductMVP.ReqPresenterOps reqPresenterOps, Database database) {
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    @Override
    public void feedbackListRequest(String productName) {
        List<Object> objects = new ArrayList<>();

        FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        collectionReference = firestore.collection("feedbacks");

        Query query = collectionReference.whereEqualTo("product", productName);
        Task<QuerySnapshot> querySnapshot = database.getDocuments(query);

        for (DocumentSnapshot documentSnapshot: querySnapshot.getResult()) {
            Map<String, Object> data = documentSnapshot.getData();

            Feedback feedback = new Feedback(data);
            objects.add(feedback);
        }

        reqPresenterOps.onReturnedFeedbackList(objects);
    }

    @Override
    public void insertFeedback(Feedback feedback) {
        FirebaseFirestore firestore = database.getFirestore();
        task = database.addDocument(firestore.collection("feedbacks"),feedback);
        reqPresenterOps.onFeedbackInserted();
    }

    @Override
    public void deleteFeedback() {
        database.deleteDocument(task.getResult());
        reqPresenterOps.onFeedbackDeleted();
    }
}

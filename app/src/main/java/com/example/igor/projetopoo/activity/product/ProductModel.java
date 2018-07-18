package com.example.igor.projetopoo.activity.product;

import android.support.constraint.ConstraintLayout;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;
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
    private ProductActivity activity;
    private ProductMVP.ReqPresenterOps reqPresenterOps;
    private Database database;
    private Task<DocumentReference> task;

    public ProductModel (ProductActivity activity, ProductMVP.ReqPresenterOps reqPresenterOps, Database database) {
        this.activity = activity;
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    @Override
    public void feedbackListRequest(String productName) throws ConnectionException, DatabaseException {
        final ConstraintLayout layout = activity.findViewById(R.id.container_product);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeAllViews();
            }
        });

        if (!ParentActivity.checkConnection(activity)) throw new ConnectionException(activity, layout);

        List<Object> objects = new ArrayList<>();

        FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        collectionReference = firestore.collection("feedbacks");

        Query query = collectionReference.whereEqualTo("product", productName);
        Task<QuerySnapshot> querySnapshot = database.getDocuments(query);
        if (!querySnapshot.isSuccessful()) throw new DatabaseException(activity);

        for (DocumentSnapshot documentSnapshot: querySnapshot.getResult()) {
            Map<String, Object> data = documentSnapshot.getData();

            Feedback feedback = new Feedback(data);
            objects.add(feedback);
        }

        reqPresenterOps.onReturnedFeedbackList(objects);
    }

    @Override
    public void insertFeedback(Feedback feedback) throws DatabaseException {
        FirebaseFirestore firestore = database.getFirestore();
        task = database.addDocument(firestore.collection("feedbacks"),feedback);
        if (!task.isSuccessful()) throw new DatabaseException(activity);
        reqPresenterOps.onFeedbackInserted();
    }

    @Override
    public void deleteFeedback() throws DatabaseException {
        database.deleteDocument(task.getResult());
        if (!task.isSuccessful()) throw new DatabaseException(activity);
        reqPresenterOps.onFeedbackDeleted();
    }
}

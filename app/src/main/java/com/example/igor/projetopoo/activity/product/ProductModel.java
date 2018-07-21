package com.example.igor.projetopoo.activity.product;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.util.Log;

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
    public void feedbackListRequest(Product product) throws ConnectionException, DatabaseException {
        final ConstraintLayout layout = activity.findViewById(R.id.container_product);

        final AppBarLayout appbar = activity.findViewById(R.id.appbar);
        final boolean hasConnectivity = ParentActivity.checkConnection(activity);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeAllViews();
                appbar.setExpanded(hasConnectivity);
            }
        });

        if (!hasConnectivity) {
            throw new ConnectionException(activity, layout);
        }

        List<Object> objects = new ArrayList<>();

        FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference = firestore.collection("feedbacks");
        Query query = collectionReference.whereEqualTo("product", product.getName());
        Task<QuerySnapshot> querySnapshot = database.getDocuments(query);
        if (!querySnapshot.isSuccessful()) throw new DatabaseException(activity);

        for (DocumentSnapshot documentSnapshot: querySnapshot.getResult()) {
            Map<String, Object> data = documentSnapshot.getData();

            if (data != null) {
                Feedback feedback = new Feedback(data);
                objects.add(feedback);
            }
        }

        reqPresenterOps.onReturnedFeedbackList(objects, product);
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

    @Override
    public void refreshProduct(Product product) throws DatabaseException {
        FirebaseFirestore firestore = database.getFirestore();
        Map<String, Object> map = product.toMap();
        Task<Void> task = database.updateDocument(firestore.collection("products").document(product.getId()), map);
        if (!task.isSuccessful()) throw new DatabaseException(activity);
    }
}

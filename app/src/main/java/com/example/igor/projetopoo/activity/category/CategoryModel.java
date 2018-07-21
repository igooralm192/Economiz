package com.example.igor.projetopoo.activity.category;

import android.support.constraint.ConstraintLayout;
import android.util.Log;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryModel implements CategoryMVP.ModelOps {
    private CategoryActivity activity;
    private CategoryMVP.ReqPresenterOps reqPresenterOps;
    private Database database;

    public CategoryModel(CategoryActivity activity, CategoryMVP.ReqPresenterOps reqPresenterOps, Database database) {
        this.activity = activity;
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    @Override
    public void categoryRequest(final Category category) throws ConnectionException, DatabaseException {
        final ConstraintLayout layout = activity.findViewById(R.id.container_category);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeAllViews();
            }
        });

        if (!ParentActivity.checkConnection(activity)) throw new ConnectionException(activity, layout);

        List<Object> objects = new ArrayList<>();
        List<QuerySnapshot> querySnapshotList = new ArrayList<>();

        FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        Query categoryQuery = firestore.collection("categories").whereEqualTo("parent_category", category.getName());
        Task<QuerySnapshot> categoryTask = database.getDocuments(categoryQuery);
        if (!categoryTask.isSuccessful()) throw new DatabaseException(activity);
        querySnapshotList.add(categoryTask.getResult());

        Query productQuery = firestore.collection("products").whereEqualTo("parent_category", category.getName());
        Task<QuerySnapshot> productTask = database.getDocuments(productQuery);
        if (!productTask.isSuccessful()) throw new DatabaseException(activity);
        querySnapshotList.add(productTask.getResult());

        for (QuerySnapshot querySnapshot: querySnapshotList) {
            for (DocumentSnapshot documentSnapshot: querySnapshot) {
                String path = documentSnapshot.getReference().getPath().split("/")[0];
                Map<String, Object> data = documentSnapshot.getData();

                if (path.equals("categories")) {
                    if (data != null) {
                        Category subcategory = new Category(data);
                        objects.add(subcategory);
                    }
                } else {
                    if (data != null) {
                        Product product = new Product(documentSnapshot.getId(), data);
                        objects.add(product);
                    }
                }
            }
        }

        reqPresenterOps.onReturnedCategory(objects);
    }
}
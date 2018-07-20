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

        FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        if (category.getHaveSubcategories()) collectionReference = firestore.collection("categories");
        else collectionReference = firestore.collection("products");

        Query query = collectionReference.whereEqualTo("parent_category", category.getName());
        Task<QuerySnapshot> task = database.getDocuments(query);
        if (!task.isSuccessful()) throw new DatabaseException(activity);

        for (DocumentSnapshot documentSnapshot: task.getResult()) {
            Map<String, Object> data = documentSnapshot.getData();

            if (collectionReference.getId().equals("categories")) {
                if (data != null) {
                    Category subcategory = new Category(data);
                    objects.add(subcategory);
                }
            } else {
                if (data != null) {
                    Product product = new Product(data);
                    objects.add(product);
                }
            }
        }

        reqPresenterOps.onReturnedCategory(collectionReference.getId(), objects);
    }
}
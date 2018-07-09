package com.example.igor.projetopoo.activity.category;

import android.util.Log;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
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
    private CategoryMVP.ReqPresenterOps reqPresenterOps;
    private Database database;

    public CategoryModel(CategoryMVP.ReqPresenterOps reqPresenterOps, Database database) {
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    @Override
    public void categoryRequest(final Category category) {
        List<Object> objects = new ArrayList<>();

        FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        if (category.getHaveSubcategories()) collectionReference = firestore.collection("categories");
        else collectionReference = firestore.collection("products");

        Query query = collectionReference.whereEqualTo("parent_category", category.getName());
        Task<QuerySnapshot> task = database.getDocuments(query);

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

    @Override
    public void suggestionsRequest() {
        List<Object> objects = new ArrayList<>();
        final List<QuerySnapshot> querySnapshotList = new ArrayList<>();
        final FirebaseFirestore firestore = database.getFirestore();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Query categoryQuery = firestore.collection("categories").orderBy("name");
                Task<QuerySnapshot> categoryTask = database.getDocuments(categoryQuery);
                querySnapshotList.add(categoryTask.getResult());
            }
        }).start();

        Query productQuery = firestore.collection("products").orderBy("name");
        Task<QuerySnapshot> productTask = database.getDocuments(productQuery);
        querySnapshotList.add(productTask.getResult());

        while (querySnapshotList.size() != 2);

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
                        Product product = new Product(data);
                        objects.add(product);
                    }
                }
            }
        }

        reqPresenterOps.onReturnedAllSuggestions(objects);
    }
}
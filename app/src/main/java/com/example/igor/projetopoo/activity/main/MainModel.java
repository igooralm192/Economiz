package com.example.igor.projetopoo.activity.main;

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

public class MainModel implements MainMVP.ModelOps {
    private MainMVP.ReqPresenterOps reqPresenterOps;
    private Database database;

    public MainModel (MainMVP.ReqPresenterOps reqPresenterOps, Database database) {
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    @Override
    public void categoryListRequest() {
        List<Object> objects = new ArrayList<>();

        FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        collectionReference = firestore.collection("categories");

        Query query = collectionReference.whereEqualTo("parent_category", "");
        Task<QuerySnapshot> querySnapshot = database.getDocuments(query);

        for (DocumentSnapshot documentSnapshot: querySnapshot.getResult()) {
            Map<String, Object> data = documentSnapshot.getData();

            Category category = null;
            if (data != null) {
                category = new Category(data);
            }
            objects.add(category);
        }

        reqPresenterOps.onReturnedCategoryList(objects);
    }

    @Override
    public void suggestionsRequest() {
        List<Object> objects = new ArrayList<>();
        final List<QuerySnapshot> querySnapshotList = new ArrayList<>();
        final FirebaseFirestore firestore = database.getFirestore();

        Query categoryQuery = firestore.collection("categories").orderBy("name");
        Task<QuerySnapshot> categoryTask = database.getDocuments(categoryQuery);
        querySnapshotList.add(categoryTask.getResult());

        Query productQuery = firestore.collection("products").orderBy("name");
        Task<QuerySnapshot> productTask = database.getDocuments(productQuery);
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
                        Product product = new Product(data);
                        objects.add(product);
                    }
                }
            }
        }

        reqPresenterOps.onReturnedAllSuggestions(objects);
    }
}

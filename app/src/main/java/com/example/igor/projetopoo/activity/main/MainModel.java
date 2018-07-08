package com.example.igor.projetopoo.activity.main;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
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
        QuerySnapshot querySnapshot = database.getDocuments(query);

        for (DocumentSnapshot documentSnapshot: querySnapshot) {
            Map<String, Object> data = documentSnapshot.getData();

            Category category = new Category(data);
            objects.add(category);
        }

        reqPresenterOps.onReturnedCategoryList(objects);
    }

}

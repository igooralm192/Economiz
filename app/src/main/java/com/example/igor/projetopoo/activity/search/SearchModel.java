package com.example.igor.projetopoo.activity.search;

import android.util.Log;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.entities.Result;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchModel implements SearchMVP.ModelOps {
    private SearchMVP.ReqPresenterOps reqPresenterOps;
    private Database database;

    public SearchModel(SearchMVP.ReqPresenterOps reqPresenterOps, Database database){
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    @Override
    public void resultListRequest(String query, String upperbound) {
        List<Object> objects = new ArrayList<>();

        final List<QuerySnapshot> querySnapshotList = new ArrayList<>();
        final FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        Query categoryQuery = firestore.collection("categories").orderBy("name").startAt(query).endBefore(upperbound);
        Task<QuerySnapshot> categoryTask = database.getDocuments(categoryQuery);
        querySnapshotList.add(categoryTask.getResult());

        Query productQuery = firestore.collection("products").orderBy("name").startAt(query).endBefore(upperbound);
        Task<QuerySnapshot> productTask = database.getDocuments(productQuery);
        querySnapshotList.add(productTask.getResult());

        for(QuerySnapshot queryDocumentSnapshots: querySnapshotList){
            for (DocumentSnapshot document: queryDocumentSnapshots) {
                String path = document.getReference().getPath().split("/")[0];
                Map<String, Object> data = document.getData();

                if(path.equals("categories")) {
                    if(data != null) {
                        Category category = new Category(data);
                        objects.add(category);
                    }
                } else {
                    if (data != null) {
                        Product product = new Product(data);
                        objects.add(product);
                    }
                }
            }
        }

        reqPresenterOps.onReturnedResultList(objects);
    }
}

package com.example.igor.projetopoo.activity.search;

import android.support.constraint.ConstraintLayout;

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

public class SearchModel implements SearchMVP.ModelOps {
    private SearchActivity activity;
    private SearchMVP.ReqPresenterOps reqPresenterOps;
    private Database database;

    public SearchModel(SearchActivity activity, SearchMVP.ReqPresenterOps reqPresenterOps, Database database){
        this.activity = activity;
        this.reqPresenterOps = reqPresenterOps;
        this.database = database;
    }

    /**
     * Requisita ao banco de dados (Firebase) a lista de categorias e produtos que encaixam na pesquisa
     * e envia para a Presenter a lista em formato Object
     */
    @Override
    public void resultListRequest(String query, String upperbound) throws ConnectionException, DatabaseException {
        final ConstraintLayout layout = activity.findViewById(R.id.container_search);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeAllViews();
            }
        });

        if (!ParentActivity.checkConnection(activity)) throw new ConnectionException(activity, layout);

        List<Object> objects = new ArrayList<>();

        final List<QuerySnapshot> querySnapshotList = new ArrayList<>();
        final FirebaseFirestore firestore = database.getFirestore();
        CollectionReference collectionReference;

        Query categoryQuery = firestore.collection("categories").orderBy("name").startAt(query).endBefore(upperbound);
        Task<QuerySnapshot> categoryTask = database.getDocuments(categoryQuery);
        if (!categoryTask.isSuccessful()) throw new DatabaseException(activity);
        querySnapshotList.add(categoryTask.getResult());

        Query productQuery = firestore.collection("products").orderBy("name").startAt(query).endBefore(upperbound);
        Task<QuerySnapshot> productTask = database.getDocuments(productQuery);
        if (!productTask.isSuccessful()) throw new DatabaseException(activity);
        querySnapshotList.add(productTask.getResult());

        for(QuerySnapshot queryDocumentSnapshots: querySnapshotList){

            for (DocumentSnapshot document: queryDocumentSnapshots) {
                String path = document.getReference().getPath().split("/")[0];
                Map<String, Object> data = document.getData();

                if(path.equals("categories")) {
                    if(data != null) {
                        Category category = new Category(document.getId(), data);
                        objects.add(category);
                    }
                } else {
                    if (data != null) {
                        Product product = new Product(document.getId(), data);
                        objects.add(product);
                    }
                }
            }
        }

        reqPresenterOps.onReturnedResultList(objects);
    }
}

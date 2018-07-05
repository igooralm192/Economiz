package com.example.igor.projetopoo.activity.category;

import android.util.Log;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
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
    public void categoryRequest(final String name) {
        List<Object> objects = new ArrayList<>();

        FirebaseFirestore firestore = database.getFirestore();
        Query query = firestore.collection("categories").whereEqualTo("parent_category", name);
        QuerySnapshot querySnapshot = database.getDocuments(query);

        String path = querySnapshot.getDocuments().get(0).getReference().getPath().split("/")[0];

        for (DocumentSnapshot documentSnapshot: querySnapshot) {
            Map<String, Object> data = documentSnapshot.getData();

            if (path.equals("categories")) {
                if (data != null) {
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

        reqPresenterOps.onReturnedCategory(path, objects);
    }
}
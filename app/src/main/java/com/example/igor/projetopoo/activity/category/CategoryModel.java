package com.example.igor.projetopoo.activity.category;

import android.util.Log;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
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
        QuerySnapshot querySnapshot = database.getDocuments(query);

        for (DocumentSnapshot documentSnapshot: querySnapshot) {
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
package com.example.igor.projetopoo.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.AsyncDownload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public abstract class DatabaseHelper {
    public static void addCategory(final Database database, final String name, final Category category) {
        new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public Object doInBackground(Object... objects) {
                Map<String, Object> map = new HashMap<>();

                map.put("name", category.getName());
                map.put("parent_category", category.getParentCategory());
                map.put("background_category", category.getBackgroundCategory());

                FirebaseFirestore firestore = database.getFirestore();
                DocumentReference documentReference = firestore.collection("categories").document(name);
                Task<Void> task = database.updateDocument(documentReference, map);

                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("DATABASE", "Documento " + name + " foi adicionado com sucesso!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("DATABASE", "Erro ao adicionar documento!");
                    }
                });

                return null;
            }

            @Override
            public void onPostExecute(Object object) {

            }
        }).execute();

    }

    public static void addProduct(final Database database, final String name, final Product product) {
        new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public Object doInBackground(Object... objects) {
                Map<String, Object> map = new HashMap<>();

                map.put("name", product.getName());
                map.put("parent_category", product.getParentCategory());
                map.put("background_category", product.getBackgroundCategory());
                map.put("average_price", product.getAveragePrice());


                Map<String, Object> range = new HashMap<>();
                range.put("minimum_price", product.getPriceRange().first);
                range.put("maximum_price", product.getPriceRange().second);
                map.put("price_range", range);

                FirebaseFirestore firestore = database.getFirestore();
                DocumentReference documentReference = firestore.collection("products").document(name);
                Task<Void> task = database.updateDocument(documentReference, map);

                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("DATABASE", "Documento " + name + " foi adicionado com sucesso!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("DATABASE", "Erro ao adicionar documento!");
                    }
                });

                return null;
            }

            @Override
            public void onPostExecute(Object object) {

            }
        }).execute();
    }

    //public static void addFeedback(Database database, final String name, Feedback feedback);
}

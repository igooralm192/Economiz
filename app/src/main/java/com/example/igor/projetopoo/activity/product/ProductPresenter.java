package com.example.igor.projetopoo.activity.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.product.ProductMVP;
import com.example.igor.projetopoo.activity.product.ProductModel;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.AsyncDownload;
import com.example.igor.projetopoo.helper.CustomDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductPresenter implements ProductMVP.PresenterOps, ProductMVP.ReqPresenterOps {

    private ProductMVP.ReqViewOps reqViewOps;
    private ProductMVP.ModelOps modelOps;

    public ProductPresenter(ProductMVP.ReqViewOps reqViewOps, Database database) {
        this.reqViewOps = reqViewOps;
        this.modelOps = new ProductModel(this, database);
    }

    @Override
    public void getCategoryList() {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);

            }

            @Override
            public Object doInBackground(Object... objects) {
                modelOps.categoryListRequest();

                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                reqViewOps.showProgressBar(false);
            }
        });

        asyncDownload.execute();
    }

    @Override
    public void onReturnedCategoryList(List<Object> objects) {
        List<Category> categories = new ArrayList<>();

        for (Object object: objects) {
            categories.add((Category) object);
            categories.get(categories.indexOf(object)).setBackground(R.drawable.food);
        }

        Collections.sort(categories);

        reqViewOps.showCategories(categories);
    }

    @Override
    public void getAllSuggestions(Activity activity) {
        final CustomDialog loadSuggestions = new CustomDialog(activity, R.layout.load_suggestions);
        loadSuggestions.getWindow().setBackgroundDrawableResource(R.drawable.load_suggestions_background);
        loadSuggestions.setCanceledOnTouchOutside(false);

        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public synchronized void onPreExecute() {
                loadSuggestions.show();
            }

            @Override
            public Object doInBackground(Object... objects) {
                modelOps.suggestionsRequest();
                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                loadSuggestions.dismiss();
            }
        });

        asyncDownload.execute();
    }

    @Override
    public void onReturnedAllSuggestions(List<Object> objects) {
        List<Category> categories = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        for (Object object: objects) {
            if (object instanceof Category) {
                Category category = (Category) object;
                categories.add(category);
            } else {
                Product product = (Product) object;
                products.add(product);
            }
        }

        reqViewOps.saveAllSuggestions(categories, products);
    }




}
package com.example.igor.projetopoo.activity.main;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.AsyncDownload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPresenter implements MainMVP.PresenterOps, MainMVP.ReqPresenterOps {

    private MainMVP.ReqViewOps reqViewOps;
    private MainMVP.ModelOps modelOps;

    public MainPresenter(MainMVP.ReqViewOps reqViewOps, Database database) {
        this.reqViewOps = reqViewOps;
        this.modelOps = new MainModel(this, database);
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
    public void getAllSuggestions() {
        modelOps.suggestionsRequest();
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

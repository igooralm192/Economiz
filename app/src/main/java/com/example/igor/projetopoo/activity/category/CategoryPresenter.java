package com.example.igor.projetopoo.activity.category;

import android.util.Log;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.AsyncDownload;

import java.util.ArrayList;
import java.util.List;

public class CategoryPresenter implements CategoryMVP.PresenterOps, CategoryMVP.ReqPresenterOps {
    private CategoryMVP.ReqViewOps reqViewOps;
    private CategoryMVP.ModelOps modelOps;

    public CategoryPresenter(CategoryMVP.ReqViewOps reqViewOps, Database database) {
        this.reqViewOps = reqViewOps;
        this.modelOps = new CategoryModel(this, database);
    }

    @Override
    public void getCategory(final String name) {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);
            }

            @Override
            public Object doInBackground(Object... objects) {
                modelOps.categoryRequest(name);

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
    public void onReturnedCategory(String type, List<Object> objects) {
        if (type.equals("categories")) {
            List<Category> subcategories = new ArrayList<>();

            for (Object object: objects)
                subcategories.add((Category) object);

            reqViewOps.showSubcategories(subcategories);
        } else {
            List<Product> products = new ArrayList<>();

            for (Object object: objects)
                products.add((Product) object);

            reqViewOps.showProducts(products);
        }
    }
}
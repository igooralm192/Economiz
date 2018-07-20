package com.example.igor.projetopoo.activity.category;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Entitie;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;
import com.example.igor.projetopoo.helper.AsyncDownload;
import com.example.igor.projetopoo.helper.CustomDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryPresenter implements CategoryMVP.PresenterOps, CategoryMVP.ReqPresenterOps {
    private CategoryActivity activity;
    private CategoryMVP.ReqViewOps reqViewOps;
    private CategoryMVP.ModelOps modelOps;

    public CategoryPresenter(CategoryActivity activity, Database database) {
        this.activity = activity;
        this.reqViewOps = activity;
        this.modelOps = new CategoryModel(activity, this, database);
    }

    @Override
    public void getCategory(final Category category) {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);

            }

            @Override
            public Object doInBackground(Object... objects) {
                try {
                    modelOps.categoryRequest(category);
                } catch (ConnectionException e) {
                    e.connectionFail(CategoryPresenter.this, category);
                } catch (DatabaseException e) {
                    e.failReadData();
                }

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
    public void onReturnedCategory(List<Object> objects) {
        List<Entitie> subcategories = new ArrayList<>();
        List<Entitie> products = new ArrayList<>();

        for (Object object: objects) {
            if (object instanceof Product)
                products.add((Product) object);

        }

        Collections.sort(products);

        for (Object object: objects) {
            if (object instanceof Category)
                subcategories.add((Category) object);
        }

        Collections.sort(subcategories);

        List<Entitie> subitems = new ArrayList<>();
        subitems.addAll(products);
        subitems.addAll(subcategories);

        reqViewOps.showSubitems(subitems);

    }
}
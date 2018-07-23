package com.example.igor.projetopoo.activity.main;

import android.app.Activity;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.category.CategoryPresenter;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;
import com.example.igor.projetopoo.helper.AsyncDownload;
import com.example.igor.projetopoo.helper.CustomDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPresenter implements MainMVP.PresenterOps, MainMVP.ReqPresenterOps {
    private MainActivity activity;
    private MainMVP.ReqViewOps reqViewOps;
    private MainMVP.ModelOps modelOps;

    public MainPresenter(MainActivity activity, Database database) {
        this.activity = activity;
        this.reqViewOps = activity;
        this.modelOps = new MainModel(activity, this, database);
    }

    /*
      Executa uma AsyncTask (tarefas simultâneas), determina que a View mostre o símbolo de
      carregamento, solicita a Model a lista de categorias principais e depois desabilita o
      símbolo que estava sendo mostrado.
     */
    @Override
    public void getCategoryList() {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);

            }

            @Override
            public Object doInBackground(Object... objects) {
                try {
                    modelOps.categoryListRequest();
                } catch (ConnectionException e) {
                    e.connectionFail(MainPresenter.this);
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

    /*
      Recebe uma lista de Objects (da Model) e os transforma em Category, ordena-a por nome e
      determina que a View mostre essa lista de categorias.
     */
    @Override
    public void onReturnedCategoryList(List<Object> objects) {
        List<Category> categories = new ArrayList<>();

        for (Object object: objects) {
            categories.add((Category) object);
        }

        Collections.sort(categories);

        reqViewOps.showCategories(categories);
    }

    /*
      Executa uma AsyncTask (tarefas simultâneas), abre uma mensagem informando o processamento de
      informações, solicita a Model as sugestões de categorias e de produtos, depois fecha a caixa
      de mensagem.
     */
    @Override
    public void getAllSuggestions() {
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
                try {
                    modelOps.suggestionsRequest();
                } catch (ConnectionException e) {
                    e.connectionFail(MainPresenter.this, activity);
                } catch (DatabaseException e) {
                    e.failReadData();
                }
                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                loadSuggestions.dismiss();
            }
        });

        asyncDownload.execute();
    }

    /*
      Recebe uma lista de Objects (da Model) e a transforma em duas lista, uma de Category e outra
      de Product e determina que a View salve-as como sugestão.
     */
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

package com.example.igor.projetopoo.activity.main;

import android.app.Activity;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;

import java.util.List;

/*
  Interface utilizada para definir as outras interfaces necessárias ao padrão MVP (Model-View-Presenter)
  referente à tela principal.
 */
public interface MainMVP {

    // Métodos implementados pela Presenter e os quais a View (MainActivity) possui acesso.
    public interface PresenterOps {
        void getCategoryList();
        void getAllSuggestions(Activity activity);
    }

    // Métodos implementados pela Model e os quais a Presenter tem acesso.
    public interface ModelOps {
        void categoryListRequest() throws ConnectionException, DatabaseException;
        void suggestionsRequest();
    }

    // Métodos implementados pela Presenter e os quais a Model tem acesso.
    public interface ReqPresenterOps {
        void onReturnedCategoryList(List<Object> objects);
        void onReturnedAllSuggestions(List<Object> objects);
    }

    // Métodos implementados pela View (MainActivity) e os quais a Presenter tem acesso.
    public interface ReqViewOps {
        void showCategories(List<Category> categories);
        void showProgressBar(Boolean enabled);
        void saveAllSuggestions(List<Category> categories, List<Product> products);
    }
}
package com.example.igor.projetopoo.activity.search;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;

import java.util.List;

/**
 * Interface utilizada para definir as outras interfaces necessárias ao padrão MVP (Model-View-Presenter)
 * referente a tela de pesquisa
 */
public interface SearchMVP {

    // Métodos implementados pela Presenter (SearchPresenter) e os quais a View (SearchActivity) tem acesso
    interface PresenterOps{
        void getResultList(String query);
    }

    // Métodos implementados pela Model (SearchModel) e os quais a Presenter tem acesso
    interface ModelOps{
        void resultListRequest(String query, String upperbound) throws ConnectionException, DatabaseException;
    }

     // Métodos implementados pela Presenter e os quais o Model tem acesso
    interface ReqPresenterOps{
        void onReturnedResultList(List<Object> objects);
    }


     // Métodos implementados pela View e os quais a Presenter tem accesso
    interface ReqViewOps{
        void showResults(List<Category> categoryList, List<Product> productList);
        void showProgressBar(Boolean enabled);
    }

}

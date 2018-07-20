package com.example.igor.projetopoo.activity.search;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.entities.Result;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;

import java.util.List;

public interface SearchMVP {

    interface PresenterOps{
        // Presenter methods => View acess
        void getResultList(String query);
    }

    interface ModelOps{
        // Model methods => Presenter acess
        void resultListRequest(String query, String upperbound) throws ConnectionException, DatabaseException;
    }

    interface ReqPresenterOps{
        // Presenter methods => Model acess
        void onReturnedResultList(List<Object> objects);
    }

    interface ReqViewOps{
        // View methods => Presenter acess
        void showResults(List<Category> categoryList, List<Product> productList);
        void showProgressBar(Boolean enabled);
    }








}

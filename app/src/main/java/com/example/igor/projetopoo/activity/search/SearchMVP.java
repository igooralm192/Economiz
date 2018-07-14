package com.example.igor.projetopoo.activity.search;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.entities.Result;

import java.util.List;

public interface SearchMVP {

    interface PresenterOps{
        // Presenter methods => View acess
        void getResultList(String query);
    }

    interface ModelOps{
        // Model methods => Presenter acess
        void resultListRequest(String query, String upperbound);
    }

    interface ReqPresenterOps{
        // Presenter methods => Model acess
        void onReturnedResultList(List<Object> objects);
    }

    interface ReqViewOps{
        // View methods => Presenter acess
        void showResults(List<Category> categoryList, List<Product> productList);
    }








}

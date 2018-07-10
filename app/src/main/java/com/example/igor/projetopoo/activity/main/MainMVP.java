package com.example.igor.projetopoo.activity.main;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;

import java.util.List;

public interface MainMVP {
    public interface PresenterOps {
        // Presenter methods => View acess
        void getCategoryList();
        void getAllSuggestions();
    }

    public interface ModelOps {
        // Model methods => Presenter acess
        void categoryListRequest();
        void suggestionsRequest();
    }

    public interface ReqPresenterOps {
        // Presenter methods => Model acess
        void onReturnedCategoryList(List<Object> objects);
        void onReturnedAllSuggestions(List<Object> objects);
    }

    public interface ReqViewOps {
        // View methods => Presenter access
        void showCategories(List<Category> categories);
        void showProgressBar(Boolean enabled);
        void saveAllSuggestions(List<Category> categories, List<Product> products);
    }
}
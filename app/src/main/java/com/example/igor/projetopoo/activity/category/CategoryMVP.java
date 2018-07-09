package com.example.igor.projetopoo.activity.category;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;

import java.util.List;

public interface CategoryMVP {
    public interface PresenterOps {
        void getCategory(final Category category);
        void getAllSuggestions();
    }

    public interface ModelOps {
        void categoryRequest(final Category category);
        void suggestionsRequest();
    }

    public interface ReqPresenterOps {
        void onReturnedCategory(String type, List<Object> objects);
        void onReturnedAllSuggestions(List<Object> objects);
    }

    public interface ReqViewOps {
        void showSubcategories(List<Category> subcategories);
        void showProducts(List<Product> products);
        void showProgressBar(Boolean enabled);
        void saveAllSuggestions(List<Category> categories, List<Product> products);
    }
}
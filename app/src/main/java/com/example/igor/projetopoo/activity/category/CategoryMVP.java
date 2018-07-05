package com.example.igor.projetopoo.activity.category;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;

import java.util.List;

public interface CategoryMVP {
    public interface PresenterOps {
        void getCategory(final String name);
    }

    public interface ModelOps {
        void categoryRequest(final String name);
    }

    public interface ReqPresenterOps {
        void onReturnedCategory(String type, List<Object> objects);
    }

    public interface ReqViewOps {
        void showSubcategories(List<Category> subcategories);
        void showProducts(List<Product> products);
        void showProgressBar(boolean enabled);
    }
}
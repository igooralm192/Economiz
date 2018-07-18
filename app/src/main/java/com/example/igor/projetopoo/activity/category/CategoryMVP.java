package com.example.igor.projetopoo.activity.category;

import android.app.Activity;
import android.content.Context;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;

import java.util.List;

public interface CategoryMVP {
    public interface PresenterOps {
        void getCategory(final Category category);
    }

    public interface ModelOps {
        void categoryRequest(final Category category) throws ConnectionException, DatabaseException;
    }

    public interface ReqPresenterOps {
        void onReturnedCategory(String type, List<Object> objects);
    }

    public interface ReqViewOps {
        void showSubcategories(List<Category> subcategories);
        void showProducts(List<Product> products);
        void showProgressBar(Boolean enabled);
    }
}
package com.example.igor.projetopoo.activity.main;

import com.example.igor.projetopoo.entities.Category;

import java.util.List;

public interface MainMVP {
    public interface PresenterOps {
        // Presenter methods => View acess
        void getCategoryList();
    }

    public interface ModelOps {
        // Model methods => Presenter acess
        void categoryListRequest();
    }

    public interface ReqPresenterOps {
        // Presenter methods => Model acess
        void onReturnedCategoryList(List<Object> objects);
    }

    public interface ReqViewOps {
        // View methods => Presenter access
        void showCategories(List<Category> categories);
        void showProgressBar(Boolean enabled);
    }
}

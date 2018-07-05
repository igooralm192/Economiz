package com.example.igor.projetopoo.activity.category;

public interface CategoryMVP {
    public interface PresenterOps {
        void getCategory(String name);
    }

    public interface ModelOps {
        void categoryRequest(String name);
    }

    public interface ReqPresenterOps {
        void returnCategory();
    }

    public interface ReqViewOps {
        void showCategory();
    }
}

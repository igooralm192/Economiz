package com.example.igor.projetopoo.activity.category;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Entitie;
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
        void onReturnedCategory(List<Object> objects);
    }

    public interface ReqViewOps {
        void showSubitems(List<Entitie> subitems);
        void showProgressBar(Boolean enabled);
    }
}
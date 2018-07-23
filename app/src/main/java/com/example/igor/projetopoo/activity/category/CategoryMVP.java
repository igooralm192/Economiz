package com.example.igor.projetopoo.activity.category;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Entity;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;

import java.util.List;

public interface CategoryMVP {
    // Métodos da Presenter aos quais a Activity possui acesso
    public interface PresenterOps {
        void getCategory(final Category category);
    }

    // Métodos da Model aos quais a Presenter possui acesso
    public interface ModelOps {
        void categoryRequest(final Category category) throws ConnectionException, DatabaseException;
    }

    // Métodos da Presenter aos quais a Model possui acesso
    public interface ReqPresenterOps {
        void onReturnedCategory(List<Object> objects);
    }

    // Métodos da View aos quais a Presenter possui acesso
    public interface ReqViewOps {
        void showSubitems(List<Entity> subitems);

        void showProgressBar(Boolean enabled);
    }
}
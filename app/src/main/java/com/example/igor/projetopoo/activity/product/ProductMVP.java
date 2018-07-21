package com.example.igor.projetopoo.activity.product;

import android.app.Dialog;

import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;

import java.util.List;

public interface ProductMVP {
    public interface PresenterOps {
        // Presenter methods => View access
        void getFeedbacks(Product product);
        void addFeedback(Dialog dialog, Feedback feedback);
        void removeFeedback();
        void updateProduct(Product currentProduct);
    }

    public interface ModelOps {
        // Model methods => Presenter access
        void feedbackListRequest(Product product) throws ConnectionException, DatabaseException;
        void insertFeedback(Feedback feedback) throws ConnectionException, DatabaseException;
        void deleteFeedback() throws ConnectionException, DatabaseException;
        void refreshProduct(Product product) throws DatabaseException;
    }

    public interface ReqPresenterOps {
        // Presenter methods => Model access
        void onReturnedFeedbackList(List<Object> objects, Product product);
        void onFeedbackInserted();
        void onFeedbackDeleted();
    }

    public interface ReqViewOps {
        //View methods => Presenter access
        void showFeedbacks(List<Feedback> list,Double averagePrice);
        void showSnackbar(int op);
        void showProgressBar(Boolean enabled);
    }
}

package com.example.igor.projetopoo.activity.product;

import android.app.Activity;
import android.app.Dialog;
import android.util.Pair;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;

import java.util.List;

public interface ProductMVP {
    public interface PresenterOps {
        // Presenter methods => View acess
        void getFeedbacks(String productName);
        void addFeedback(Dialog dialog, Feedback feedback);
        void removeFeedback();
    }

    public interface ModelOps {
        // Model methods => Presenter acess
        void feedbackListRequest(String productName) throws ConnectionException, DatabaseException;
        void insertFeedback(Feedback feedback) throws ConnectionException, DatabaseException;
        void deleteFeedback() throws ConnectionException, DatabaseException;
    }

    public interface ReqPresenterOps {
        // Presenter methods => Model acess
        void onReturnedFeedbackList(List<Object> objects);
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

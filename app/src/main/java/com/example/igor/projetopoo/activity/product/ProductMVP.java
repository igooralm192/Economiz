package com.example.igor.projetopoo.activity.product;

import android.app.Activity;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;

import java.util.List;

public class ProductMVP {
    public interface PresenterOps {
        // Presenter methods => View acess
        void addFeedback(Feedback feedback);
        void removeFeedback(Feedback feedback);
    }

    public interface ModelOps {
        // Model methods => Presenter acess
        void insertFeedback(Feedback feedback);
        void deleteFeedback(Feedback feedback);
    }

    public interface ReqPresenterOps {
        // Presenter methods => Model acess
        void onFeedbackInserted();
        void onFeedbackDeleted();
    }

    public interface ReqViewOps {
        //View methods => Presenter access
        void showSnackbar();
        void showProgressBar(Boolean enabled);
    }
}

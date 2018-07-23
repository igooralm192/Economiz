package com.example.igor.projetopoo.exception;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.category.CategoryMVP;
import com.example.igor.projetopoo.activity.category.CategoryPresenter;
import com.example.igor.projetopoo.activity.main.MainMVP;
import com.example.igor.projetopoo.activity.main.MainPresenter;
import com.example.igor.projetopoo.activity.product.ProductMVP;
import com.example.igor.projetopoo.activity.product.ProductPresenter;
import com.example.igor.projetopoo.activity.search.SearchMVP;
import com.example.igor.projetopoo.activity.search.SearchPresenter;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;

// Exceção para falhas na conexão com a internet

public class ConnectionException extends Exception {
    private Context context;
    private Activity activity;
    private ConstraintLayout layout;

    public ConnectionException(Context context, ConstraintLayout layout) {
        this.context = context;
        this.activity = (Activity) context;
        this.layout = layout;
    }

    public void connectionFail(final Object presenter, final Object ... object) {
        final View view = LayoutInflater.from(context).inflate(R.layout.connection_fail, layout, false);

        Button button = view.findViewById(R.id.try_again_connection);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presenter instanceof MainPresenter) {
                    MainMVP.PresenterOps presenterOps = (MainMVP.PresenterOps) presenter;

                    if (object[0] instanceof Activity)
                        presenterOps.getAllSuggestions();
                    else
                        presenterOps.getCategoryList();
                }

                if (presenter instanceof CategoryPresenter) {
                    CategoryMVP.PresenterOps presenterOps = (CategoryMVP.PresenterOps) presenter;

                    presenterOps.getCategory((Category) object[0]);
                }

                if (presenter instanceof ProductPresenter) {
                    ProductMVP.PresenterOps presenterOps = (ProductMVP.PresenterOps) presenter;

                    if (object[0] instanceof Product)
                        presenterOps.getFeedbacks((Product) object[0]);
                    else if (object[0] instanceof Feedback)
                        presenterOps.addFeedback((Feedback) object[0], (Product) object[1], (Dialog) object[2]);
                }

                if (presenter instanceof SearchPresenter) {
                    SearchMVP.PresenterOps presenterOps = (SearchMVP.PresenterOps) presenter;

                    presenterOps.getResultList((String) object[0]);
                }
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.addView(view, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            }
        });

    }
}

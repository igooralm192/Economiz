package com.example.igor.projetopoo.activity.product;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.product.ProductMVP;
import com.example.igor.projetopoo.activity.product.ProductModel;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.AsyncDownload;
import com.example.igor.projetopoo.helper.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProductPresenter implements ProductMVP.PresenterOps, ProductMVP.ReqPresenterOps {

    private ProductMVP.ReqViewOps reqViewOps;
    private ProductMVP.ModelOps modelOps;

    public ProductPresenter(ProductMVP.ReqViewOps reqViewOps, Database database) {
        this.reqViewOps = reqViewOps;
        this.modelOps = new ProductModel(this, database);
    }

    @Override
    public void getFeedbacks(final String productName) {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);

            }

            @Override
            public Object doInBackground(Object... objects) {
                modelOps.feedbackListRequest(productName);

                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                reqViewOps.showProgressBar(false);
            }
        });

        asyncDownload.execute();
    }

    @Override
    public void onReturnedFeedbackList(List<Object> objects) {
        List<Feedback> feedbacks = new ArrayList<>();

        for (Object object: objects) {
            feedbacks.add((Feedback) object);
        }

        reqViewOps.showFeedbacks(feedbacks);
    }

    @Override
    public void addFeedback(Dialog dialog, String name, Pair<Double,Double> range) {
        EditText location = dialog.findViewById(R.id.location_edit_text);
        EditText price = dialog.findViewById(R.id.price_edit_text);
        String prc = price.getText().toString();
        String loc = location.getText().toString();

        location.setError(null);
        price.setError(null);

        if ("".equals(loc)) {
            loc = "Localização não informada";
        }

        if ("".equals(prc)) {
            price.setError("Este campo é obrigatório");
            return;
        }
        if(Double.parseDouble(prc) > range.second){
            String s = "R$ "+ String.format("%.2f", range.second);
            s = s.replace('.',',');
            price.setError("O valor deve ser menor que " + s);
            return;
        }else if(Double.parseDouble(prc) < range.first){
            String s = "R$ "+ String.format("%.2f", range.first);
            s = s.replace('.',',');
            price.setError("O valor deve ser maior que "+ s);
            return;
        }

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String str = format.format(date);
        final Feedback feedback = new Feedback(name, loc, str, Double.parseDouble(prc));
        dialog.dismiss();

        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);
            }

            @Override
            public Object doInBackground(Object... objects) {
                modelOps.insertFeedback(feedback);
                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                reqViewOps.showProgressBar(false);
            }
        });
        asyncDownload.execute();
    }

    @Override
    public void removeFeedback() {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);
            }

            @Override
            public Object doInBackground(Object... objects) {
                modelOps.deleteFeedback();
                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                reqViewOps.showProgressBar(false);
            }
        });
        asyncDownload.execute();
    }


    @Override
    public void onFeedbackInserted() {
        reqViewOps.showSnackbar(0);
    }

    @Override
    public void onFeedbackDeleted() {
        reqViewOps.showSnackbar(1);
    }
}
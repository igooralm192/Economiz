package com.example.igor.projetopoo.activity.product;

import android.app.Dialog;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.exception.ConnectionException;
import com.example.igor.projetopoo.exception.DatabaseException;
import com.example.igor.projetopoo.helper.AsyncDownload;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ProductPresenter implements ProductMVP.PresenterOps, ProductMVP.ReqPresenterOps {
    private ProductActivity activity;
    private ProductMVP.ReqViewOps reqViewOps;
    private ProductMVP.ModelOps modelOps;
    private Database database;

    public ProductPresenter(ProductActivity activity, Database database) {
        this.activity = activity;
        this.reqViewOps = activity;
        this.modelOps = new ProductModel(activity, this, database);
        this.database = database;
    }

    @Override
    public void getFeedbacks(final Product product) {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);

            }

            @Override
            public Object doInBackground(Object... objects) {
                try {
                    modelOps.feedbackListRequest(product);
                } catch (ConnectionException e) {
                    e.connectionFail(ProductPresenter.this, product);
                } catch (DatabaseException e) {
                    e.failReadData();
                }

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
    public void onReturnedFeedbackList(List<Object> objects, Product product) {
        List<Feedback> feedbacks = new ArrayList<>();

        for (Object object: objects) {
            feedbacks.add((Feedback) object);
        }
        Double sum = 0.0;
        for (Feedback a:feedbacks
             ) {
            sum += a.getPrice().doubleValue();
        }
        if(feedbacks.size()>0) sum/=feedbacks.size();
        else sum = (product.getPriceRange().first.doubleValue()+product.getPriceRange().second.doubleValue())/2;

        Collections.sort(feedbacks, new Comparator<Feedback>() {
            @Override
            public int compare(Feedback f1, Feedback f2) {
                return f2.getDate().compareTo(f1.getDate());
            }
        });

        reqViewOps.showFeedbacks(feedbacks,sum);
    }

    @Override
    public void addFeedback(final Dialog dialog, final Feedback feedback) {

        dialog.dismiss();

        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);
            }

            @Override
            public Object doInBackground(Object... objects) {
                try {
                    modelOps.insertFeedback(feedback);

                    FirebaseFirestore firestore = database.getFirestore();
                    CollectionReference collectionReference = firestore.collection("products");
                    Query query = collectionReference.whereEqualTo("name", feedback.getProduct());
                    Task<QuerySnapshot> querySnapshot = database.getDocuments(query);
                    if (!querySnapshot.isSuccessful()) throw new DatabaseException(activity);

                    if (querySnapshot.getResult().size() > 0) {
                        DocumentSnapshot product = querySnapshot.getResult().getDocuments().get(0);

                        if (product.getData() != null) {
                            return new Product(product.getId(), product.getData());
                        }
                    }
                } catch (ConnectionException e) {
                    e.connectionFail(ProductPresenter.this, feedback, dialog);
                } catch (DatabaseException e) {
                    e.failWriteData();
                }

                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                if (object instanceof Product) {
                    ProductPresenter.this.getFeedbacks((Product) object);
                }
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

                try {
                    modelOps.deleteFeedback();
                } catch (ConnectionException e) {
                    e.connectionFail(ProductPresenter.this);
                } catch (DatabaseException e) {
                    e.failRemoveData();
                }

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
    public void updateProduct(final Product currentProduct) {

        new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);
            }

            @Override
            public Object doInBackground(Object... objects) {
                try {
                    modelOps.refreshProduct(currentProduct);
                } catch (DatabaseException e) {
                    e.failWriteData();
                }

                return null;
            }

            @Override
            public void onPostExecute(Object object) {
                // ProductPresenter.this.getFeedbacks(feedback.getProduct());
            }
        }).execute();
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
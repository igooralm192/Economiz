package com.example.igor.projetopoo.activity.search;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.AsyncDownload;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements SearchMVP.PresenterOps, SearchMVP.ReqPresenterOps {
    private SearchMVP.ReqViewOps reqViewOps;
    private SearchMVP.ModelOps modelOps;

    public SearchPresenter(SearchMVP.ReqViewOps reqViewOps, Database database){
        this.reqViewOps = reqViewOps;
        this.modelOps = new SearchModel(this, database);

    }

    @Override
    public void getResultList(final String query) {
        AsyncDownload asyncDownload = new AsyncDownload(new AsyncDownload.OnAsyncDownloadListener() {
            @Override
            public void onPreExecute() {
                reqViewOps.showProgressBar(true);

            }

            @Override
            public Object doInBackground(Object... objects) {
                char lastChar = query.charAt(query.length()-1);
                char letters[] = query.toCharArray();

                for (int i =0; i < letters.length; i++){
                    if (letters[i] == ' '){
                        letters[i+1] = Character.toUpperCase(letters[i+1]);
                    }
                }

                letters[0] = Character.toUpperCase(letters[0]);
                String lowerbound = new String(letters);

                letters[query.length()-1] = ++lastChar;
                String upperbound = new String(letters);


                modelOps.resultListRequest(lowerbound, upperbound);

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
    public void onReturnedResultList(List<Object> objects) {
        List<Category> categoryList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        for (Object object: objects) {
            if (object instanceof Category) {
                categoryList.add((Category) object);
            }else {
                productList.add((Product) object);
            }
        }
        reqViewOps.showResults(categoryList, productList);
    }

}

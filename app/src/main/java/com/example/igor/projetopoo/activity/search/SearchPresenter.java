    package com.example.igor.projetopoo.activity.search;

import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.entities.Result;

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
    public void getResultList(String query) {
        char a = query.charAt(query.length()-1);
        String upperbound = query.replace(a, a++);

        char uppercharquery = query.toUpperCase().charAt(0);
        query.toLowerCase();

        char upperCharUpperBound = upperbound.toUpperCase().charAt(0);
        upperbound.toLowerCase();

        modelOps.resultListRequest(query.replace(query.charAt(0), uppercharquery), upperbound.replace(upperbound.charAt(0), upperCharUpperBound));
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

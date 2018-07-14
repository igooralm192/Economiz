package com.example.igor.projetopoo.activity.search;

import com.example.igor.projetopoo.entities.Result;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements SearchMVP.PresenterOps, SearchMVP.ReqPresenterOps {

    private SearchMVP.ReqViewOps reqViewOps;
    private SearchMVP.ModelOps modelOps;



    public SearchPresenter(SearchMVP.ReqViewOps reqViewOps){
        this.reqViewOps = reqViewOps;
        this.modelOps = new SearchModel(this);

    }

    @Override
    public void getResultList() {
        modelOps.resultListRequest();
    }


    @Override
    public void onReturnedResultList(List<Object> objects) {
        List<Result> results = new ArrayList<>();
        for (Object object: objects){
            results.add((Result) object);
        }
        reqViewOps.showResults(results);
    }
}

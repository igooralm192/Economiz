package com.example.igor.projetopoo.activity.search;

public class SearchPresenter implements SearchMVP.PresenterOps, SearchMVP.ReqPresenterOps {

    private SearchMVP.ReqViewOps reqViewOps;
    private SearchMVP.ModelOps modelOps;



    public SearchPresenter(SearchMVP.ReqViewOps reqViewOps){
        this.reqViewOps = reqViewOps;
        this.modelOps = new SearchModel(this);

    }
}

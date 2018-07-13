package com.example.igor.projetopoo.activity.search;

public class SearchModel implements SearchMVP.ModelOps {

    private SearchMVP.ReqPresenterOps reqPresenterOps;

    public SearchModel(SearchMVP.ReqPresenterOps reqPresenterOps){
        this.reqPresenterOps = reqPresenterOps;
    }

}

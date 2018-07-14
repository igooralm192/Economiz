package com.example.igor.projetopoo.activity.search;

import java.util.ArrayList;
import java.util.List;

public class SearchModel implements SearchMVP.ModelOps {

    private SearchMVP.ReqPresenterOps reqPresenterOps;

    public SearchModel(SearchMVP.ReqPresenterOps reqPresenterOps){
        this.reqPresenterOps = reqPresenterOps;
    }

    @Override
    public void resultListRequest() {
        List<Object> objects = new ArrayList<>();
        // TODO: Implement this function
        reqPresenterOps.onReturnedResultList(objects);
    }
}

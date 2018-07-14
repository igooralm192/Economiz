package com.example.igor.projetopoo.activity.search;

import com.example.igor.projetopoo.entities.Result;

import java.util.List;

public interface SearchMVP {

    interface PresenterOps{
        // Presenter methods => View acess
        void getResultList();
    }

    interface ModelOps{
        // Model methods => Presenter acess
        void resultListRequest();
    }

    interface ReqPresenterOps{
        // Presenter methods => Model acess
        void onReturnedResultList(List<Object> objects);
    }

    interface ReqViewOps{
        // View methods => Presenter acess
        void showResults(List<Result> results);
    }








}

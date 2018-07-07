package com.example.igor.projetopoo.activity.main;

public class MainModel implements MainMVP.ModelOps {
    private MainMVP.ReqPresenterOps reqPresenterOps;

    public MainModel (MainMVP.ReqPresenterOps reqPresenterOps) {
        this.reqPresenterOps = reqPresenterOps;
    }

}

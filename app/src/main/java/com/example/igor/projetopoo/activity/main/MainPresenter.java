package com.example.igor.projetopoo.activity.main;

public class MainPresenter implements MainMVP.PresenterOps, MainMVP.ReqPresenterOps {

    private MainMVP.ReqViewOps reqViewOps;
    private MainMVP.ModelOps modelOps;

    public MainPresenter(MainMVP.ReqViewOps reqViewOps) {
        this.reqViewOps = reqViewOps;
        this.modelOps = new MainModel(this);
    }

}

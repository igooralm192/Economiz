package com.example.igor.projetopoo.activity.category;

public class CategoryPresenter implements CategoryMVP.PresenterOps, CategoryMVP.ReqPresenterOps {
    private CategoryMVP.ReqViewOps reqViewOps;
    private CategoryMVP.ModelOps modelOps;

    public CategoryPresenter(CategoryMVP.ReqViewOps reqViewOps) {
        this.reqViewOps = reqViewOps;
        this.modelOps = new CategoryModel(this);
    }

    @Override
    public void getCategory(String name) {

    }

    @Override
    public void returnCategory() {

    }
}

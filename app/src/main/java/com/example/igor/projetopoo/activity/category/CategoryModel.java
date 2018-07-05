package com.example.igor.projetopoo.activity.category;

public class CategoryModel implements CategoryMVP.ModelOps {
    private CategoryMVP.ReqPresenterOps reqPresenterOps;

    public CategoryModel(CategoryMVP.ReqPresenterOps reqPresenterOps) {
        this.reqPresenterOps = reqPresenterOps;
    }

    @Override
    public void categoryRequest(String name) {

    }
}

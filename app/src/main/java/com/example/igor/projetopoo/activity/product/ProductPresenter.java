package com.example.igor.projetopoo.activity.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.product.ProductMVP;
import com.example.igor.projetopoo.activity.product.ProductModel;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.AsyncDownload;
import com.example.igor.projetopoo.helper.CustomDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductPresenter implements ProductMVP.PresenterOps, ProductMVP.ReqPresenterOps {

    private ProductMVP.ReqViewOps reqViewOps;
    private ProductMVP.ModelOps modelOps;

    public ProductPresenter(ProductMVP.ReqViewOps reqViewOps, Database database) {
        this.reqViewOps = reqViewOps;
        this.modelOps = new ProductModel(this, database);
    }


}
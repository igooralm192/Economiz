package com.example.igor.projetopoo.activity.product;

import android.app.Activity;

import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Product;

import java.util.List;

public class ProductMVP {
    public interface PresenterOps {
        // Presenter methods => View acess

    }

    public interface ModelOps {
        // Model methods => Presenter acess

    }

    public interface ReqPresenterOps {
        // Presenter methods => Model acess

    }

    public interface ReqViewOps {
        //View methods => Presenter access

    }
}

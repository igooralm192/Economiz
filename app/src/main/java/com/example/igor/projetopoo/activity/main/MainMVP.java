package com.example.igor.projetopoo.activity.main;

public interface MainMVP {
    public interface PresenterOps {
        // Presenter methods => View acess
        //void doSomething();
    }

    public interface ModelOps {
        // Model methods => Presenter acess
        //void catchSomething();
    }

    public interface ReqPresenterOps {
        // Presenter methods => Model acess
        //void returnSomething();
    }

    public interface ReqViewOps {
        // View methods => Presenter access
        //void showSomething();
    }
}

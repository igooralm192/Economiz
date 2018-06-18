package com.example.igor.projetopoo;

import android.graphics.drawable.TransitionDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;


public class MainActivity extends AppCompatActivity implements OnSearchActionListener {

    FrameLayout blackBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blackBar = (FrameLayout) findViewById(R.id.blackBar);
        MaterialSearchBar searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);

        searchBar.setOnSearchActionListener(this);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        TransitionDrawable background = (TransitionDrawable) blackBar.getBackground();

        if (enabled) {
            background.startTransition(300);
        } else {
            background.reverseTransition(300);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}

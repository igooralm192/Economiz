package com.example.igor.projetopoo.activity.category;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.utils.Animation;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class CategoryActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    private Toolbar toolbar;
    private FrameLayout blackLayout;
    private MaterialSearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        blackLayout = findViewById(R.id.black_category);
        searchBar = findViewById(R.id.search_bar_category);

        toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Categorias");

        searchBar.setOnSearchActionListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_category, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) finish();
        else if (id == R.id.search_icon_category) {
            Animation.openSearch(searchBar, getSupportActionBar(), blackLayout);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (enabled) {

        } else {
            Animation.closeSearch(searchBar, getSupportActionBar(), blackLayout);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}

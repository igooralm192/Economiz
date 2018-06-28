package com.example.igor.projetopoo.activity.main;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.entities.Item;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnSearchActionListener {

    FrameLayout blackBar;
    MaterialSearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blackBar = (FrameLayout) findViewById(R.id.blackBar);

        makeSearchBar();
    }

    private void makeSearchBar() {
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(inflater);

        List<Item> suggestions = new ArrayList<Item>();
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Abacaxi"));
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Banana"));
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Carne"));
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Amendoim"));

        customSuggestionsAdapter.setSuggestions(suggestions);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                customSuggestionsAdapter.getFilter().filter(searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        searchBar.setOnSearchActionListener(this);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        TransitionDrawable background = (TransitionDrawable) blackBar.getBackground();

        if (enabled) {
            searchBar.hideSuggestionsList();
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

package com.example.igor.projetopoo.activity.search;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.example.igor.projetopoo.R;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import static com.example.igor.projetopoo.R.id.toolbar;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        MaterialSearchBar searchBar = findViewById(toolbar);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(inflater);
        List<Product> suggestions = new ArrayList<>();

        // TODO: Popular a lista de sugestões com os produtos vindos do banco de dados
        /* for (int i = 0; i < 10; i++){
            suggestions.add(new Product("Produto" + i, i *10.0 ));
        }*/

        customSuggestionsAdapter.setSuggestions(suggestions);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}

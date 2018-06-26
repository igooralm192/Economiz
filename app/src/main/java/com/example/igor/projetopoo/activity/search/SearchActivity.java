package com.example.igor.projetopoo.activity.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.entities.Item;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MaterialSearchBar searchBar = findViewById(R.id.search_searchbar);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(inflater);
        List<Item> suggestions = new ArrayList<>();

        // TODO: Popular a lista de sugestões com os produtos vindos do banco de dados
        /* for (int i = 0; i < 10; i++){
            suggestions.add(new Product("Produto" + i, i *10.0 ));
        }*/

        customSuggestionsAdapter.setSuggestions(suggestions);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

    }

}

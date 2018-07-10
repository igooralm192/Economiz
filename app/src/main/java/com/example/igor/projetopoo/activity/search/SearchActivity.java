package com.example.igor.projetopoo.activity.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.main.MainActivity;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.entities.Item;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements
        MaterialSearchBar.OnSearchActionListener,
        SuggestionAdapter.OnItemViewClickListener {

    private MaterialSearchBar searchBar;
    private FrameLayout blackBackground;
    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;
    private SharedPreferences sharedPreferences;
    private static final String RECENT_QUERY = "Recent Queries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String mainMessage = intent.getStringExtra(MainActivity.RECENT_MESSAGE);

        searchBar = findViewById(R.id.search_searchbar);
        blackBackground = findViewById(R.id.black_search);
        sharedPreferences = getSharedPreferences(RECENT_QUERY, 0);

        blackBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBar.isSearchEnabled())
                    searchBar.disableSearch();
            }
        });

        recentQueries = loadRecentQueries();
        recentQueriesClone = new ArrayList<>(recentQueries);

        final SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(getLayoutInflater());
        customSuggestionsAdapter.setOnItemViewClickListener(this);
        customSuggestionsAdapter.setSuggestions(recentQueries);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

        searchBar.setPlaceHolder(mainMessage);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();

                if (s.length() > 0) {
                    List<Item> recent = new ArrayList<>();
                    for (Item item: recentQueries) {
                        if (recent.size() >= 2) break;
                        if (item.getName().toLowerCase().startsWith( s.toLowerCase() ))
                            recent.add(item);
                    }

                    // recent.addAll(getFilteredProducts());
                    // recent.addAll(getFilteredCategories());

                    searchBar.updateLastSuggestions(recent);
                } else {
                    if (searchBar.isSearchEnabled())
                        searchBar.updateLastSuggestions(recentQueriesClone);

                }

            }

        });

        searchBar.setOnSearchActionListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        List<Item> recent = new ArrayList<>();

        for (Object object: searchBar.getLastSuggestions()) {
            Item item = (Item) object;

            if (item.getType().equals("recent")) {
                recent.add(item);
            }
        }

        saveRecentQueries(recent);
    }

    private void saveRecentQueries(List<Item> recent) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray array = new JSONArray();

        for (Item item : recent) {
            JSONObject object = item.toJson();
            array.put(object.toString());
        }
        Log.i("TAG", array.toString());
        editor.putString("recent", array.toString());
        editor.apply();
    }

    private List<Item> loadRecentQueries() {
        List<Item> recent = new ArrayList<>();

        try {
            String arrayStr = sharedPreferences.getString("recent", null);

            JSONArray array = new JSONArray(arrayStr);

            for (int i=0; i<array.length(); i++) {
                JSONObject object = new JSONObject((String) array.get(i));

                Item item = new Item(
                        object.getInt("idIcon"),
                        object.getString("name"),
                        object.getString("type")
                );

                recent.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recent;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        TransitionDrawable background = (TransitionDrawable) blackBackground.getBackground();

        if (enabled) background.startTransition(300);
        else background.reverseTransition(300);
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String newText = text.toString();
        searchBar.setPlaceHolder(newText);

        Item item = new Item(R.drawable.ic_history_black_24dp, newText, "recent");
        if (text.length() != 0)
            if (!recentQueriesClone.contains(item)) {
                if (recentQueriesClone.size() == 2) recentQueriesClone.remove(1);

                recentQueriesClone.add(0, item);
                recentQueries.add(item);
            }


        searchBar.setLastSuggestions(recentQueriesClone);
        searchBar.disableSearch();
    }

    @Override
    public void onButtonClicked(int buttonCode) {


    }

    @Override
    public void onItemClick(View view) {
        TextView query = view.findViewById(R.id.name_suggestion);

        searchBar.disableSearch();
        searchBar.setPlaceHolder(query.getText());
    }
}

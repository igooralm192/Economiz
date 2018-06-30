package com.example.igor.projetopoo.activity.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.entities.Item;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnSearchActionListener, SuggestionAdapter.OnItemViewClickListener {

    FrameLayout blackBar;
    MaterialSearchBar searchBar;
    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;
    private SharedPreferences sharedPreferences;
    private static final String RECENT_QUERY = "Recent Queries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blackBar = (FrameLayout) findViewById(R.id.blackBar);

        //makeSearchBar();
    }

    private void makeSearchBar() {
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);

        final SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(getLayoutInflater());

        /*List<Item> suggestions = new ArrayList<Item>();
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Abacaxi", "recent"));
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Banana", "recent"));
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Carne", "product"));
        suggestions.add(new Item(R.mipmap.ic_launcher_round, "Amendoim", "category"));*/

        sharedPreferences = getSharedPreferences(RECENT_QUERY, 0);

        recentQueries = loadRecentQueries();
        recentQueriesClone = new ArrayList<>(recentQueries);

        customSuggestionsAdapter.setOnItemViewClickListener(this);
        customSuggestionsAdapter.setSuggestions(recentQueries);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //customSuggestionsAdapter.getFilter().filter(searchBar.getText());
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
}

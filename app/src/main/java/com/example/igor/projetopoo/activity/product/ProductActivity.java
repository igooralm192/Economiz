package com.example.igor.projetopoo.activity.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;

import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.example.igor.projetopoo.utils.Animation;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener, SuggestionAdapter.OnItemViewClickListener {

    private  MaterialSearchBar searchBar;
    private FrameLayout blackBar;

    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;
    private SharedPreferences sharedPreferences;
    private static final String RECENT_QUERY = "Recent Queries";
    public static final String RECENT_MESSAGE = "search.name.recent";

    Map<String, Class> index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        searchBar = findViewById(R.id.product_search_bar);
        searchBar.setOnSearchActionListener(this);
        blackBar = findViewById(R.id.black_bar);
        sharedPreferences = getSharedPreferences(RECENT_QUERY, 0);
        recentQueries = loadRecentQueries();
        recentQueriesClone = new ArrayList<>(recentQueries);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppBarLayout apbar = findViewById(R.id.appbar);

        apbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CardView card = findViewById(R.id.ProductCard);
                card.setAlpha((float)(1+(verticalOffset/400.0)));
                if(card.getAlpha()==0)card.setVisibility(View.GONE);
                card.setTranslationY(verticalOffset);
                return;
            }
        });
        final SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(getLayoutInflater());


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
                        if (item.getName().toLowerCase().startsWith( s.trim().toLowerCase() ))
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


    }













    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        if(id == R.id.app_bar_search){
            Animation.openSearch(searchBar, getSupportActionBar(), blackBar);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(enabled){

        } else{
            Animation.closeSearch(searchBar, getSupportActionBar(), blackBar);

        }
    }


    @Override
    public void onSearchConfirmed(CharSequence text) {
        String newText = text.toString();
        newText = newText.trim();

        if (newText.length() != 0) {
            Item item = new Item(R.drawable.ic_history_black_24dp, newText, "recent");
            if (text.length() != 0)
                if (!recentQueriesClone.contains(item)) {
                    if (recentQueriesClone.size() == 2) recentQueriesClone.remove(1);

                    recentQueriesClone.add(0, item);
                    recentQueries.add(item);
                }

            searchBar.setLastSuggestions(recentQueriesClone);

            this.saveRecentQueries(recentQueriesClone);

            this.startActivity(newText, SearchActivity.class, RECENT_MESSAGE);

            searchBar.disableSearch();
        }else{
            searchBar.showSuggestionsList();
        }
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void onItemClick(View view) {
        TextView query = view.findViewById(R.id.name_suggestion);

        searchBar.setLastSuggestions(recentQueriesClone);
        searchBar.disableSearch();

        index = new HashMap<String, Class>();
        index.put("recent", SearchActivity.class);
        index.put("product", ProductActivity.class);
//        index.put("category", );

        for (String type : index.keySet()) {
            Item item = new Item(R.drawable.ic_history_black_24dp, query.getText().toString(), type);
            int indItem = recentQueries.indexOf(item);

            if (indItem != -1) {
                this.startActivity(query.getText().toString(), index.get(type), RECENT_MESSAGE);
            }
        }
        finish();
    }

    private void startActivity(String text, Class activity, String keyMessage) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(keyMessage, text);
        startActivity(intent);
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

            if (arrayStr != null) {
                JSONArray array = new JSONArray(arrayStr);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = new JSONObject((String) array.get(i));

                    Item item = new Item(
                            object.getInt("idIcon"),
                            object.getString("name"),
                            object.getString("type")
                    );

                    recent.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recent;
    }
}

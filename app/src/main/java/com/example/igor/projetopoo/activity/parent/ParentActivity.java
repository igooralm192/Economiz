package com.example.igor.projetopoo.activity.parent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.category.CategoryActivity;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.helper.Constant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParentActivity extends AppCompatActivity implements
        MaterialSearchBar.OnSearchActionListener,
        SuggestionAdapter.OnItemViewClickListener,
        ListGenericAdapter.OnItemViewClickListener {

    private Context context;
    private FrameLayout blackLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialSearchBar searchBar;

    private Database database;
    private SharedPreferences sharedPreferences;

    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;

    private List<Category> categoriesSuggestions;
    private List<Product> productsSuggestions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new Database(FirebaseFirestore.getInstance());
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFERENCES, 0);

        Toast.makeText(this, "Conectando", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String newText = text.toString().trim();

        if (newText.length() != 0) {
            Item item = new Item(R.drawable.ic_history_black_24dp, newText, "recent", null);

            if (!recentQueriesClone.contains(item)) {
                if (recentQueriesClone.size() == 2) recentQueriesClone.remove(1);

                recentQueriesClone.add(0, item);
                recentQueries.add(item);
            }

            searchBar.setLastSuggestions(recentQueriesClone);

            this.saveRecentQueries(recentQueriesClone);

            Map<String, String> map = new HashMap<>();
            map.put(Constant.LAST_QUERY, newText);

            this.startActivity(SearchActivity.class, map);

            searchBar.disableSearch();
        } else searchBar.showSuggestionsList();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void onItemClick(View view) {
        TextView query = view.findViewById(R.id.name_suggestion);

        Map<String, Class> index = new HashMap<>();

        index.put(Constant.Entities.Item.TYPE_RECENT, SearchActivity.class);
        //index.put(Constant.Entities.Item.TYPE_PRODUCT, ProductActivity.class);
        index.put(Constant.Entities.Item.TYPE_CATEGORY, CategoryActivity.class);

        for (String type : index.keySet()) {
            Item item = new Item(R.drawable.ic_history_black_24dp, query.getText().toString(), type, null);
            int indItem = getSearchBar().getLastSuggestions().indexOf(item);

            if (indItem != -1) {
                if (type.equals("category")) {
                    List<Item> list = (List<Item>) getSearchBar().getLastSuggestions();
                    this.onCategoryClick((Category) list.get(indItem).getObject());
                } else if (type.equals("product")) {
                    List<Item> list = (List<Item>) getSearchBar().getLastSuggestions();
                    this.onProductClick((Product) list.get(indItem).getObject());
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put(Constant.LAST_QUERY, query.getText().toString());

                    this.startActivity(index.get(type), map);
                }

                searchBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchBar.setLastSuggestions(getRecentQueriesClone());
                        searchBar.disableSearch();
                    }
                }, 300);

            }
        }
    }

    @Override
    public void onCategoryClick(Category category) {

    }

    @Override
    public void onProductClick(Product product) {

    }

    public void filterSuggestions(String query) {
        List<Category> categories = categoriesSuggestions;
        List<Product> products = productsSuggestions;

        List<Item> newSuggestions = new ArrayList<>();

        int countRecent = 0, countProduct = 0, countCategories = 0;

        for (Item item: recentQueries) {
            if (countRecent >= 2) break;
            if (item.getName().toLowerCase().startsWith( query.toLowerCase() )) {
                newSuggestions.add(item);
                countRecent++;
            }
        }

        for (Product product: products) {
            if (countProduct >= 4 - countRecent) break;

            if (product.getName().toLowerCase().startsWith( query.toLowerCase() )) {
                Item item = new Item(
                        R.drawable.ic_shopping_cart_red_32dp,
                        product.getName(),
                        "product",
                        product,
                        String.format("R$ %.2f", product.getAveragePrice())
                );

                newSuggestions.add(item);
                countProduct++;
            }
        }

        for (Category category: categories) {
            if (countCategories >= 6 - (countRecent + countProduct)) break;

            if (category.getName().toLowerCase().startsWith( query.toLowerCase() )) {
                Item item = new Item(
                        R.drawable.ic_search_black_24dp,
                        category.getName(),
                        "category",
                        category
                );

                newSuggestions.add(item);
                countCategories++;
            }
        }

        searchBar.updateLastSuggestions(newSuggestions);
    }

    public void setAllSuggestions() {
        List<Category> categories = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        String json = sharedPreferences.getString(Constant.ALL_SUGGESTIONS, null);

        if (json != null) {
            try {
                JSONObject suggestions = new JSONObject(json);

                JSONArray arrCategories = suggestions.getJSONArray("categories");
                JSONArray arrProducts = suggestions.getJSONArray("products");

                for (int i=0; i<arrCategories.length(); i++) {
                    Category category = Category.toObject(arrCategories.getJSONObject(i));
                    categories.add(category);
                }

                for (int i=0; i<arrProducts.length(); i++) {
                    Product product = Product.toObject(arrProducts.getJSONObject(i));
                    products.add(product);
                }

                this.categoriesSuggestions = categories;
                this.productsSuggestions = products;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveRecentQueries(List<Item> recent) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray array = new JSONArray();

        for (Item item : recent) {
            JSONObject object = item.toJson();
            array.put(object.toString());
        }

        editor.putString(Constant.RECENT_QUERIES, array.toString());
        editor.apply();
    }

    public List<Item> loadRecentQueries() {
        List<Item> recent = new ArrayList<>();

        try {
            String arrayStr = sharedPreferences.getString(Constant.RECENT_QUERIES, null);

            if (arrayStr != null) {
                JSONArray array = new JSONArray(arrayStr);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = new JSONObject((String) array.get(i));

                    Object obj = null;
                    Category category = null;
                    Product product = null;

                    if (object.getString("type").equals("category")) {
                        category = Category.toObject(object.getJSONObject("object"));
                        obj = category;
                    } else if (object.getString("type").equals("product")) {
                        product = Product.toObject(object.getJSONObject("object"));
                        obj = product;
                    }

                    Item item = new Item(
                            object.getInt("idIcon"),
                            object.getString("name"),
                            object.getString("type"),
                            obj,
                            object.getString("price")
                    );

                    recent.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recent;
    }

    public void createSearchBar() {
        recentQueries = loadRecentQueries();
        recentQueriesClone = new ArrayList<>(recentQueries);

        SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(getLayoutInflater());
        customSuggestionsAdapter.setOnItemViewClickListener(this);
        customSuggestionsAdapter.setSuggestions(recentQueries);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString().trim();

                if (query.length() > 0) {
                    filterSuggestions(query);
                } else {
                    if (searchBar.isSearchEnabled())
                        searchBar.updateLastSuggestions(recentQueriesClone);
                }
            }

        });

        searchBar.setOnSearchActionListener(this);
    }

    public void startActivity(Class activity, Map<String, String> extras) {
        Intent intent = new Intent(this, activity);

        for (String key: extras.keySet())
            intent.putExtra(key, extras.get(key));

        startActivity(intent);
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // -------------------------- ABSTRACT METHODS --------------------------

    public abstract void init();

    // -------------------------- GETTERS AND SETTERS --------------------------

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FrameLayout getBlackLayout() {
        return blackLayout;
    }

    public void setBlackLayout(FrameLayout blackLayout) {
        this.blackLayout = blackLayout;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public MaterialSearchBar getSearchBar() {
        return searchBar;
    }

    public void setSearchBar(MaterialSearchBar searchBar) {
        this.searchBar = searchBar;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public List<Item> getRecentQueries() {
        return recentQueries;
    }

    public void setRecentQueries(List<Item> recentQueries) {
        this.recentQueries = recentQueries;
    }

    public List<Item> getRecentQueriesClone() {
        return recentQueriesClone;
    }

    public void setRecentQueriesClone(List<Item> recentQueriesClone) {
        this.recentQueriesClone = recentQueriesClone;
    }

    public List<Category> getCategoriesSuggestions() {
        return categoriesSuggestions;
    }

    public void setCategoriesSuggestions(List<Category> categoriesSuggestions) {
        this.categoriesSuggestions = categoriesSuggestions;
    }

    public List<Product> getProductsSuggestions() {
        return productsSuggestions;
    }

    public void setProductsSuggestions(List<Product> productsSuggestions) {
        this.productsSuggestions = productsSuggestions;
    }
}

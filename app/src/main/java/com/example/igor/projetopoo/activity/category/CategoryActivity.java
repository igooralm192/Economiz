package com.example.igor.projetopoo.activity.category;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.example.igor.projetopoo.utils.Animation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity implements
        MaterialSearchBar.OnSearchActionListener,
        SuggestionAdapter.OnItemViewClickListener,
        CategoryMVP.ReqViewOps,
        ListGenericAdapter.OnItemViewClickListener {

    private Context context;
    private Toolbar toolbar;
    private FrameLayout blackLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialSearchBar searchBar;
    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;
    private List<Category> categoriesSuggestions;
    private List<Product> productsSuggestions;
    private SharedPreferences sharedPreferences;

    private CategoryMVP.PresenterOps presenterOps;
    private Map<Category, Category> categoryLinks = new HashMap<>();
    private Category currentCategory;
    private static final String RECENT_QUERY = "Recent Queries";
    public static final String RECENT_MESSAGE = "search.name.recent";
    public static final String SELECTED_PRODUCT = "Selected Product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        presenterOps = new CategoryPresenter(this, new Database(FirebaseFirestore.getInstance()));

        blackLayout = findViewById(R.id.black_category);
        searchBar = findViewById(R.id.search_bar_category);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        sharedPreferences = getSharedPreferences(RECENT_QUERY, 0);
        context = getApplicationContext();

        toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);

        createSearchBar();

        Intent intent = getIntent();
        //String category = intent.getStringExtra("category");
        Category category = new Category("Alimentos", "", true);
        currentCategory = category;
        getSupportActionBar().setTitle(currentCategory.getName());
        categoryLinks.put(category, null);

        presenterOps.getCategory(category);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterOps.getCategory(currentCategory);
            }
        });

        searchBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                configSuggestions();
            }
        }, 1000);

    }

    private void createSearchBar() {
        final SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(getLayoutInflater());

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
    public void onBackPressed() {
        Integer count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) finish();
        else {
            toolbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportActionBar().setTitle( categoryLinks.get(currentCategory).getName() );
                    currentCategory = categoryLinks.get(currentCategory);
                    if (categoryLinks.get(currentCategory) == null) toolbar.setSubtitle("");
                    else toolbar.setSubtitle(currentCategory.getName());
                }
            }, 500);

            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_category, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) this.onBackPressed();
        else if (id == R.id.search_icon_category) {
            Animation.openSearch(searchBar, getSupportActionBar(), blackLayout);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            Animation.closeSearch(searchBar, getSupportActionBar(), blackLayout);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String newText = text.toString();
        newText = newText.trim();

        if (newText.length() != 0) {
            Item item = new Item(R.drawable.ic_history_black_24dp, newText, "recent");

            if (!recentQueriesClone.contains(item)) {
                if (recentQueriesClone.size() == 2) recentQueriesClone.remove(1);

                recentQueriesClone.add(0, item);
                recentQueries.add(item);
            }

            searchBar.setLastSuggestions(recentQueriesClone);

            this.saveRecentQueries(recentQueriesClone);

            this.startActivity(newText, SearchActivity.class, RECENT_MESSAGE);

            searchBar.disableSearch();
        } else searchBar.showSuggestionsList();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void onItemClick(View view) {
        TextView query = view.findViewById(R.id.name_suggestion);

        searchBar.setLastSuggestions(recentQueriesClone);
        searchBar.disableSearch();

        Map<String, Class> index = new HashMap<>();
        index.put("recent", SearchActivity.class);
        //index.put("product", );
        index.put("category", CategoryActivity.class);

        for (String type : index.keySet()) {
            Item item = new Item(R.drawable.ic_history_black_24dp, query.getText().toString(), type);
            int indItem = recentQueries.indexOf(item);

            if (indItem != -1) {
                this.startActivity(query.getText().toString(), index.get(type), RECENT_MESSAGE);
            }
        }
    }

    @Override
    public void showSubcategories(List<Category> subcategories) {
        final ListGenericAdapter<Category, Category.Holder> adapter = new ListGenericAdapter<>(
                context,
                subcategories,
                new ListAdapter<Category, Category.Holder>() {
                    @Override
                    public Category.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = getLayoutInflater().inflate(R.layout.item_list_category, parent, false);

                        return new Category.Holder(view, CategoryActivity.this);
                    }

                    @Override
                    public void onBindViewHolder(List<Category> items, @NonNull Category.Holder holder, int position) {
                        holder.setCategory(items.get(position));
                        holder.name.setText(items.get(position).getName());
                    }
                }
        );

        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(adapter);
                lista.setLayoutManager(new LinearLayoutManager(context));
                lista.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                lista.setItemAnimator(new DefaultItemAnimator());

                return lista;
            }
        });

        changeListFragment(listFragment);
    }

    @Override
    public void showProducts(List<Product> products) {
        final ListGenericAdapter<Product, Product.Holder> adapter = new ListGenericAdapter<>(
                context,
                products,
                new ListAdapter<Product, Product.Holder>() {
                    @Override
                    public Product.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = getLayoutInflater().inflate(R.layout.item_list_product, parent, false);

                        return new Product.Holder(view, CategoryActivity.this);
                    }

                    @Override
                    public void onBindViewHolder(List<Product> items, @NonNull Product.Holder holder, int position) {
                        holder.setProduct(items.get(position));
                        holder.name.setText(items.get(position).getName());
                        holder.averagePrice.setText("R$ " + String.format("%.2f", items.get(position).getAveragePrice()));
                    }
                }
        );

        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(adapter);
                lista.setLayoutManager(new LinearLayoutManager(context));
                lista.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                lista.setItemAnimator(new DefaultItemAnimator());

                return lista;
            }
        });

        changeListFragment(listFragment);
    }

    @Override
    public void showProgressBar(Boolean enabled) {
        swipeRefreshLayout.setRefreshing(enabled);
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

    private void changeListFragment(ListFragment newFragment) {
        FragmentManager manager = getSupportFragmentManager();
        final Category oldcategory = categoryLinks.get( currentCategory );
        Fragment oldFragment;

        if (oldcategory != null) {
            oldFragment = manager.findFragmentByTag(oldcategory.getName());

            Slide slide = new Slide();
            slide.setDuration(500);

            slide.setSlideEdge(Gravity.END);
            oldFragment.setEnterTransition(slide);

            slide.setSlideEdge(Gravity.START);
            oldFragment.setExitTransition(slide);
        } else {
            Integer count = manager.getBackStackEntryCount();

            if (count == 0) {
                oldFragment = manager.findFragmentByTag(currentCategory.getName());
                if (oldFragment != null) {
                    Slide slide = new Slide();
                    slide.setDuration(500);

                    slide.setSlideEdge(Gravity.START);
                    oldFragment.setExitTransition(slide);
                }
            }
        }

        Slide slide2 = new Slide();
        slide2.setDuration(500);

        slide2.setSlideEdge(Gravity.START);
        newFragment.setEnterTransition(slide2);

        slide2.setSlideEdge(Gravity.END);
        newFragment.setExitTransition(slide2);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_category, newFragment, currentCategory.getName());
        if (oldcategory != null) transaction.addToBackStack(null);
        transaction.commit();

        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(currentCategory.getName());
                if (oldcategory != null) toolbar.setSubtitle( oldcategory.getName() );
                else toolbar.setSubtitle("");
            }
        }, 500);

    }

    @Override
    public void onCategoryClick(Category category) {
        categoryLinks.put(category, currentCategory);
        currentCategory = category;

        presenterOps.getCategory(category);
    }

    @Override
    public void onProductClick(Product product) {
        //String json = product.toJSON().toString();

        //startActivity(json, ProductActivity.class, SELECTED_PRODUCT);
    }

    @Override
    public void saveAllSuggestions(List<Category> categories, List<Product> products) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            JSONObject suggestions = new JSONObject();

            JSONArray arrCategories = new JSONArray();
            JSONArray arrProducts = new JSONArray();

            for (Category category: categories)
                arrCategories.put(category.toJSON());


            suggestions.put("categories", arrCategories);

            for (Product product: products)
                arrProducts.put(product.toJSON());


            suggestions.put("products", arrProducts);

            editor.putString("suggestions", suggestions.toString());
            editor.apply();

            setAllSuggestions();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void configSuggestions() {
        String sug = sharedPreferences.getString("suggestions", null);

        if (sug == null) presenterOps.getAllSuggestions();
        else setAllSuggestions();
    }

    private void filterSuggestions(String query) {
        List<Category> categories = categoriesSuggestions;
        List<Product> products = productsSuggestions;
        List<Item> newSuggestions = new ArrayList<>();

        int countRecent = 0, countProduct = 0;

        for (Item item: recentQueries) {
            if (countRecent >= 2) break;
            if (item.getName().toLowerCase().startsWith( query.toLowerCase() )) {
                newSuggestions.add(item);
                countRecent++;
            }
        }

        for (Product product: products) {
            if (products.size() >= 4 - countRecent) break;

            if (product.getName().toLowerCase().startsWith( query.toLowerCase() )) {
                Item item = new Item(
                        R.drawable.ic_shopping_cart_red_32dp,
                        product.getName(),
                        "product",
                        String.format("R$ %.2f", product.getAveragePrice()));

                newSuggestions.add(item);
                countProduct++;
            }
        }

        for (Category category: categories) {
            if (categories.size() >= 6 - (countRecent + countProduct)) break;

            if (category.getName().toLowerCase().startsWith( query.toLowerCase() )) {
                Item item = new Item(
                        R.drawable.ic_search_black_24dp,
                        category.getName(),
                        "category"
                );

                newSuggestions.add(item);
            }
        }

        searchBar.updateLastSuggestions(newSuggestions);
    }

    private void setAllSuggestions() {
        List<Category> categories = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        String json = sharedPreferences.getString("suggestions", null);

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
}

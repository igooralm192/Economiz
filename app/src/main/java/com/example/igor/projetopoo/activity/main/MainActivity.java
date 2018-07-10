package com.example.igor.projetopoo.activity.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
import com.example.igor.projetopoo.helper.Blur;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements
        OnSearchActionListener,
        SuggestionAdapter.OnItemViewClickListener,
        MainMVP.ReqViewOps,
        ListGenericAdapter.OnItemViewClickListener {

    private Context context;

    private FrameLayout blackBar;
    private RelativeLayout appBar;

    private Boolean suggestionsStatus = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialSearchBar searchBar;
    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;
    private SharedPreferences sharedPreferences;
    private static final String RECENT_QUERY = "Recent Queries";
    public static final String RECENT_MESSAGE = "search.name.recent";
    private List<Category> categoriesSuggestions;
    private List<Product> productsSuggestions;

    Map<String, Class> index;

    private MainMVP.PresenterOps presenterOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        presenterOps = new MainPresenter(this, new Database(FirebaseFirestore.getInstance()));

        blackBar = (FrameLayout) findViewById(R.id.blackBar);
        appBar = (RelativeLayout) findViewById(R.id.appBar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_main);

        makeSearchBar();

        //setCategoryList(this);
        configSuggestions();
        if (suggestionsStatus) presenterOps.getCategoryList();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterOps.getCategoryList();
            }
        });


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
    public void showCategories(List<Category> categories) {
        final ListGenericAdapter<Category, Category.MainHolder> listGenericAdapter = new ListGenericAdapter<Category, Category.MainHolder>(
                context,
                categories,
                new ListAdapter<Category, Category.MainHolder>() {
                    @Override
                    public Category.MainHolder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(context).inflate(R.layout.item_list_main_category, parent, false);

                        Category.MainHolder holder = new Category.MainHolder(view, MainActivity.this);

                        return holder;
                    }

                    @Override
                    public void onBindViewHolder(List<Category> items, @NonNull Category.MainHolder holder, int position) {
                        Category category = items.get(position);
                        holder.name.setText(category.getName());
                        holder.background.setImageResource(category.getBackground());

                        Blur.blurImage(holder.background, 5, context);
                    }
                });

        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(listGenericAdapter);
                lista.setLayoutManager(new LinearLayoutManager(context));
                lista.setItemAnimator(new DefaultItemAnimator());

                return lista;
            }
        });

        changeListFragment(listFragment);
    }

    private void changeListFragment(ListFragment newFragment) {
        FragmentManager manager = getSupportFragmentManager();
        //final Category oldcategory = categoryLinks.get( currentCategory );
        Fragment oldFragment;

        /*if (oldcategory != null) {
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
        }*/

        Slide slide2 = new Slide();
        slide2.setDuration(500);
        slide2.setSlideEdge(Gravity.START);

        newFragment.setEnterTransition(slide2);

        Fade fade = new Fade();
        fade.setDuration(350);
        newFragment.setExitTransition(fade);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.constraint_layout_main, newFragment);
        //if (oldcategory != null) transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void showProgressBar(Boolean enabled) {
        swipeRefreshLayout.setRefreshing(enabled);
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
        //index.put("product", );
        //index.put("category", );

        for (String type : index.keySet()) {
            Item item = new Item(R.drawable.ic_history_black_24dp, query.getText().toString(), type);
            int indItem = recentQueries.indexOf(item);

            if (indItem != -1) {
                this.startActivity(query.getText().toString(), index.get(type), RECENT_MESSAGE);
            }
        }
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

    private void setCategoryList(final Context context) {
        ArrayList<Category> categories = new ArrayList<Category>();
        categories.add(new Category("Alimentos", R.drawable.food));
        categories.add(new Category("Limpeza", R.drawable.cleaning2));
        categories.add(new Category("Farmácia", R.drawable.farmacia));
        categories.add(new Category("Zoológico", R.drawable.food));

        final RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        final ListGenericAdapter<Category, Category.MainHolder> listGenericAdapter = new ListGenericAdapter<Category, Category.MainHolder>(
                context,
                categories,
                new ListAdapter<Category, Category.MainHolder>() {
                    @Override
                    public Category.MainHolder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(context).inflate(R.layout.item_list_main_category, parent, false);

                        Category.MainHolder holder = new Category.MainHolder(view, MainActivity.this);

                        return holder;
                    }

                    @Override
                    public void onBindViewHolder(List<Category> items, @NonNull Category.MainHolder holder, int position) {
                        Category category = items.get(position);
                        holder.name.setText(category.getName());
                        holder.background.setImageResource(category.getBackground());

                        Blur.blurImage(holder.background, 5, context);
                    }
                });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Exemplo de instanciação do ListFragment
        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(listGenericAdapter);
                lista.setLayoutManager(layout);

                return lista;
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {

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

            suggestionsStatus = true;
            searchBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    presenterOps.getCategoryList();
                }
            }, 1000);

            setAllSuggestions();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void configSuggestions() {
        String sug = sharedPreferences.getString("suggestions", null);

        if (sug != null) {
            suggestionsStatus = true;
            setAllSuggestions();
        } else presenterOps.getAllSuggestions(this);
    }

    private void filterSuggestions(String query) {
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
                        String.format("R$ %.2f", product.getAveragePrice()));

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
                        "category"
                );

                newSuggestions.add(item);
                countCategories++;
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

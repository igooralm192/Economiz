package com.example.igor.projetopoo.activity.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.category.CategoryActivity;
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.example.igor.projetopoo.helper.Blur;
import com.example.igor.projetopoo.helper.Constant;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ParentActivity implements MainMVP.ReqViewOps {

    private Boolean suggestionsStatus = false;

    private RelativeLayout appBar;
    private MainMVP.PresenterOps presenterOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        createSearchBar();
        configSuggestions();

        if (suggestionsStatus) presenterOps.getCategoryList();

        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterOps.getCategoryList();
            }
        });

    }

    @Override
    public void init() {
        setContext(getApplicationContext());
        setBlackLayout( (FrameLayout) findViewById(R.id.black_bar));
        setSearchBar( (MaterialSearchBar) findViewById(R.id.search_bar_main) );
        setSwipeRefreshLayout( (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_main));

        appBar = (RelativeLayout) findViewById(R.id.app_bar);
        presenterOps = new MainPresenter(this, getDatabase());
    }

    @Override
    public void showCategories(List<Category> categories) {
        final ListGenericAdapter<Category, Category.MainHolder> listGenericAdapter = new ListGenericAdapter<Category, Category.MainHolder>(
                getContext(),
                categories,
                new ListAdapter<Category, Category.MainHolder>() {
                    @Override
                    public Category.MainHolder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(context).inflate(R.layout.item_list_main_category, parent, false);

                        return new Category.MainHolder(view, MainActivity.this);
                    }

                    @Override
                    public void onBindViewHolder(List<Category> items, @NonNull Category.MainHolder holder, int position) {
                        Category category = items.get(position);
                        holder.setCategory(category);
                        holder.name.setText(category.getName());
                        holder.background.setImageResource(category.getBackground());

                        Blur.blurImage(holder.background, 5, getContext());
                    }
                });

        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(listGenericAdapter);
                lista.setLayoutManager(new LinearLayoutManager(getContext()));
                lista.setItemAnimator(new DefaultItemAnimator());

                return lista;
            }
        });

        changeListFragment(listFragment);
    }

    @Override
    public void showProgressBar(Boolean enabled) {
        getSwipeRefreshLayout().setRefreshing(enabled);
    }

    @Override
    public void saveAllSuggestions(List<Category> categories, List<Product> products) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        try {
            JSONObject suggestions = new JSONObject();

            JSONArray arrCategories = new JSONArray();
            JSONArray arrProducts = new JSONArray();

            for (Category category: categories)
                arrCategories.put(category.toJSON());


            suggestions.put(Constant.Entities.CATEGORIES, arrCategories);

            for (Product product: products)
                arrProducts.put(product.toJSON());


            suggestions.put(Constant.Entities.PRODUCTS, arrProducts);

            editor.putString(Constant.ALL_SUGGESTIONS, suggestions.toString());
            editor.apply();

            suggestionsStatus = true;

            getSearchBar().postDelayed(new Runnable() {
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

    @Override
    public void onSearchStateChanged(boolean enabled) {
        TransitionDrawable background = (TransitionDrawable) getBlackLayout().getBackground();

        if (enabled) {
            getBlackLayout().setVisibility(View.VISIBLE);
            background.startTransition(300);
        } else {
            background.reverseTransition(300);
            getBlackLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getBlackLayout().setVisibility(View.GONE);
                }
            }, 300);
        }
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

                /*getSearchBar().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSearchBar().setLastSuggestions(getRecentQueriesClone());
                        getSearchBar().disableSearch();
                    }
                }, 300);*/

            }
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        Log.i("TAG", "Na main");
        Map<String, String> map = new HashMap<>();
        map.put(Constant.SELECTED_CATEGORY, category.toJSON().toString());

        startActivity(CategoryActivity.class, map);

    }

    @Override
    public void onProductClick(Product product) {

    }

    private void changeListFragment(ListFragment newFragment) {
        FragmentManager manager = getSupportFragmentManager();

        Slide slide = new Slide();
        slide.setDuration(500);
        slide.setSlideEdge(Gravity.START);

        newFragment.setEnterTransition(slide);

        Fade fade = new Fade();
        fade.setDuration(350);
        newFragment.setExitTransition(fade);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.constraint_layout_main, newFragment);
        transaction.commit();
    }

    private void configSuggestions() {
        String sug = getSharedPreferences().getString(Constant.ALL_SUGGESTIONS, null);
        sug = null;
        if (sug != null) {
            suggestionsStatus = true;
            setAllSuggestions();
        } else presenterOps.getAllSuggestions(this);
    }
}

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
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.activity.product.ProductActivity;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.example.igor.projetopoo.helper.Constant;
import com.example.igor.projetopoo.helper.CustomDialog;
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

public class CategoryActivity extends ParentActivity implements CategoryMVP.ReqViewOps {
    private Toolbar toolbar;

    private CategoryMVP.PresenterOps presenterOps;
    private Map<Category, Category> categoryLinks = new HashMap<>();
    private Category currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        init();
        createSearchBar();

        presenterOps.getCategory(currentCategory);

        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterOps.getCategory(currentCategory);
            }
        });

    }

    @Override
    public void init() {
        setContext(getApplicationContext());
        setBlackLayout( (FrameLayout) findViewById(R.id.black_category));
        setSearchBar( (MaterialSearchBar) findViewById(R.id.search_bar_category) );
        setSwipeRefreshLayout( (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_category));

        presenterOps = new CategoryPresenter(this, getDatabase());
        toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        currentCategory = Category.toObject( intent.getStringExtra(Constant.SELECTED_CATEGORY) );

        getSupportActionBar().setTitle(currentCategory.getName());
        categoryLinks.put(currentCategory, null);

        setAllSuggestions();
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
            Animation.openSearch(getSearchBar(), getBlackLayout());
        }

        return super.onOptionsItemSelected(item);
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
    public void showSubcategories(List<Category> subcategories) {
        final ListGenericAdapter<Category, Category.CategoryHolder> adapter = new ListGenericAdapter<>(
                getContext(),
                subcategories,
                new ListAdapter<Category, Category.CategoryHolder>() {
                    @Override
                    public Category.CategoryHolder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = getLayoutInflater().inflate(R.layout.item_list_category, parent, false);

                        return new Category.CategoryHolder(view, CategoryActivity.this);
                    }

                    @Override
                    public void onBindViewHolder(List<Category> items, @NonNull Category.CategoryHolder holder, int position) {
                        holder.setCategory(items.get(position));
                        holder.name.setText(items.get(position).getName());
                    }
                }
        );

        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(adapter);
                lista.setLayoutManager(new LinearLayoutManager(getContext()));
                lista.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                lista.setItemAnimator(new DefaultItemAnimator());

                return lista;
            }
        });

        changeListFragment(listFragment);
    }

    @Override
    public void showProducts(List<Product> products) {
        final ListGenericAdapter<Product, Product.Holder> adapter = new ListGenericAdapter<>(
                getContext(),
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
                lista.setLayoutManager(new LinearLayoutManager(getContext()));
                lista.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            Animation.closeSearch(getSearchBar(), getBlackLayout());
        }
    }

    @Override
    public void onItemClick(View view) {
        TextView query = view.findViewById(R.id.name_suggestion);

        Map<String, Class> index = new HashMap<>();

        index.put(Constant.Entities.Item.TYPE_RECENT, SearchActivity.class);
        index.put(Constant.Entities.Item.TYPE_PRODUCT, ProductActivity.class);
        index.put(Constant.Entities.Item.TYPE_CATEGORY, CategoryActivity.class);

        for (String type : index.keySet()) {
            Item item = new Item(R.drawable.ic_history_black_24dp, query.getText().toString(), type, null);
            int indItem = getSearchBar().getLastSuggestions().indexOf(item);

            if (indItem != -1) {
                if (type.equals("category")) {
                    List list = getSearchBar().getLastSuggestions();
                    Item categoryItem = (Item) list.get(indItem);
                    Category category = (Category) categoryItem.getObject();

                    if (!category.getName().equals(currentCategory.getName()))
                        super.onCategoryClick(category);

                } else if (type.equals("product")) {

                    List list = getSearchBar().getLastSuggestions();
                    Item productItem = (Item) list.get(indItem);
                    this.onProductClick((Product) productItem.getObject());

                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put(Constant.LAST_QUERY, query.getText().toString());

                    startActivity(index.get(type), map);
                }

                getSearchBar().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSearchBar().setLastSuggestions(getRecentQueriesClone());
                        getSearchBar().disableSearch();
                    }
                }, 300);

            }
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        categoryLinks.put(category, currentCategory);
        currentCategory = category;

        presenterOps.getCategory(category);
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
}

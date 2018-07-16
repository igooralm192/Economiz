package com.example.igor.projetopoo.activity.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.igor.projetopoo.activity.category.CategoryActivity;
import com.example.igor.projetopoo.activity.main.MainActivity;
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.activity.product.ProductActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.entities.Result;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.igor.projetopoo.helper.Constant;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends ParentActivity implements SearchMVP.ReqViewOps {
    private String lastQuery;

    private SearchMVP.PresenterOps presenterOps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
        createSearchBar();

        presenterOps.getResultList(lastQuery);

        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterOps.getResultList(lastQuery);
            }
        });


        getBlackLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSearchBar().isSearchEnabled())
                    getSearchBar().disableSearch();
            }
        });

        getSearchBar().setCardViewElevation(8);
    }

    @Override
    public void init() {
        setContext(this);
        setBlackLayout( (FrameLayout) findViewById(R.id.black_search) );
        setSearchBar( (MaterialSearchBar) findViewById(R.id.search_searchbar) );
        setSwipeRefreshLayout( (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_search) );

        presenterOps = new SearchPresenter(this, getContext(), getDatabase());

        Intent intent = getIntent();
        lastQuery = intent.getStringExtra(Constant.LAST_QUERY);
        getSearchBar().setPlaceHolder(lastQuery);

        setAllSuggestions();
    }

    @Override
    public void showResults(List<Category> categoryList, List<Product> productList) {
        List<Result> resultList = new ArrayList<>();

        for(Category category: categoryList){
            Result result = new Result(R.drawable.ic_search_black_24dp,category.getName());
            resultList.add(result);
        }

        for(Product product: productList){
            Result result = new Result(R.drawable.ic_shopping_cart_red_32dp, product.getName(), product.getAveragePrice());
            resultList.add(result);
        }

        setResultList(this, resultList);
    }


    @Override
    public void showProgressBar(Boolean enabled) {
        getSwipeRefreshLayout().setRefreshing(enabled);
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
    public void onSearchConfirmed(CharSequence text) {
        String newText = text.toString().trim();

        if (newText.length() != 0) {
            Item item = new Item(R.drawable.ic_history_black_24dp, newText, "recent", null);

            if (!getRecentQueriesClone().contains(item)) {
                if (getRecentQueriesClone().size() == 2) getRecentQueriesClone().remove(1);

                getRecentQueriesClone().add(0, item);
                getRecentQueries().add(item);
            }

            getSearchBar().setLastSuggestions(getRecentQueriesClone());

            this.saveRecentQueries(getRecentQueriesClone());

            lastQuery = newText;

            getSearchBar().setPlaceHolder(lastQuery);
            presenterOps.getResultList(lastQuery);

            getSearchBar().disableSearch();
        } else getSearchBar().showSuggestionsList();
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
                    this.onCategoryClick((Category) categoryItem.getObject());

                } else if (type.equals("product")) {

                    List list = getSearchBar().getLastSuggestions();
                    Item productItem = (Item) list.get(indItem);
                    this.onProductClick((Product) productItem.getObject());

                } else {
                    lastQuery = query.getText().toString();

                    getSearchBar().setPlaceHolder(lastQuery);
                    presenterOps.getResultList(lastQuery);
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

    private void setResultList(final Context context, List<Result> result){

        final RecyclerView.LayoutManager layout = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);

        final ListGenericAdapter<Result, Result.Holder> listGenericAdapter = new ListGenericAdapter<>(this, result, new ListAdapter<Result, Result.Holder>() {
            @Override
            public Result.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_list_result, parent, false);
                return new Result.Holder(view);
            }

            @Override
            public void onBindViewHolder(List<Result> items, @NonNull Result.Holder holder, int position) {
                Result result = items.get(position);
                holder.iconResult.setImageResource(result.getIcon());
                holder.nameResult.setText(result.getName());
                if (result.getPrice().doubleValue() != -1)
                    holder.priceResult.setText(String.format("R$ %.2f", result.getPrice()));
                else
                    holder.priceResult.setText("");


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
                lista.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL  ));

                return lista;
            }
        });

        fragmentTransaction.add(R.id.search_container, listFragment);
        fragmentTransaction.commit();
    }
}

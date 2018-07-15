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
import com.example.igor.projetopoo.activity.main.MainActivity;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements
        MaterialSearchBar.OnSearchActionListener,
        SuggestionAdapter.OnItemViewClickListener, SearchMVP.ReqViewOps {

    private MaterialSearchBar searchBar;
    private FrameLayout blackBackground;
    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;
    private SharedPreferences sharedPreferences;
    private static final String RECENT_QUERY = "Recent Queries";
    private SearchMVP.PresenterOps presenterOps;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = findViewById(R.id.search_searchbar);
        blackBackground = findViewById(R.id.black_search);
        sharedPreferences = getSharedPreferences(RECENT_QUERY, 0);

        //Intent intent = getIntent();
        //String query = intent.getStringExtra(MainActivity.RECENT_MESSAGE);
        //searchBar.setPlaceHolder(query);


        database = new Database(FirebaseFirestore.getInstance());
        presenterOps = new SearchPresenter(this, database);


        blackBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBar.isSearchEnabled())
                    searchBar.disableSearch();
            }
        });

        searchBar.setCardViewElevation(8);
        recentQueries = loadRecentQueries();
        recentQueriesClone = new ArrayList<>(recentQueries);

        final SuggestionAdapter customSuggestionsAdapter = new SuggestionAdapter(getLayoutInflater());
        customSuggestionsAdapter.setOnItemViewClickListener(this);
        customSuggestionsAdapter.setSuggestions(recentQueries);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);

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

        presenterOps.getResultList("Al");
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

        editor.putString("recent", array.toString());
        editor.apply();
    }

    private List<Item> loadRecentQueries() {
        List<Item> recent = new ArrayList<>();

        try {
            String arrayStr = sharedPreferences.getString("recent", null);

            if (arrayStr != null) {
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recent;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        TransitionDrawable background = (TransitionDrawable) blackBackground.getBackground();


        if (enabled) {
            blackBackground.setVisibility(View.VISIBLE);
            background.startTransition(300);
        } else {
            background.reverseTransition(300);
            blackBackground.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blackBackground.setVisibility(View.GONE);
                }
            }, 300);

        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String newText = text.toString();
        newText = newText.trim();
        searchBar.setPlaceHolder(newText);

        Item item = new Item(R.drawable.ic_history_black_24dp, newText, "recent");
        if (newText.length() != 0)
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

        Map<String, Class> index = new HashMap<>();
        index.put("recent", SearchActivity.class);
        //index.put("product", );
        //index.put("category", );

        for (String type : index.keySet()) {
            Item item = new Item(R.drawable.ic_history_black_24dp, query.getText().toString(), type);
            int indItem = recentQueries.indexOf(item);

            if (indItem != -1) {
                if (type.equals("recent")) {
                    searchBar.setPlaceHolder(item.getName());
                } else {
                    //this.startActivity(query.getText().toString(), index.get(type), MainActivity.RECENT_MESSAGE);
                }
            }
        }
    }

    private void startActivity(String text, Class activity, String keyMessage) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(keyMessage, text);
        startActivity(intent);
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
                if (result.getPrice() != -1)
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

                return lista;
            }
        });

        fragmentTransaction.add(R.id.search_container, listFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void showResults(List<Category> categoryList, List<Product> productList) {
        //TODO> Implementar essa merda aqui
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
}

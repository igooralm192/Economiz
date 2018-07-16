package com.example.igor.projetopoo.activity.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Feedback;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.example.igor.projetopoo.helper.CustomDialog;
import com.example.igor.projetopoo.utils.Animation;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductActivity extends AppCompatActivity implements
        MaterialSearchBar.OnSearchActionListener,
        SuggestionAdapter.OnItemViewClickListener,
        ProductMVP.ReqViewOps {

    Map<String, Class> index;

    private  MaterialSearchBar searchBar;
    private FrameLayout blackBar;
    private CustomDialog dialog;
    private List<Item> recentQueries;
    private List<Item> recentQueriesClone;
    private SharedPreferences sharedPreferences;
    private static final String RECENT_QUERY = "Recent Queries";
    public static final String RECENT_MESSAGE = "search.name.recent";
    private Feedback feedback;
    private Context context;
    private ListFragment listFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProductMVP.PresenterOps presenterOps;
    private AppBarLayout apbar;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        context = getApplicationContext();

        presenterOps = new ProductPresenter(this, new Database(FirebaseFirestore.getInstance()));

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_feedback);

        makeInitialFeedbackList();

        presenterOps.getFeedbacks("Maçã");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterOps.getFeedbacks("Maçã");
            }
        });

        dialog= new CustomDialog(this, R.layout.dialog);
        searchBar = findViewById(R.id.product_search_bar);
        blackBar = findViewById(R.id.black_bar);
        sharedPreferences = getSharedPreferences(RECENT_QUERY, 0);
        recentQueries = loadRecentQueries();
        recentQueriesClone = new ArrayList<>(recentQueries);
        product = new Product("Maçã", "alimentos", 6.25, null, new Pair<>(3.0,8.0));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apbar = findViewById(R.id.appbar);

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

        searchBar.setOnSearchActionListener(this);

    }

    private void makeInitialFeedbackList() {
        List<Feedback> list = new ArrayList<>();

        final ListGenericAdapter<Feedback,Feedback.Holder> adapter = new ListGenericAdapter<>(
                this,
                list,
                new ListAdapter<Feedback, Feedback.Holder>() {
                    @Override
                    public Feedback.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = getLayoutInflater().inflate(R.layout.item_list_feedback, parent, false);
                        return new Feedback.Holder(view);
                    }

                    @Override
                    public void onBindViewHolder(List<Feedback> items, @NonNull Feedback.Holder holder, int position) {
                        holder.location.setText(items.get(position).getLocation());
                        holder.date.setText(items.get(position).getDate().toUpperCase());
                        String s = "R$ "+ String.format("%.2f", items.get(position).getPrice());
                        s = s.replace('.',',');
                        holder.price.setText(s);
                    }

                }
        );

        listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(adapter);
                lista.setLayoutManager(new LinearLayoutManager(context));
                lista.setItemAnimator(new DefaultItemAnimator());
                //lista.setPadding(0, 120, 0, 0);

                return lista;
            }
        });

        changeListFragment(listFragment);
    }

    private void changeListFragment(ListFragment newFragment) {
        FragmentManager manager = getSupportFragmentManager();

        Slide slide2 = new Slide();
        slide2.setDuration(500);
        slide2.setSlideEdge(Gravity.START);

        newFragment.setEnterTransition(slide2);

        Fade fade = new Fade();
        fade.setDuration(350);
        newFragment.setExitTransition(fade);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.timeline_container, newFragment);
        transaction.commit();
    }

    @Override
    public void showFeedbacks(List<Feedback> list) {
        for(int i = 0; i < 51; i++){
            list.add(new Feedback("Maçã", "Mercadinho do Shaake "+ i, "10 de Fevereiro de 2018", 6.5));
        }

        final ListGenericAdapter<Feedback,Feedback.Holder> adapter = new ListGenericAdapter<>(
                this,
                list,
                new ListAdapter<Feedback, Feedback.Holder>() {
                    @Override
                    public Feedback.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = getLayoutInflater().inflate(R.layout.item_list_feedback, parent, false);
                        return new Feedback.Holder(view);
                    }

                    @Override
                    public void onBindViewHolder(List<Feedback> items, @NonNull Feedback.Holder holder, int position) {
                        holder.location.setText(items.get(position).getLocation());
                        holder.date.setText(items.get(position).getDate().toUpperCase());
                        String s = "R$ "+ String.format("%.2f", items.get(position).getPrice().floatValue());
                        s = s.replace('.',',');
                        holder.price.setText(s);
                    }

                }
        );

        listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(adapter);
                lista.setLayoutManager(new LinearLayoutManager(context));
                lista.setItemAnimator(new DefaultItemAnimator());
                lista.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

                return lista;
            }
        });

        changeListFragment(listFragment);

        apbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CardView card = findViewById(R.id.ProductCard);
                RecyclerView re = listFragment.getList();
                TextView a = findViewById(R.id.toolbar_name);
                TextView b = findViewById(R.id.toolbar_price);
                a.setAlpha((float)(-verticalOffset/300.0));
                b.setAlpha((float)(-verticalOffset/300.0));
                card.setAlpha((float)(1+(verticalOffset/300.0)));
                if(card.getAlpha()==0)card.setVisibility(View.GONE);
                else card.setVisibility(View.VISIBLE);
                card.setTranslationY(verticalOffset);
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
                lp.setMargins(0, 240 + verticalOffset*220/400, 0,0);
                swipeRefreshLayout.setLayoutParams(lp);
                //swipeRefreshLayout.setY(145f + verticalOffset*220/400);
                //re.setPadding(0,25+verticalOffset*145/400,0,0);

            }
        });
    }

    @Override
    public void showProgressBar(Boolean enabled) {
        swipeRefreshLayout.setRefreshing(enabled);
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
        if(!enabled){
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

    public void showDialog(View v){
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.show();
    }
    public void cancelDialog(View v){
        dialog.dismiss();
    }
    public void createFeedback(View v){
        presenterOps.addFeedback(dialog,product.getName(),product.getPriceRange());
    }

    @Override
    public void showSnackbar(int op) {
        String str;

        if(op == 1) str = getString(R.string.feedback_removed);
        else str = getString(R.string.feedback_added);

        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.product_cordinator), str, Snackbar.LENGTH_LONG);

        if(op == 0)
            mySnackbar.setAction(R.string.undo_string, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenterOps.removeFeedback();
                }
            });

        mySnackbar.show();
    }

}
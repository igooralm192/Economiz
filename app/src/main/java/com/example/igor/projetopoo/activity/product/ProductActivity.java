package com.example.igor.projetopoo.activity.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.Snackbar;
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
import com.example.igor.projetopoo.activity.category.CategoryActivity;
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.adapter.SuggestionAdapter;
import com.example.igor.projetopoo.database.Database;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Feedback;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductActivity extends ParentActivity implements ProductMVP.ReqViewOps {
    private CustomDialog dialog;
    private AppBarLayout appbar;
    private TextView toolbarName;
    private TextView toolbarPrice;
    private TextView infoName;
    private TextView infoPrice;

    private ProductMVP.PresenterOps presenterOps;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        init();
        createSearchBar();

        presenterOps.getFeedbacks(currentProduct.getName());

        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterOps.getFeedbacks(currentProduct.getName());
            }
        });

    }

    @Override
    public void init() {
        setContext(this);
        setBlackLayout( (FrameLayout) findViewById(R.id.black_product));
        setSearchBar( (MaterialSearchBar) findViewById(R.id.product_search_bar));
        setSwipeRefreshLayout( (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_feedback));

        presenterOps = new ProductPresenter(this, new Database(FirebaseFirestore.getInstance()));
        dialog = new CustomDialog(this, R.layout.dialog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarName = findViewById(R.id.toolbar_name);
        toolbarPrice = findViewById(R.id.toolbar_price);
        infoName = findViewById(R.id.name_info_product);
        infoPrice = findViewById(R.id.price_info_product);


        Intent intent = getIntent();
        currentProduct = Product.toObject( intent.getStringExtra(Constant.SELECTED_PRODUCT) );
        if (currentProduct != null) {
            String name = currentProduct.getName();
            String price = String.format("R$ %.2f", currentProduct.getAveragePrice().doubleValue()).replace('.', ',');

            toolbarName.setText(name);
            toolbarPrice.setText(price);
            infoName.setText(name);
            infoPrice.setText(price);
        }

        appbar = findViewById(R.id.appbar);
        settingsAppBar();
        setAllSuggestions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) finish();

        if(id == R.id.app_bar_search)
            Animation.openSearch(getSearchBar(), getBlackLayout());

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFeedbacks(List<Feedback> list) {
        final Context context = this;

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

                        String s = "R$ " + String.format("%.2f", items.get(position).getPrice().floatValue()).replace('.',',');
                        holder.price.setText(s);
                    }

                }
        );

        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
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

    }

    @Override
    public void showProgressBar(Boolean enabled) {
        getSwipeRefreshLayout().setRefreshing(enabled);
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

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled){
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
                    this.onCategoryClick((Category) categoryItem.getObject());

                } else if (type.equals("product")) {

                    List list = getSearchBar().getLastSuggestions();
                    Item productItem = (Item) list.get(indItem);
                    Product product = (Product) productItem.getObject();

                    if (!product.getName().equals(currentProduct.getName()))
                        super.onProductClick((Product) productItem.getObject());

                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put(Constant.LAST_QUERY, query.getText().toString());

                    this.startActivity(index.get(type), map);
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
        transaction.replace(R.id.container_product, newFragment);
        transaction.commit();
    }

    private void settingsAppBar() {
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CardView card = findViewById(R.id.info_product);
                ConstraintLayout layout = findViewById(R.id.layout_info_product);

                int offset = appbar.getTotalScrollRange();

                toolbarName.setAlpha((float)(-verticalOffset / (float) offset));
                toolbarPrice.setAlpha((float)(-verticalOffset/(float) offset));
                card.setAlpha((float)(1+(verticalOffset/(float)offset)));

                card.setTranslationY(verticalOffset);

                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) getSwipeRefreshLayout().getLayoutParams();
                double top = Math.floor((card.getTop() + layout.getBottom() - appbar.getBottom()) * (1 + verticalOffset/(float)offset) );
                lp.setMargins(0, (int) top, 0,0);
                getSwipeRefreshLayout().setLayoutParams(lp);
            }
        });
    }

    public void createFeedback(View v){
        EditText location = dialog.findViewById(R.id.location_edit_text);
        EditText price = dialog.findViewById(R.id.price_edit_text);
        String prc = price.getText().toString();
        String loc = location.getText().toString();
        Pair<Number, Number> range = currentProduct.getPriceRange();

        location.setError(null);
        price.setError(null);

        if ("".equals(loc)) {
            loc = "Localização não informada";
        }

        if ("".equals(prc)) {
            price.setError("Este campo é obrigatório");
            return;
        }
        if(Double.parseDouble(prc) > range.second.doubleValue()){
            String s = "R$ "+ String.format("%.2f", range.second);
            s = s.replace('.',',');
            price.setError("O valor deve ser menor que " + s);
            return;
        }else if(Double.parseDouble(prc) < range.first.doubleValue()){
            String s = "R$ "+ String.format("%.2f", range.first);
            s = s.replace('.',',');
            price.setError("O valor deve ser maior que "+ s);
            return;
        }

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String str = format.format(date);

        Feedback feedback = new Feedback(currentProduct.getName(), loc, str, Double.parseDouble(prc));
        presenterOps.addFeedback(dialog, feedback);
    }

    public void showDialog(View v){
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.show();
    }

    public void cancelDialog(View v){
        dialog.dismiss();
    }


}

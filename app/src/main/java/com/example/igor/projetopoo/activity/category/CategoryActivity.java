package com.example.igor.projetopoo.activity.category;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.activity.product.ProductActivity;
import com.example.igor.projetopoo.activity.search.SearchActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Entity;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.example.igor.projetopoo.helper.Constant;
import com.example.igor.projetopoo.utils.Animation;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
    Classe que representa a tela das subcategorias.
    Baseado no modelo MVP, a CategoryActivity representa a View.
 */

public class CategoryActivity extends ParentActivity implements CategoryMVP.ReqViewOps {
    private Toolbar toolbar;

    private CategoryMVP.PresenterOps presenterOps;
    private Map<Category, Category> categoryLinks = new HashMap<>();
    private Category currentCategory;

    // Método principal onde é executado todas as operações para a execução do aplicativo.

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

    // Configurações iniciais

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

    }

    // Criação das opções do menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_category, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Método que trata o evento ao selecionar uma opção do menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) this.onBackPressed();
        else if (id == R.id.search_icon_category) {
            Animation.openSearch(getSearchBar(), getBlackLayout());
        }

        return super.onOptionsItemSelected(item);
    }

    // Evento ao clicar no botão de voltar

    @Override
    public void onBackPressed() {
        if (categoryLinks.get(currentCategory) == null) finish();
        else {
            presenterOps.getCategory(categoryLinks.get(currentCategory));

            toolbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportActionBar().setTitle( getSupportActionBar().getSubtitle() );
                    currentCategory = categoryLinks.get(currentCategory);
                    if (categoryLinks.get(currentCategory) == null) toolbar.setSubtitle("");
                    else toolbar.setSubtitle(categoryLinks.get(currentCategory).getName());
                }
            }, 500);
        }
    }

    // Método que exibe tanto uma lista de subcategorias quanto de produtos

    @Override
    public void showSubitems(List<Entity> subitems) {
        setAdapter(new ListGenericAdapter<>(
                getContext(),
                subitems,
                new ListAdapter<Entity, Entity.Holder>() {
                    @Override
                    public Entity.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(context).inflate(R.layout.item_list_entity, parent, false);

                        return new Entity.Holder(view, CategoryActivity.this);
                    }

                    @Override
                    public void onBindViewHolder(List<Entity> items, @NonNull Entity.Holder holder, int position) {
                        Entity entity = items.get(position);

                        holder.setEntity(entity);
                        holder.name.setText(entity.getName());
                        if (entity instanceof Category) {
                            holder.price.setText("");
                        } else {
                            Product product = (Product) entity;
                            holder.price.setText(String.format(Locale.getDefault(), "R$ %.2f", product.getAveragePrice().doubleValue()).replace('.', ','));
                        }
                    }
                })
        );

        ListFragment listFragment = ListFragment.getInstance();

        changeListFragment(listFragment);
    }

    // Método que exibe a barra de progresso

    @Override
    public void showProgressBar(Boolean enabled) {
        getSwipeRefreshLayout().setRefreshing(enabled);
    }

    // Método que seta as configurações da lista da activity.

    @Override
    public RecyclerView onListSettings(RecyclerView lista) {
        lista.setAdapter(getAdapter());
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        lista.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        lista.setItemAnimator(new DefaultItemAnimator());

        return lista;
    }

    // Evento ao mudar o estado da barra de pesquisa

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            Animation.closeSearch(getSearchBar(), getBlackLayout());
        }
    }

    // Evento ao clicar em uma sugestão da barra de pesquisa

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

    // Evento ao clicar em uma categoria

    @Override
    public void onCategoryClick(Category category) {
        categoryLinks.put(category, currentCategory);
        currentCategory = category;

        presenterOps.getCategory(category);
    }

    // Método que faz a troca da lista do fragmento

    private void changeListFragment(ListFragment newFragment) {
        FragmentManager manager = getSupportFragmentManager();
        final Category oldcategory = categoryLinks.get( currentCategory );

        Slide slide2 = new Slide();
        slide2.setDuration(500);

        slide2.setSlideEdge(Gravity.START);
        newFragment.setEnterTransition(slide2);

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.container_category, newFragment, currentCategory.getName());
        transaction.commitAllowingStateLoss();

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

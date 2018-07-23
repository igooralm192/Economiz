package com.example.igor.projetopoo.activity.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.activity.category.CategoryActivity;
import com.example.igor.projetopoo.activity.parent.ParentActivity;
import com.example.igor.projetopoo.activity.product.ProductActivity;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.entities.Item;
import com.example.igor.projetopoo.entities.Product;
import com.example.igor.projetopoo.entities.Result;
import com.example.igor.projetopoo.fragment.ListFragment;
import com.example.igor.projetopoo.helper.Constant;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * É classe principal do projeto, sendo usada para mostrar a tela de pesquisa do aplicativo,
 * representando a View no padrão MVP.
 */
public class SearchActivity extends ParentActivity implements SearchMVP.ReqViewOps {
    private String lastQuery;

    private SearchMVP.PresenterOps presenterOps;

    /**
     * Método principal, chamado quando a tela é criada. Determina o layout, inicializa atributos,
     * chama o método para construir a barra de pesquisa, configurar as sugestões e solicita a
     * Presenter as categorias principais.
     */
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

    // Inicializa alguns atributos
    @Override
    public void init() {
        setContext(this);
        setBlackLayout( (FrameLayout) findViewById(R.id.black_search) );
        setSearchBar( (MaterialSearchBar) findViewById(R.id.search_searchbar) );
        setSwipeRefreshLayout( (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_search) );

        presenterOps = new SearchPresenter(this, getDatabase());

        Intent intent = getIntent();
        lastQuery = intent.getStringExtra(Constant.LAST_QUERY);
        getSearchBar().setPlaceHolder(lastQuery);
    }


     // Mostra em um dropdown o resultado da pesquisa
    @Override
    public void showResults(List<Category> categoryList, List<Product> productList) {
        List<Result> resultList = new ArrayList<>();

        for(Product product: productList){
            Result result = new Result(R.drawable.ic_shopping_cart_red_32dp, product.getName(), product.getAveragePrice(), product);
            resultList.add(result);
        }

        for(Category category: categoryList){
            Result result = new Result(R.drawable.ic_search_black_24dp,category.getName(), category);
            resultList.add(result);
        }

        setAdapter(new ListGenericAdapter<>(
                this,
                resultList,
                new ListAdapter<Result, Result.Holder>() {
                    @Override
                    public Result.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(context).inflate(R.layout.item_list_result, parent, false);

                        return new Result.Holder(view);
                    }

                    @Override
                    public void onBindViewHolder(List<Result> items, @NonNull final Result.Holder holder, int position) {
                        final Result result = items.get(position);
                        final Double price = result.getPrice().doubleValue();

                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (price == -1)
                                    SearchActivity.this.onCategoryClick((Category) result.getObject());
                                else
                                    SearchActivity.this.onProductClick((Product) result.getObject());
                            }
                        });

                        holder.iconResult.setImageResource(result.getIcon());
                        holder.nameResult.setText(result.getName());
                        if (result.getPrice().doubleValue() != -1)
                            holder.priceResult.setText(String.format("R$ %.2f", result.getPrice()));
                        else
                            holder.priceResult.setText("");


                    }
                })
        );

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Exemplo de instanciação do ListFragment
        ListFragment listFragment = ListFragment.getInstance();

        fragmentTransaction.add(R.id.container_search, listFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }


    // Mostra o símbolo de carregamento ou não a depender do valor de enabled.
    @Override
    public void showProgressBar(Boolean enabled) {
        getSwipeRefreshLayout().setRefreshing(enabled);
    }

    /**
     * Recebe uma RecyclerView (do fragmento) e define algumas configurações, como seu Adapter e
     * LayourManager, retornando a lista configurada em seguida
     */
    @Override
    public RecyclerView onListSettings(RecyclerView lista) {
        lista.setAdapter(getAdapter());
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        lista.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return lista;
    }

    // Verifica se a barra de pesquisa foi selecionada ou não e executa as animações necessárias.
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

    //Exibe e atualiza as pesquisas recentes e chama o metodo para pesquisa no banco de dados
    @Override
    public void onSearchConfirmed(CharSequence text) {
        String newText = text.toString().trim();

        if (newText.length() != 0) {
            Item item = new Item(R.drawable.ic_history_black_24dp, newText, "recent", null);
            if (getRecentQueriesClone().size() > 0) {
                if (!getRecentQueriesClone().get(0).equals(item)) {
                    if (getRecentQueries().contains(item)) {
                        int index = getRecentQueries().indexOf(item);
                        while (index != -1) {
                            getRecentQueries().remove(index);
                            index = getRecentQueries().indexOf(item);
                        }
                    }

                    if (getRecentQueriesClone().size() == 2) getRecentQueriesClone().remove(1);

                    getRecentQueriesClone().add(0, item);
                    getRecentQueries().add(item);
                }
            } else {
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

    // Ao clicar numa sugestão, verifica-se o seu tipo para poder redirecionar o usuário a tela correta.
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
                    super.onCategoryClick((Category) categoryItem.getEntity());

                } else if (type.equals("product")) {
                    List list = getSearchBar().getLastSuggestions();
                    Item productItem = (Item) list.get(indItem);
                    super.onProductClick((Product) productItem.getEntity());

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

                break;
            }
        }
    }

}

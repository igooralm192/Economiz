package com.example.igor.projetopoo.activity.product;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.fragment.ListFragment;

import java.util.ArrayList;
import java.util.List;


public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final List<Item> items = new ArrayList<>();


        for (int i=0; i<50; i++) {
            items.add(new Item("Item "+i));
        }

        final Context context = this;
        final ListGenericAdapter<Item, Item.Holder> adapter = new ListGenericAdapter<>(this, items, new ListAdapter<Item, Item.Holder>() {
            @Override
            public Item.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_list_test, parent, false);

                return new Item.Holder(view);
            }

            @Override
            public void onBindViewHolder(List<Item> items, @NonNull Item.Holder holder, int position) {
                holder.nome.setText(items.get(position).getNome());
            }
        });

        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                lista.setAdapter(adapter);
                lista.setLayoutManager(new LinearLayoutManager(context));
                lista.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                return lista;
            }
        });

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.timeline_container, listFragment);
        transaction.commit();

        AppBarLayout apbar = findViewById(R.id.appbar);
        apbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CardView card = findViewById(R.id.ProductCard);
                card.setAlpha((float)(1+(verticalOffset/400.0)));
                if(card.getAlpha()==0)card.setVisibility(View.GONE);
                card.setTranslationY(verticalOffset);
                System.out.println("OI CU");
                System.out.print((verticalOffset/400));
                return;
            }
        });
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
        if(id == R.id.searchBar){

        }
        return super.onOptionsItemSelected(item);
    }

}

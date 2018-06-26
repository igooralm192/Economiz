package com.example.igor.projetopoo.activity.main;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.igor.projetopoo.R;
import com.example.igor.projetopoo.adapter.ListAdapter;
import com.example.igor.projetopoo.adapter.ListGenericAdapter;
import com.example.igor.projetopoo.entities.Category;
import com.example.igor.projetopoo.fragment.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.igor.projetopoo.helper.Blur;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  OnSearchActionListener {

    FrameLayout blackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        blackBar = (FrameLayout) findViewById(R.id.blackBar);
        MaterialSearchBar searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);

        searchBar.setOnSearchActionListener(this);

        setCategoryList(this);
    }

    private void setCategoryList(final Context context) {
        ArrayList<Category> categories = new ArrayList<Category>();
        categories.add(new Category("Alimentos", R.drawable.food));
        categories.add(new Category("Limpeza", R.drawable.cleaning2));
        categories.add(new Category("Farmácia", R.drawable.farmacia));
        categories.add(new Category("Zoológico", R.drawable.food));

        final RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        final ListGenericAdapter<Category, Category.Holder> listGenericAdapter = new ListGenericAdapter<Category, Category.Holder>(this, categories, new ListAdapter<Category, Category.Holder>() {
            @Override
            public Category.Holder onCreateViewHolder(Context context, @NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.category_view, parent, false);

                Category.Holder holder = new Category.Holder(view);

                return holder;
            }

            @Override
            public void onBindViewHolder(List<Category> items, @NonNull Category.Holder holder, int position) {
                Category category = items.get(position);
                holder.nameCategory.setText(category.getName());
                holder.backCategory.setImageResource(category.getBackground());

                Blur.blurImage(holder.backCategory, 5, context);
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

        fragmentTransaction.add(R.id.constraint_layout_main, listFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        TransitionDrawable background = (TransitionDrawable) blackBar.getBackground();

        if (enabled) {
            background.startTransition(300);
        } else {
            background.reverseTransition(300);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {}

    @Override
    public void onButtonClicked(int buttonCode) {}
}

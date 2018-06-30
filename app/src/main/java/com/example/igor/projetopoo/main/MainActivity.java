package com.example.igor.projetopoo.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.igor.projetopoo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Exemplo de instanciação do ListFragment
        ListFragment listFragment = ListFragment.getInstance(new ListFragment.OnListFragmentSettings() {
            @Override
            public RecyclerView setList(RecyclerView lista) {
                return null;
            }
        });   */
    }
}

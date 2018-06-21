package com.example.igor.projetopoo.activity.product;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.igor.projetopoo.R;

public class Item {
    private String nome;

    public Item(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        protected TextView nome;

        public Holder(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nome);
        }
    }
}

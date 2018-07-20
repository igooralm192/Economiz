package com.example.igor.projetopoo.exception;

import android.content.Context;
import android.widget.Toast;

public class DatabaseException extends Exception {
    private Context context;

    public DatabaseException(Context context) {
        this.context = context;
    }

    public void failReadData() {
        Toast.makeText(context, "Não foi possível ler os dados, tente novamente.", Toast.LENGTH_LONG).show();
    }

    public void failWriteData() {
        Toast.makeText(context, "Não foi possível adicionar os dados, tente novamente.", Toast.LENGTH_LONG).show();
    }

    public void failRemoveData() {
        Toast.makeText(context, "Não foi possível remover os dados, tente novamente.", Toast.LENGTH_LONG).show();
    }
}

package com.example.igor.projetopoo.helper;

import android.os.AsyncTask;

public class AsyncDownload extends AsyncTask<Object, Void, Object>{
    private OnAsyncDownloadListener onAsyncDownloadListener;

    public AsyncDownload(OnAsyncDownloadListener onAsyncDownloadListener) {
        this.onAsyncDownloadListener = onAsyncDownloadListener;
    }

    @Override
    protected void onPreExecute() {
        onAsyncDownloadListener.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        return onAsyncDownloadListener.doInBackground(objects);
    }

    @Override
    protected void onPostExecute(Object object) {
        onAsyncDownloadListener.onPostExecute(object);
    }

    public interface OnAsyncDownloadListener {
        void onPreExecute();
        Object doInBackground(Object... objects);
        void onPostExecute(Object object);
    }
}
package com.stipess.youplay.radio;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.util.ArrayList;

public abstract class Browser {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public enum ListType
    {
        COUNTRIES,

        STATIONS
    }

    public interface Listener {
        void postExecute(ArrayList<Country> countries);

        void preExecute();

        void getPostExecute(ArrayList<Station> stations);
    }

    public abstract void setListener(Listener listener);

    protected void onPreExecute() {}

    protected String doInBackground(String... strings) { return null; }

    protected void onPostExecute(String result) {}

    public void execute(final String... args) {
        mainHandler.post(this::onPreExecute);
        EXECUTOR.execute(() -> {
            String res = doInBackground(args);
            mainHandler.post(() -> onPostExecute(res));
        });
    }
}

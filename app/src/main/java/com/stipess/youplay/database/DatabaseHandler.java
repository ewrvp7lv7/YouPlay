package com.stipess.youplay.database;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.stipess.youplay.Ilisteners.OnDataChanged;
import com.stipess.youplay.music.Music;

import java.util.ArrayList;

import static com.stipess.youplay.utils.Constants.DOWNLOADED;

public class DatabaseHandler {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String tableName;
    private String databaseName;
    private UpdateType type;
    private YouPlayDatabase db;
    private Music pjesma;
    private ArrayList<Music> pjesme;
    private OnDataChanged onDataChanged;

    private volatile boolean cancelled;

    // Executes database work on a background thread
    private void doInBackground() {

        SQLiteDatabase database = null;
        if(databaseName.equals(YouPlayDatabase.PLAYLIST_DB))
        {
            if(type == UpdateType.GET)
            {
                pjesme.addAll(db.getDataTable(tableName));
            }
            else
            {
                database = db.getDatabase(YouPlayDatabase.PLAYLIST_DB);
                if(database != null)
                for(Music pjesma : pjesme)
                    db.insertInTable(pjesma, tableName);
            }
        }
        else
        {
            database = db.getDatabase(YouPlayDatabase.YOUPLAY_DB);
            if(type == UpdateType.ADD)
            {
                if(!db.ifItemExists(pjesma.getId()) && database != null)
                    YouPlayDatabase.insertSong(database, pjesma);
                else if(pjesma.getDownloaded() == 1)
                    db.updateSong(DOWNLOADED, pjesma);
            }
            else if(type == UpdateType.REMOVE)
            {
                db.deleteData(pjesma.getId());
            }
            else if(type == UpdateType.REMOVE_LIST)
            {
                for(int i = 0; i < pjesme.size(); i++)
                {
                    Log.d("DatabaseHandler", "Deleting " + pjesme.get(i).getTitle());
                    db.deleteData(pjesme.get(i).getId());
                    publishProgress(i, pjesme.get(i).getTitle());
                }
            }
            else if(type == UpdateType.GET)
            {
                pjesme.addAll(db.getData());
            }
        }
        if(database != null)
            database.close();
    }

    private void onProgressUpdate(Object... values)
    {
        if(onDataChanged != null)
            onDataChanged.deleteProgress((int) values[0], (String) values[1]);
    }

    private void onPostExecute()
    {
        if(cancelled) return;

        if(onDataChanged != null && type != UpdateType.GET)
            onDataChanged.dataChanged(type, databaseName, pjesma);
        else if(onDataChanged != null)
            onDataChanged.dataChanged(type, pjesme);
    }

    private void onPreExecute() {

    }

    public enum UpdateType
    {
        ADD,
        // Dohvati sve pjesme iz SQL
        GET,

        REMOVE,

        REMOVE_LIST
    }

    public DatabaseHandler setDataChangedListener(OnDataChanged onDataChanged)
    {
        this.onDataChanged = onDataChanged;
        return this;
    }

    public DatabaseHandler(ArrayList<Music> pjesme, String tableName, String databaseName, UpdateType type)
    {
        this.tableName = tableName;
        this.databaseName = databaseName;
        this.type = type;
        this.pjesme = pjesme;

        db = YouPlayDatabase.getInstance();

    }

    // Koristiti kada dohvacamo pjesme
    public DatabaseHandler(UpdateType type, OnDataChanged dataChanged, String databaseName, String tableName)
    {
        this.type = type;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.pjesme = new ArrayList<>();
        this.onDataChanged = dataChanged;
        db = YouPlayDatabase.getInstance();
    }

    public DatabaseHandler(Music pjesma, String tableName, String databaseName, UpdateType type)
    {
        this.tableName = tableName;
        this.databaseName = databaseName;
        this.pjesma = pjesma;
        this.type = type;

        db = YouPlayDatabase.getInstance();
    }

    public void execute() {
        mainHandler.post(this::onPreExecute);
        EXECUTOR.execute(() -> {
            doInBackground();
            mainHandler.post(this::onPostExecute);
        });
    }

    private void publishProgress(final Object... values) {
        mainHandler.post(() -> onProgressUpdate(values));
    }

    public void cancel() {
        cancelled = true;
    }
}

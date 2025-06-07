package com.stipess.youplay.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import com.stipess.youplay.AudioService;
import com.stipess.youplay.utils.Constants;

/**
 * Network callback for connectivity changes.
 */
public class NetworkStateCallback extends ConnectivityManager.NetworkCallback {

    private final Context context;

    public NetworkStateCallback(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void onAvailable(Network network) {
        Intent newIntent = new Intent(context, AudioService.class);
        newIntent.putExtra(AudioService.ACTION, Constants.ADS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(newIntent);
        } else {
            context.startService(newIntent);
        }
    }
}

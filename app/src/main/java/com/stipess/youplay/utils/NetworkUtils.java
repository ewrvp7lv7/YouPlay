package com.stipess.youplay.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NetworkUtils {
    private NetworkUtils() {}

    public static boolean hasInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final AtomicBoolean connected = new AtomicBoolean(false);
            final CountDownLatch latch = new CountDownLatch(1);
            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    connected.set(true);
                    latch.countDown();
                }

                @Override
                public void onUnavailable() {
                    connected.set(false);
                    latch.countDown();
                }
            };
            cm.registerDefaultNetworkCallback(callback);
            try {
                latch.await(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
            cm.unregisterNetworkCallback(callback);
            return connected.get();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
        } else {
            Network[] networks = cm.getAllNetworks();
            for (Network network : networks) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                if (caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                        || caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                        || caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN))) {
                    return true;
                }
            }
            return false;
        }
    }
}


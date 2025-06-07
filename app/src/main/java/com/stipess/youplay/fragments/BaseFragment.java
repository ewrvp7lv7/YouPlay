package com.stipess.youplay.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.core.os.HandlerCompat;
import android.view.View;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stipess.youplay.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {


    private static final String TAG = BaseFragment.class.getSimpleName();

    private Handler handler;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    void setPlayScreen()
    {
        Runnable runnable = () -> {
            Activity activity = BaseFragment.this.getActivity();
            if (activity instanceof MainActivity)
                ((MainActivity) BaseFragment.this.getActivity()).pager.setCurrentItem(0, true);
        };
        handler.postDelayed(runnable, 200);
    }

    public boolean internetConnection() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
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
            connectivityManager.registerDefaultNetworkCallback(callback);
            try {
                latch.await(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
            connectivityManager.unregisterNetworkCallback(callback);
            return connected.get();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(network);
            return caps != null &&
                    (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                            || caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    public abstract void buildAlertDialog(int position, View view);

    public abstract void setupActionBar();

    public abstract void initAudioService();

}

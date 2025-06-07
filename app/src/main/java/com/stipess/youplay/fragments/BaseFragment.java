package com.stipess.youplay.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.core.os.HandlerCompat;
import android.view.View;

import com.stipess.youplay.utils.NetworkUtils;

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
        return NetworkUtils.hasInternet(requireContext());
    }

    public abstract void buildAlertDialog(int position, View view);

    public abstract void setupActionBar();

    public abstract void initAudioService();

}

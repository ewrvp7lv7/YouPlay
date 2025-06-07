package com.stipess.youplay.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsetsController;

import com.stipess.youplay.BuildConfig;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Stjepan on 2.12.2017..
 */

public class Utils {

    private Utils(){}

    private static DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    private static DecimalFormat formatter = new DecimalFormat("#,###", symbols);
    private static final long MEGA_BYTE = 1048576;


    public static String convertViewsToString(long views)
    {
        return formatter.format(views);
    }




    public static long convertToMilis(String duration)
    {
        Log.d("Utils", "duration: "  + duration);
        String[] regex;
        if(duration.contains(":"))
            regex = duration.split(":");
        else
            regex = duration.split("\\.");

        if(regex.length >= 3)
        {
            long hours = Integer.parseInt(regex[0]);
            long mins  = Integer.parseInt(regex[1]);
            long secs  = Integer.parseInt(regex[2]);

            hours = (hours * 3600) * 1000;
            mins  = (mins  * 60) * 1000;
            secs  = secs * 1000;

            return hours + mins + secs;
        }
        else if(regex.length >= 2)
        {
            long mins = Integer.parseInt(regex[0]);
            long secs = Integer.parseInt(regex[1]);

            mins  = (mins  * 60) * 1000;
            secs  = secs * 1000;

            return mins + secs;
        }
        else
        {
            long secs = Integer.parseInt(regex[0]);

            return secs * 1000;
        }
    }

    public static String convertDuration(long milis)
    {
        int seconds = (int) (milis / 1000) % 60;
        int minutes = (int) ((milis / (1000*60)) % 60);
        int hours   = (int) ((milis / (1000*60*60)));
        if(hours == 0)
        {
            return String.format("%d:%02d", minutes, seconds);
        }
        return String.format("%d:%02d:%02d",hours ,minutes, seconds);
    }

    public static int freeSpace(boolean external)
    {
        StatFs statFs = getStats(external);
        long availableBlocks = statFs.getAvailableBlocksLong();
        long blockSize = statFs.getBlockSizeLong();
        long freeBytes = availableBlocks * blockSize;

        return (int) (freeBytes / MEGA_BYTE);
    }

    private static StatFs getStats(boolean external){
        String path;

        if (external){
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        else{
            path = Environment.getRootDirectory().getAbsolutePath();
        }

        return new StatFs(path);
    }

    public static boolean needsUpdate(String webVersion)
    {
        webVersion = webVersion.replaceAll("\\.","");
        String localVersion = BuildConfig.VERSION_NAME.replaceAll("\\.", "");

        return Integer.parseInt(webVersion) > Integer.parseInt(localVersion);
    }

    /**
     * Try to retrieve the first thumbnail URL from a NewPipeExtractor info item.
     * This method uses reflection to stay compatible with different library versions.
     */
    public static String getThumbnailUrl(Object infoItem) {
        if (infoItem == null) return null;
        try {
            java.lang.reflect.Method m = infoItem.getClass().getMethod("getThumbnailUrl");
            Object url = m.invoke(infoItem);
            if (url != null) return url.toString();
        } catch (Exception ignored) {}
        try {
            java.lang.reflect.Method m = infoItem.getClass().getMethod("getThumbnails");
            Object list = m.invoke(infoItem);
            if (list instanceof java.util.List && !((java.util.List<?>) list).isEmpty()) {
                Object thumb = ((java.util.List<?>) list).get(0);
                java.lang.reflect.Method urlMethod = thumb.getClass().getMethod("getUrl");
                Object url = urlMethod.invoke(thumb);
                if (url != null) return url.toString();
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Set the color of the system bars on devices where the API is available.
     * <p>
     * This method uses reflection for newer APIs to keep the project
     * compatible with older Android SDKs.
     */
    @SuppressWarnings("deprecation")
    public static void setSystemBarsColor(Window window, int color) {
        if (window == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                try {
                    java.lang.reflect.Method m = controller.getClass().getMethod(
                            "setSystemBarsColor", int.class, boolean.class);
                    m.invoke(controller, color, false);
                    return;
                } catch (Exception ignored) {
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.setNavigationBarColor(color);
            }
        }
    }

}

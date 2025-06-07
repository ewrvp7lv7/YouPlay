package androidx.media.session;

import android.content.Intent;
import android.view.KeyEvent;

/**
 * Minimal compatibility stub matching the AndroidX media library API.
 * <p>
 * This implementation simply returns {@code null}. It exists so the app can be
 * built without bundling the full library while still providing the method
 * signature expected at runtime.
 */
public class MediaButtonReceiver {
    public static KeyEvent handleIntent(MediaSessionCompat session, Intent intent) {
        return null;
    }
}

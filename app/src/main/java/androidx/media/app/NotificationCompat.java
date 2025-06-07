package androidx.media.app;

import androidx.media.session.MediaSessionCompat;

public class NotificationCompat {
    public static class MediaStyle {
        public MediaStyle setMediaSession(MediaSessionCompat.Token token) { return this; }
        public MediaStyle setShowActionsInCompactView(int... actions) { return this; }
    }
}

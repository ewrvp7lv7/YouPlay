package androidx.media.session;

import android.content.Context;
import androidx.media.MediaMetadataCompat;

public class MediaSessionCompat {
    public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;

    public static class Token {}

    public MediaSessionCompat(Context context, String tag) {}

    public void setFlags(int flags) {}
    public void setCallback(Callback callback) {}
    public Token getSessionToken() { return new Token(); }
    public void setMediaButtonReceiver(android.app.PendingIntent mbr) {}
    public void setActive(boolean active) {}
    public void setMetadata(MediaMetadataCompat metadata) {}
    public void setPlaybackState(PlaybackStateCompat state) {}

    public static abstract class Callback {
        public void onPlay() {}
        public void onPause() {}
        public void onSkipToNext() {}
        public void onSkipToPrevious() {}
        public void onSeekTo(long pos) {}
    }
}

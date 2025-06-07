package androidx.media;

import android.graphics.Bitmap;

public class MediaMetadataCompat {
    public static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
    public static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
    public static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
    public static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
    public static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
    public static final String METADATA_KEY_DOWNLOAD_STATUS = "android.media.metadata.DOWNLOAD_STATUS";
    public static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";

    public static class Builder {
        public Builder putString(String key, String value) { return this; }
        public Builder putLong(String key, long value) { return this; }
        public Builder putBitmap(String key, Bitmap value) { return this; }
        public MediaMetadataCompat build() { return new MediaMetadataCompat(); }
    }
}

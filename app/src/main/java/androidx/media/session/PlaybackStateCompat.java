package androidx.media.session;

public class PlaybackStateCompat {
    public static final int STATE_NONE = 0;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_SKIPPING_TO_NEXT = 7;
    public static final int STATE_SKIPPING_TO_PREVIOUS = 8;

    public static final long ACTION_PLAY = 1L;
    public static final long ACTION_PAUSE = 2L;
    public static final long ACTION_SKIP_TO_NEXT = 32L;
    public static final long ACTION_SKIP_TO_PREVIOUS = 16L;
    public static final long ACTION_SEEK_TO = 256L;
    public static final long ACTION_PLAY_PAUSE = 512L;

    public static final long PLAYBACK_POSITION_UNKNOWN = -1L;

    public static class Builder {
        public Builder setState(int state, long position, float playbackSpeed) { return this; }
        public Builder setActions(long actions) { return this; }
        public PlaybackStateCompat build() { return new PlaybackStateCompat(); }
    }
}

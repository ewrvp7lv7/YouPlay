package com.stipess.youplay.youtube.loaders;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.stipess.youplay.AudioService;
import com.stipess.youplay.extractor.DownloaderTestImpl;
import com.stipess.youplay.music.Music;
import com.stipess.youplay.utils.FileManager;
import com.stipess.youplay.utils.Utils;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.InfoItem;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Stjepan Stjepanovic
 * <p>
 * Copyright (C) Stjepan Stjepanovic 2017 <stipess@youplayandroid.com>
 * UrlLoader.java is part of YouPlay.
 * <p>
 * YouPlay is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * YouPlay is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with YouPlay.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UrlLoader
{
    private static final String TAG = UrlLoader.class.getSimpleName();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";
    private static final String DEFAULT_HTTP_ACCEPT_LANGUAGE = "de";

    private String getYoutubeLink;
    private Listener listener;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<Music> musicList = new ArrayList<>();
    private List<Music> checkList = new ArrayList<>();
    private boolean relatedVideos;

    public UrlLoader(String getYoutubeLink, boolean relatedVideos, List<Music> list)
    {
        this.getYoutubeLink = getYoutubeLink;
        this.relatedVideos = relatedVideos;
        checkList.addAll(list);
    }

    public interface Listener{
        void postExecute(List<String> list);
    }

    public List<Music> getMusicList()
    {
        return musicList;
    }

    private List<String> loadInternal() {
        List<String> data = new ArrayList<>();
        try
        {
            if(NewPipe.getDownloader() == null)
                NewPipe.init(DownloaderTestImpl.getInstance());

            StreamingService service = NewPipe.getService("YouTube");
            StreamInfo streamInfo = StreamInfo.getInfo(service, getYoutubeLink);

            List<AudioStream> audioStreams = streamInfo.getAudioStreams();
            List<InfoItem> relatedVideos;
            try {
                java.lang.reflect.Method m = streamInfo.getClass().getMethod("getRelatedItems");
                Object obj = m.invoke(streamInfo);
                relatedVideos = obj instanceof List ? (List<InfoItem>) obj : new ArrayList<>();
            } catch (Exception ignored) {
                try {
                    java.lang.reflect.Method m = streamInfo.getClass().getMethod("getRelatedStreams");
                    Object obj = m.invoke(streamInfo);
                    relatedVideos = obj instanceof List ? (List<InfoItem>) obj : new ArrayList<>();
                } catch (Exception e) {
                    relatedVideos = new ArrayList<>();
                }
            }

            data.add(Utils.getThumbnailUrl(streamInfo));
            if(!audioStreams.isEmpty()) {
                try {
                    java.lang.reflect.Method m = audioStreams.get(0).getClass().getMethod("getUrl");
                    Object url = m.invoke(audioStreams.get(0));
                    if (url != null) data.add(url.toString());
                } catch (Exception e) {
                    try {
                        java.lang.reflect.Method m = audioStreams.get(0).getClass().getMethod("getContent");
                        Object url = m.invoke(audioStreams.get(0));
                        if (url != null) data.add(url.toString());
                    } catch (Exception ex) {
                        // ignore when not available
                    }
                }
            }

            Log.d(TAG, "Extracted");

            if(this.relatedVideos) {

                for(InfoItem item : relatedVideos) {
                    if(!(item instanceof StreamInfoItem)) {
                        continue;
                    }
                    StreamInfoItem stream = (StreamInfoItem) item;
                    Music music = new Music();
                    music.setAuthor(stream.getUploaderName());
                    music.setViews(Utils.convertViewsToString(stream.getViewCount()));
                    music.setUrlImage(Utils.getThumbnailUrl(stream));
                    music.setTitle(stream.getName());
                    String tempUrl = stream.getUrl();
                    if(tempUrl.contains("https://youtu.be/")) {
                        tempUrl = tempUrl.substring(17, tempUrl.length());
                    } else if(tempUrl.contains("https://www.youtube.com/watch?v=")) {
                        tempUrl = tempUrl.substring(32, tempUrl.length());
                    } else if(tempUrl.contains("https://m.youtube.com/watch?v=")) {
                        tempUrl = tempUrl.substring(30, tempUrl.length());
                    } else if(tempUrl.contains("http://www.youtube.com/v/")) {
                        tempUrl = tempUrl.substring(25, tempUrl.length());
                    }
                    music.setId(tempUrl);
                    music.setDuration(Utils.convertDuration(stream.getDuration()*1000));
                    Log.d(TAG, "Pjesma; "+music.getId());
                    Log.d(TAG, "CheckList: " + checkList.size());
                    for(Music pjesma : checkList)
                    {
                        if(pjesma.getId().equals(music.getId()))
                        {
                            Log.d(TAG, "Pjesme: " + pjesma.getId()+"-"+music.getId());
                            if(pjesma.getDownloaded() == 1)
                            {
                                music.setPath(FileManager.getMediaPath(music.getId()));
                                music.setDownloaded(1);
                            }
                        }
                    }
                    musicList.add(music);
                }

            }


            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void postResult(List<String> strings) {
        AudioService audioService = AudioService.getInstance();
        if(listener != null && audioService != null && !audioService.isDestroyed())
            listener.postExecute(strings);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public void load() {
        executor.execute(() -> {
            List<String> result = loadInternal();
            mainHandler.post(() -> postResult(result));
        });
    }
}

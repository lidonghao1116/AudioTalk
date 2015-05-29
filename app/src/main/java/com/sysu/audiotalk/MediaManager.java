package com.sysu.audiotalk;

import android.media.*;

import java.io.IOException;

/**
 * Created by user on 15/5/29.
 */
public class MediaManager {
    //TODO:了解MediaManager的状态转移流程
    private static MediaPlayer mMediaPlayer;

    private static boolean isPause;

    public static void playSound(String filePath, MediaPlayer.OnCompletionListener listener)
    {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }else {
            mMediaPlayer.reset();
        }

        mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(listener);

        try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();isPause = true;
        }
    }

    public static void resume() {
        if(mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}

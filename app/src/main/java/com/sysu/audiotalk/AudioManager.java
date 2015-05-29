package com.sysu.audiotalk;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by user on 15/5/28.
 */
public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;



    private String mCurrentFilePath;

    public AudioStateListener mListener;

    //单例模式的级别是application的
    //dialogManager的级别应该是Activity的，所以不声明为单例
    private static AudioManager mInstance;

    private boolean isPrepared;

    private AudioManager(String dir){
        mDir = dir;
    }

    public static AudioManager getInstance(String dir) {
        if(mInstance == null) {
            synchronized (AudioManager.class) {
                if(mInstance == null)
                    mInstance = new AudioManager(dir);
            }
        }
        return mInstance;
    }

    public void prepareAudio() {
        isPrepared =false;

        File dir = new File(mDir);
        if(!dir.exists())
            dir.mkdirs();

        String fileName = generateFileName();
        File file = new File(dir,fileName);
        mCurrentFilePath = file.getAbsolutePath();

        //要了解一下mediaRecorder的状态转移图
        mMediaRecorder = new MediaRecorder();
        //设置文件路径
        mMediaRecorder.setOutputFile(file.getAbsolutePath());

        //设置音频源为麦克风
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //设置编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            isPrepared = true;

            if(mListener!= null) {
                //更新界面
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * generate random file name
     * @return
     */
    private String generateFileName() {
//        SimpleDateFormat simpleDateFormat;
//
//        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//
//        Date date = new Date();
//
//        String str = simpleDateFormat.format(date);
//
//        Random random = new Random();
//
//        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
//
//        return rannum + str;// 当前时间
        return UUID.randomUUID().toString()+".amr";
    }

    public int getVolume(int maxLevel) {
        if(isPrepared) {
            //maxAmplitude的范围是1-32767
            try {
                return (maxLevel * mMediaRecorder.getMaxAmplitude()/32768)+1;
            }catch (Exception e) {
             //有异常的话直接返回1
            }
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel() {

        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }

    }

    public interface AudioStateListener {
        void wellPrepared();
    }

    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public String getmCurrentFilePath() {
        return mCurrentFilePath;
    }

}

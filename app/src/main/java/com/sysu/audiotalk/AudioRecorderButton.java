package com.sysu.audiotalk;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

//TODO:dialog的出现有点慢

/**
 * Created by user on 15/5/27.
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    //定义单位为dp，然后在构造方法中转换为px
    private static final int DISTANCE_Y_CANCEL = 50;

    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;

    private DialogManager mDialogManager;

    private AudioManager mAudioManager;

    private float mTime = 0;
    //是否触发了longclick
    private boolean mReady;

    //获取音量大小的线程
    private Runnable mGetVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            while(isRecording) {
                try {
                    //每0.1秒更新音量
                    Thread.sleep(100);
                    //累计录音时间
                    mTime += 0.1;
                    mHandler.sendEmptyMessage(MSG_VOLUME_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);

        setOnLongClickListener(new OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });

        //TODO：应该判断外部储存卡是否存在
        String dir = Environment.getExternalStorageDirectory()+"/xu_recorder_audios";
        System.out.println(dir);
        mAudioManager = AudioManager.getInstance(dir);
        //设置监听recorder准备好的事件
        mAudioManager.setOnAudioStateListener(this);
    }

    /**
     * 录音完成之后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mAudioFinishListener;

    public void setmAudioFinishListener(AudioFinishRecorderListener mAudioFinishListener) {
        this.mAudioFinishListener = mAudioFinishListener;
    }

    private static final int MSG_AUDIO_PREPARED = 0x123;
    private static final int MSG_VOLUME_CHANGED = 0x124;
    private static final int MSG_DIALOG_DISMISS = 0x152;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //同时要开始计时
                    mDialogManager.showRecordingDialog();
                    isRecording = true;

                    //开启一个线程来获取音量，并计时
                    new Thread(mGetVolumeRunnable).start();
                    break;
                case MSG_VOLUME_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVolume(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                default:
                    break;

            }
            super.handleMessage(msg);
        }
    };

    //录音准备完成后的回调
    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //TODO:set to true for test
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording) {
                    if(wantToCancel(x,y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    }else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!mReady) {
                    reset();
                    return  super.onTouchEvent(event);
                }
                //prepare没完成已经up
                if(!isRecording || mTime < 0.6f) {
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    //对话框显示一段时间
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS,1200);
                }else if(mCurState == STATE_RECORDING) { //正常录制结束
                    //释放资源等等
                    //callbacktoActivity
                    mDialogManager.dismissDialog();
                    mAudioManager.release();

                    if(mAudioFinishListener != null) {
                        //录音完成之后由activity回调
                        mAudioFinishListener.onFinish(mTime,mAudioManager.getmCurrentFilePath());
                    }

                }else if(mCurState == STATE_WANT_TO_CANCEL) {
                    mHandler.sendEmptyMessage(MSG_DIALOG_DISMISS);
                }
                reset();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        isRecording = false;
        mReady = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    private void changeState(int state) {
        if(mCurState != state) {
            mCurState = state;
            switch(mCurState) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_normal);
                    setText(R.string.btn_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.btn_recording);
                    if(isRecording) {
                        //TODO:更新dialog
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.btn_want_cancel);
                    //TODO:更新dialog
                    mDialogManager.wantToCancel();
                    break;
                default:
                    break;
            }
        }
    }

    //判断是否超出了区域
    private boolean wantToCancel(int x, int y) {
        if(x < 0 || x>getWidth())
            return true;
        if(y < -DISTANCE_Y_CANCEL || y > getWidth()+DISTANCE_Y_CANCEL)
            return true;
        return false;
    }


}

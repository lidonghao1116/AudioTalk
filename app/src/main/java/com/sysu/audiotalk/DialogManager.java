package com.sysu.audiotalk;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by user on 15/5/27.
 */
public class DialogManager {
    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVol;
    private TextView mTv;

    private Context mContext;

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        //dialog的初始化最好放在这，应为构造函数只在button的调用一次，dismiss之后dialog变为null
        //如果不调用第二次构造函数会有错误，所以初始化不放在构造函数里面
        mDialog = new Dialog(mContext,R.style.ThemeDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mDialog.setContentView(inflater.inflate(R.layout.dialog,null));

        mIcon = (ImageView) mDialog.findViewById(R.id.id_record_dialog_icon);
        mVol = (ImageView) mDialog.findViewById(R.id.id_record_dialog_vol);
        mTv = (TextView) mDialog.findViewById(R.id.id_record_dialog_tv);

        //现在style文件中加新的style
        mDialog.show();

    }

    public void recording() {
        if(mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVol.setVisibility(View.VISIBLE);
            mTv.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mTv.setText("手指上滑，取消发送");
        }
    }

    public void wantToCancel() {
        if(mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVol.setVisibility(View.GONE);
            mTv.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mTv.setText("松开手指，取消发送");
        }
    }

    public void tooShort() {
        if(mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVol.setVisibility(View.GONE);
            mTv.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mTv.setText("录音时间过短");
        }

    }

    public void dismissDialog() {
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVoiceLevel(int level) {
        if(mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            //mVol.setVisibility(View.VISIBLE);
            mTv.setVisibility(View.VISIBLE);

            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            mVol.setImageResource(resId);
        }
    }
}

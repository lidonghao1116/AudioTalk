package com.sysu.audiotalk;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 15/5/29.
 */
public class RecordListAdapter extends ArrayAdapter<MainActivity.Record> {
    private List<MainActivity.Record> mDatas;
    private Context mContext;

    private int mMinWidth;
    private int mMaxWidth;

    private LayoutInflater mInflator;

    public RecordListAdapter(Context context, List<MainActivity.Record> datas) {
        super(context, -1,datas);
        mContext = context;
        mDatas = datas;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mMaxWidth = (int) (outMetrics.widthPixels*0.7f);
        mMinWidth = (int) (outMetrics.widthPixels*0.15f);

        mInflator = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflator.inflate(R.layout.item_record, parent, false);
            holder = new ViewHolder();
            holder.seconds = (TextView) convertView.findViewById(R.id.id_time);
            holder.length = convertView.findViewById(R.id.id_recorder_content);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.seconds.setText(Math.round(getItem(position).getTime())+"\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        lp.width = (int)(mMinWidth + mMaxWidth/60f * getItem(position).time);

        return convertView;
    }

    private class ViewHolder {
        TextView seconds;
        View length;
    }
}

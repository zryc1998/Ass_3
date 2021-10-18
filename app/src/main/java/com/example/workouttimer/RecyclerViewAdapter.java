package com.example.workouttimer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<TimerList> mTimerList = new ArrayList<>();
    private final SimpleDateFormat mDateFormat;
    private View mView;
    boolean mIsRunning;
    private CountDownTimer mCountDownTimer;


    @SuppressLint("SimpleDateFormat")
    public RecyclerViewAdapter(ArrayList<TimerList> timerList) {
        this.mTimerList = timerList;

        this.mDateFormat = new SimpleDateFormat("mm:ss");
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));

    }

    public void setTimerList(ArrayList<TimerList> timerList) {
        this.mTimerList = timerList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item,parent,false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final TimerList item = mTimerList.get(position);
        holder.item_workout.setText(item.getName());
        holder.item_time.setText(mDateFormat.format(item.getTime()));


        holder.itemView.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CountDown mCountDownUtil = CountDown.getCountDownTimer();
                mCountDownUtil.setMillisInFuture(mTimerList.get(position).getTime());
                mCountDownUtil.setCountDownInterval(1000);
                mCountDownUtil.setTickDelegate(new CountDown.TickDelegate() {
                    @Override
                    public void onTick(long pMillisUntilFinished) {
                        String minSec = mDateFormat.format(pMillisUntilFinished);
                        holder.item_time.setTextColor(Color.RED);
                        holder.item_time.setText(minSec);
                    }
                });
                mCountDownUtil.setFinishDelegate(new CountDown.FinishDelegate() {
                    @Override
                    public void onFinish() {
                        holder.item_time.setTextColor(Color.parseColor("#FFFFFF"));
                        holder.item_time.setText("00:00");
                        showDialog();
                    }
                });
                notifyDataSetChanged();
                mCountDownUtil.start();

            }
        });
    }

    private void showDialog() {
        Vibrator vibrator = (Vibrator) mView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] time = {0, 2000};
        vibrator.vibrate(time, -1);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mediaPlayer = MediaPlayer.create(mView.getContext(), notification);
        mediaPlayer.start();
    }

    @Override
    public int getItemCount() {
        return mTimerList.size();
    }

//    public List<TimeInfo> getData() {
//        return mTimerList;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_workout;
        private final TextView item_time;

        private ViewHolder(@NonNull View itemView ) {
            super(itemView);
            item_workout = (TextView) itemView.findViewById(R.id.item_workout);
            item_time = (TextView) itemView.findViewById(R.id.item_time);

        }
    }


}

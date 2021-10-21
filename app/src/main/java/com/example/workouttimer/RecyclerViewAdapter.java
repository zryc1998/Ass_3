package com.example.workouttimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<TimerList> mTimerList = new ArrayList<>();
    private final SimpleDateFormat mDateFormat;
    private View mView;
    boolean mIsRunning;
    boolean mCountDownTimerCreated;
    private CountDownTimer mCountDownTimer;
    private Context mContext;


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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRunning) {
                    mCountDownTimer.cancel();
                    mIsRunning = false;
                }
                else {
                    startTimer(holder, position);
                }
            }
        });

        holder.delete_square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCountDownTimerCreated == true) {
                    mCountDownTimer.cancel();
                }
                mTimerList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mTimerList.size());
                reloadMainActivity();
            }
        });
    }

    private void reloadMainActivity() {
        if (mTimerList.size() == 0 ){
            Intent intent =  new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }
    }

    // the following 2 methods are very important to keep the position right when scrolling
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    private void startTimer(ViewHolder holder, int position) {

        mCountDownTimer = new CountDownTimer(mTimerList.get(position).getTime(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String minSec = mDateFormat.format(millisUntilFinished);
                        holder.item_time.setTextColor(Color.RED);
                        holder.item_time.setText(minSec);
                        mCountDownTimerCreated = true;

            }

            @Override
            public void onFinish() {
                mIsRunning = false;
                holder.item_time.setText("00:00");
//                holder.item_time.setTextColor(Color.parseColor("#FFFFFF"));
                setOffNotification();
                mTimerList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mTimerList.size());

                // following can be used for future upgrade: timer starts automatically
//                if (mTimerList.size() > 0 ) {
//                    int p = position;
//                    if (position == getItemCount()) {
//                        p = 0;}
//                    if (p > 0) {
//                        startTimer(holder, p);
//                    }
//                }

                reloadMainActivity();
            }
        };
        notifyDataSetChanged();
        mCountDownTimer.start();

        mIsRunning = true;
    }


    private void setOffNotification() {
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_workout;
        private final TextView item_time;
        private final View delete_square;

        private ViewHolder(@NonNull View itemView ) {
            super(itemView);
            item_workout = (TextView) itemView.findViewById(R.id.item_workout);
            item_time = (TextView) itemView.findViewById(R.id.item_time);
            delete_square = (View) itemView.findViewById(R.id.delete_square);
            mContext = itemView.getContext();
        }
    }


}

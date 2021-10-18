package com.example.workouttimer;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<TimerList> mTimerList = new ArrayList<>();
    private final SimpleDateFormat mDateFormat;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TimerList item = mTimerList.get(position);
        holder.item_workout.setText(item.getName());
        holder.item_time.setText(mDateFormat.format(item.getTime()));
        long mCountDownInterval = 1000;
        CountDown mCountDownUtil = CountDown.getCountDownTimer()
                .setMillisInFuture(item.getTime())
                .setCountDownInterval(mCountDownInterval)
                .setTickDelegate(new CountDown.TickDelegate() {
                    @Override
                    public void onTick(long pMillisUntilFinished) {
                        String minSec = mDateFormat.format(pMillisUntilFinished);
                        holder.item_time.setText(minSec);
                    }
                });
        mCountDownUtil.start();
    }


    @Override
    public int getItemCount() {
        return mTimerList.size();
    }

//    public List<TimeInfo> getData() {
//        return mTimerList;
//    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_workout;
        private final TextView item_time;

        private ViewHolder(View itemView) {
            super(itemView);
            item_workout = (TextView) itemView.findViewById(R.id.item_workout);
            item_time = (TextView) itemView.findViewById(R.id.item_time);
        }
    }
}

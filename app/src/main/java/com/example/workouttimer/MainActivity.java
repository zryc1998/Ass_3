package com.example.workouttimer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<TimerList> mTimerList = new ArrayList<>();
    RecyclerViewAdapter mAdapter;
    Button mClearButton;
    boolean mStopHandler;
    boolean mIsAtTop;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTimePicker();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerViewAdapter(mTimerList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
            if(!mIsAtTop)
            {
                mClearButton.setText("Back to top");
                mClearButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFAB00")));
            }
            }
        });

        initClearButton();

    }

    private void initClearButton() {
     mClearButton = (Button) findViewById(R.id.clear_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAtTop) {
                    Objects.requireNonNull(mRecyclerView.getLayoutManager()).scrollToPosition(0);
                    mIsAtTop = true;
                    mClearButton.setText("Clear All");
                    mClearButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D50000")));
                }
                else deleteAllItems();
            }
        });
    }


    private void deleteAllItems() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mTimerList.size() == 0) {
                    mStopHandler = true;
                }
                if (!mStopHandler) {
                    View v = Objects.requireNonNull(mRecyclerView.findViewHolderForAdapterPosition(0)).itemView;
                    deleteItem(v, 0);
                } else {
                    handler.removeCallbacksAndMessages(null);
                }
                handler.postDelayed(this, 500);
            }
        };
        this.runOnUiThread(runnable);
    }

    private void deleteItem(View rowView, int position) {
        Animation anim = AnimationUtils.loadAnimation(rowView.getContext(),
                android.R.anim.slide_out_right);
        anim.setDuration(300);
        rowView.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mTimerList.size() == 0) {
//                    addEmptyView(); // adding empty view instead of the RecyclerView
                    return;
                }
                mTimerList.remove(position);
                mAdapter.notifyDataSetChanged();

                if (mTimerList.size()==0) {
                    mClearButton.setVisibility(View.INVISIBLE);
                    recreate();
                }
            }

        }, anim.getDuration());
    }


    private void initTimePicker() {
        final EditText timeTextView = findViewById(R.id.time_text_view);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        timeTextView.setRawInputType(InputType.TYPE_NULL);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);
                final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
                final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);

                numberPickerMinutes.setMaxValue(59);
                numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 0));
                numberPickerSeconds.setMaxValue(59);
                numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 0));

                Button cancel = view.findViewById(R.id.cancel);
                Button ok = view.findViewById(R.id.ok);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onClick(View v) {
                        timeTextView.setText(String.format("%02d:%02d", numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("Minutes", numberPickerMinutes.getValue());
                        editor.putInt("Seconds", numberPickerSeconds.getValue());
                        editor.apply();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        initAddButton(timeTextView);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initAddButton(EditText timeTextView) {
        EditText workoutTextView =(EditText)findViewById(R.id.workout_text_view);
        Button addButton = (Button)findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutTextView.getText().toString();
                timeTextView.getText().toString();
                if (workoutTextView.getText().toString().trim().equals("") || timeTextView.getText().toString().trim().equals("")){
                    Toast.makeText(getBaseContext(),"Workout Name and Time Can Not be Empty", Toast.LENGTH_LONG).show();
                }
                else {
                    String[] timeText;
                    timeText = timeTextView.getText().toString().split(":");
                    long milliSecInput = Long.parseLong(timeText[0])*60000 + Long.parseLong(timeText[1])*1000;

                    TimerList tl = new TimerList(workoutTextView.getText().toString(), milliSecInput);
                    mTimerList.add(tl);
                    mAdapter.setTimerList(mTimerList);
                    mAdapter.notifyDataSetChanged();
                }
                if (mTimerList.size()>0) {
                    mClearButton.setVisibility(View.VISIBLE);
                }
//                test arraylist
//                for (TimerList tl : mTimerList) {
//                    System.out.println((tl.name + "=================" + tl.time + "\n"));
//                }
            }
        });

    }
}
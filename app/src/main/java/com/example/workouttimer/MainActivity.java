package com.example.workouttimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTimePicker();
    }

    private void initTimePicker() {

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final EditText timeTV = findViewById(R.id.time_text_view);

        timeTV.setRawInputType(InputType.TYPE_NULL);
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);

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
                        timeTV.setText(String.format("%02d:%02d", numberPickerMinutes.getValue(), numberPickerSeconds.getValue()));
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
    }
}
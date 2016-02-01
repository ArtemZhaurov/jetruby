package com.example.azhaurov.jetruby;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Preferences extends AppCompatActivity{
    EditText timeValueBox;
    RadioButton rb1, rb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        timeValueBox = (EditText) findViewById(R.id.timeValueBox);
        rb1 =  (RadioButton) findViewById(R.id.radioButton1);
        rb2 =  (RadioButton) findViewById(R.id.radioButton2);
        getSetings();
    }

    public void setSetings(View view) {
        String timeValue = timeValueBox.getText().toString();
        try {
            if ((Integer.parseInt(timeValue) < 1) || (Integer.parseInt(timeValue) > 60)) {
                throw new NumberFormatException();
            } else {
                String visualEffectsValue = rb1.isChecked() ? "slide" : "fade";
                SharedPreferences settingsActivity = getSharedPreferences("GlobalSettings", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settingsActivity.edit();
                prefEditor.putString("TimeValue", timeValue);
                prefEditor.putString("VisualEffectsValue", visualEffectsValue);
                prefEditor.commit();
                this.finish();
            }
        } catch (NumberFormatException e) {
            timeValueBox.setText("");
            Toast.makeText(getApplicationContext(), "Введите число от 1 до 60", Toast.LENGTH_LONG).show();
        }
    }

    private void getSetings() {
        SharedPreferences settingsActivity = getSharedPreferences("GlobalSettings", MODE_PRIVATE);
        String timeValue = settingsActivity.getString("TimeValue", "2");
        String visualEffectsValue = settingsActivity.getString("VisualEffectsValue","slide");
        timeValueBox.setText(timeValue);
        switch (visualEffectsValue){
            case "slide":
                rb1.setChecked(true);
                rb2.setChecked(false);
                break;
            case "fade":
                rb2.setChecked(true);
                rb1.setChecked(false);
                break;
            default:
                break;
        }
    }
}

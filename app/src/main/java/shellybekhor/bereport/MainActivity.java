package shellybekhor.bereport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private CalendarView calendarView;
    private int totalHours = 0;
    private int monthHours = 0;

    public static final int TEXT_REQUEST = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = findViewById(R.id.calendar);
        setCalendar();
    }

    private void setCalendar(){
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendar,
                                            int year, int month, int day) {
                int totalBefore = totalHours;
                buildAndRunDialog();
                signSingleDay(year, month, day, totalHours - totalBefore);
            }
        });
    }

    private void buildAndRunDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_add_hours, null);
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateTextViews();
            }
        });
        dialog.show();
    }

    private void signSingleDay(int year, int month, int day, int hours){
        if (hours < 0) {
            // TODO: cancel the color of the day in the calenderView
        }
        else if (hours > 0){
            // TODO: change the color of the day in the calenderView
        }
        // if hours == 0 no change needed
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(AddHours.EXTRA_REPLY);
                if (reply != null) {
                    Log.d(LOG_TAG, reply);
                }
            }
        }
    }

    public void returnOne(View view){
        updateHours(1);
    }

    public void returnThree(View view){
        updateHours(3);
    }

    public void returnCustom(View view){
        // TODO: ask for hours from user and then get
        int num = 0;
        updateHours(num);
    }

    public void resetDay(View view){
        // TODO: discover what day was clicked and reset it
        int value = 0;
        // TODO: get the value of the day
        updateHours(-value);
    }

    private void updateHours(int hours){
        totalHours += hours;
        monthHours += hours;
        Log.d(LOG_TAG, "HOURS: " + totalHours);
        dialog.dismiss();
    }

    private void updateTextViews(){
        TextView month = findViewById(R.id.monthHours);
        month.setText(String.valueOf(monthHours));
        TextView year = findViewById(R.id.yearHours);
        year.setText(String.valueOf(totalHours));
    }

}

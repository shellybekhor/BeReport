package shellybekhor.bereport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.applandeo.materialcalendarview.*;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Global variables //
    private AlertDialog dialog;
    private CalendarView calendarView;
    private Button reportBtn;
    private PopupWindow popup;
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
        reportBtn = findViewById(R.id.reportButton);
        setReportButton();

    }


    /**
     * This method is setting the functionality of the REPORT button
     * by creating a listener and an onClick function.
     */
    private void setReportButton()
    {
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportSuccessDialog();
            }
        });
    }

    /**
     * This method is creating a pop-up announcing the monthly hours.
     * The pop-up disappears after 2 seconds.
     */
    private void reportSuccessDialog()
    {
        Log.d(LOG_TAG, "Report Clicked!");
        LayoutInflater inflater = getLayoutInflater();
        View reportSuccessView = inflater.inflate(R.layout.activity_popup_success, null);
        popup = new PopupWindow(reportSuccessView,
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popup.showAtLocation(calendarView, Gravity.CENTER,0,0);

        Button btn = (Button) reportSuccessView.findViewById(R.id.buttonclose);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });


    }



    private void setCalendar()
    {
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                int totalBefore = totalHours;
                buildAndRunDialog();
//                List<Calendar> calendars = new ArrayList<>();
//                calendars.add(eventDay.getCalendar());
//                calendarView.setSelectedDates(calendars);
                signSingleDay(totalHours - totalBefore);
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
        Window window = dialog.getWindow();
        window.setLayout((int) (getDeviceMetrics(this).widthPixels*0.9),
                (int) (getDeviceMetrics(this).heightPixels*0.3));
//        window.setGravity(Gravity.CENTER);
    }

    private void signSingleDay(int hours){
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
        View hoursBar = dialog.findViewById(R.id.customHours);
        hoursBar.setVisibility(View.VISIBLE);
        View approveButton = dialog.findViewById(R.id.approveTyping);
        approveButton.setVisibility(View.VISIBLE);

        Window window = dialog.getWindow();
        window.setLayout((int) (getDeviceMetrics(this).widthPixels*0.9),
                (int) (getDeviceMetrics(this).heightPixels*0.5));
    }

    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
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

    public void approveHours(View view) {
        EditText hoursBar = dialog.findViewById(R.id.customHours);
        if (hoursBar != null && hoursBar.getText() != null) {
            String hours = hoursBar.getText().toString();
            if (! hours.equals("")) {
                updateHours(Integer.parseInt(hours));
            }
            else{
                dialog.dismiss();
            }
        }
        else{
            updateHours(0);
        }
    }

    private void updateTextViews(){
        TextView month = findViewById(R.id.monthHours);
        month.setText(String.valueOf(monthHours));
        TextView year = findViewById(R.id.yearHours);
        year.setText(String.valueOf(totalHours));
    }

}

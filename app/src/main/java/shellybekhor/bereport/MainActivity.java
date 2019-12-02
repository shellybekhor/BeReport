package shellybekhor.bereport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.applandeo.materialcalendarview.*;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Global variables //
    private AlertDialog dialog;
    private CalendarView calendarView;
    private Button reportBtn;
    private PopupWindow popup;
    private EventDay currentDialogsDay = null;

    // to be saved to future entries to the app
    private int totalHours = 0;
    private int monthHours = 0;
    private Map dateToHours;
    private Map monthToHours;

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

        dateToHours = new HashMap();
        monthToHours = new HashMap();
        monthToHours.put(calendarView.getCurrentPageDate().getTime(), 0);
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

        // take
        Calendar c = Calendar.getInstance();
        c.setTime(calendarView.getCurrentPageDate().getTime());
        String m = new DateFormatSymbols().getMonths()[c.get(Calendar.MONTH)];
        String txt = "חודש " + m + " דווח בהצלחה!";
        TextView reportBox = reportSuccessView.findViewById(R.id.popUpWindow);
        reportBox.setText(txt);

        Button btn = reportSuccessView.findViewById(R.id.buttonclose);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    private void setCalendar() {
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                currentDialogsDay = eventDay;
                Date date = eventDay.getCalendar().getTime();
                if (! calendarView.getSelectedDates().contains(eventDay.getCalendar())) {
                    dateToHours.put(date, 0);
                    buildAndRunDialog();
                }
                else {
                    resetDay(date);
                }
            }
        });

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                updateMonthCounter();
            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                updateMonthCounter();
            }
        });
    }

    private void buildAndRunDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_add_hours, null);
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateTextViews();
                Date date = currentDialogsDay.getCalendar().getTime();
                if (dateToHours.containsKey(date) && (int) dateToHours.get(date) == 0){
                    removeDayFromSelected(currentDialogsDay);
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout((int) (getDeviceMetrics(this).widthPixels*0.9),
                (int) (getDeviceMetrics(this).heightPixels*0.3));
//        window.setGravity(Gravity.CENTER);
    }

    private void removeDayFromSelected(EventDay eventDay){
        List<Calendar> curSelected = calendarView.getSelectedDates();
        curSelected.remove(eventDay.getCalendar());
        calendarView.setSelectedDates(curSelected);
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

    public void resetDay(Date date){
        int value = (int) dateToHours.get(date);
        updateHours(-value);
        updateTextViews();
    }

    private void updateHours(int hours){
        Date date = currentDialogsDay.getCalendar().getTime();
        totalHours += hours;
        monthHours += hours;
        dateToHours.put(date, hours);

        Date month = calendarView.getCurrentPageDate().getTime();
        monthToHours.put(month, (int) monthToHours.get(month) + hours);

        Log.d(LOG_TAG, "HOURS: " + totalHours + "  DATE:  " + date);
        dialog.dismiss();
    }

    public void approveHours(View view) {
        EditText hoursBar = dialog.findViewById(R.id.customHours);
        if (hoursBar != null && hoursBar.getText() != null) {
            String hours = hoursBar.getText().toString();
            if (!hours.equals("")) {
                updateHours(Integer.parseInt(hours));
                return;
            }
        }
        dialog.dismiss();
    }

    private void updateTextViews(){
        TextView month = findViewById(R.id.monthHours);
        month.setText(String.valueOf(monthHours));
        TextView year = findViewById(R.id.yearHours);
        year.setText(String.valueOf(totalHours));
    }

    private void updateMonthCounter(){
        Date month = calendarView.getCurrentPageDate().getTime();
        if (monthToHours.containsKey(month)) {
            monthHours = (int) monthToHours.get(month);
        }
        else{
            monthHours = 0;
            monthToHours.put(calendarView.getCurrentPageDate().getTime(), 0);
        }
        updateTextViews();
    }

}

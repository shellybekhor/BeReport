package shellybekhor.bereport;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.applandeo.materialcalendarview.*;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
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
    private int totalReported = 0;
    private int monthHours = 0;
    private Map dateToHours;
    private Map monthToHours;
    private ArrayList<Date> reportedMonths;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String[] month_names = {"ינואר", "פברואר", "מרץ", "אפריל", "מאי", "יוני",
            "יולי", "אוגוסט", "ספטמבר", "אוקטובר", "נובמר", "דצמבר"};

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
        reportedMonths = new ArrayList<>();
        monthToHours.put(calendarView.getCurrentPageDate().getTime(), 0);
        updateTextViews();
        TextView monthName = findViewById(R.id.monthName);
        monthName.setText(getCurMonthName());
    }


    /**
     * This method is setting the functionality of the REPORT button
     * by creating a listener and an onClick function.
     */
    private void setReportButton() {
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reportedMonths.contains(calendarView.getCurrentPageDate().getTime())) return;
                reportSuccessDialog();
            }
        });
    }

    /**
     * This method is creating a pop-up announcing the monthly hours.
     * The pop-up disappears after 2 seconds.
     */
    private void reportSuccessDialog() {
        Log.d(LOG_TAG, "Report Clicked!");

        // Create popup window
        LayoutInflater inflater = getLayoutInflater();
        View reportSuccessView = inflater.inflate(R.layout.activity_popup_success, null);
        popup = new PopupWindow(reportSuccessView,
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popup.showAtLocation(calendarView, Gravity.CENTER,0,0);

        // Update the text by the current month
        String m = getCurMonthName();
        String txt = getResources().getString(R.string.month_x_reported_successfully, m);
        TextView reportBox = reportSuccessView.findViewById(R.id.popUpWindow);
        reportBox.setText(txt);

        // Sign this month as reported
        reportedMonths.add(calendarView.getCurrentPageDate().getTime());
        totalReported += monthHours;

        Button btn = reportSuccessView.findViewById(R.id.buttonclose);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                updateReportedMonth();
                updateTextViews();
            }
        });
    }

    private void setCalendar() {
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                // If the month was reported, make day disabled.
                if (reportedMonths.contains(calendarView.getCurrentPageDate().getTime())) {
                    List<Calendar> c = new ArrayList<>();
                    c.add(eventDay.getCalendar());
                    calendarView.setDisabledDays(c);
                    dialog.dismiss();
                    return;
                }

                // Else, check if already selected or not, act accordingly
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

    /**
     * Build the dialog of the popup for the choosing number of hours.
     * Open the dialog, and define what happen when dismiss.
     * Define the popup window size.
     */
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

    /**
     *  Unselect a day in calender.
     */
    private void removeDayFromSelected(EventDay eventDay){
        List<Calendar> curSelected = calendarView.getSelectedDates();
        curSelected.remove(eventDay.getCalendar());
        calendarView.setSelectedDates(curSelected);
    }

    /**
     * Return 1 button onClick
     * @param view buuton
     */
    public void returnOne(View view){
        updateHours(1);
    }

    /**
     * Return 3 button onClick
     * @param view buuton
     */
    public void returnThree(View view){
        updateHours(3);
    }

    /**
     * When the "custom" button is clicked.
     * @param view the custom button.
     */
    public void customButtonClicked(View view){
        View hoursBar = dialog.findViewById(R.id.customHours);
        hoursBar.setVisibility(View.VISIBLE);
        hoursBar.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(hoursBar, InputMethodManager.SHOW_IMPLICIT);

        View approveButton = dialog.findViewById(R.id.approveTyping);
        approveButton.setVisibility(View.VISIBLE);

        Window window = dialog.getWindow();
        window.setLayout((int) (getDeviceMetrics(this).widthPixels*0.9),
                (int) (getDeviceMetrics(this).heightPixels*0.5));
    }

    /**
     * Return the device sazes and layouts.
     * @param context context
     * @return metrics
     */
    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    /**
     * Called when a day is unselected. remove number of hours that day from the counters.
     * @param date
     */
    public void resetDay(Date date){
        int value = (int) dateToHours.get(date);
        updateHours(-value);
        updateTextViews();
    }

    /**
     * Update the counters, and the maps, with the new number of hours
     * that was selected in the selected day.
     * @param hours the hours that was selected.
     */
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

    /**
     * Called when the approve button is clicked.
     * @param view the button.
     */
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

    /**
     * Change the views in the main activity, and update by the counters.
     */
    private void updateTextViews(){
        String youReported = getResources().getString(R.string.you_reported, totalReported);
        TextView title = findViewById(R.id.username);
        title.setText(youReported);

        String monthCounter = getResources().getString(R.string.month_counter, monthHours);
        TextView monthText = findViewById(R.id.monthHours);
        monthText.setText(monthCounter);

        String marked = getResources().getString(R.string.x_more_marked, (totalHours - totalReported));
        TextView reported = findViewById(R.id.marked);
        reported.setText(marked);
    }

    /**
     * Change the month counter text view to be the number of hours in the current month.
     */
    private void updateMonthCounter(){
        Date month = calendarView.getCurrentPageDate().getTime();
        if (reportedMonths.contains(month)){
            updateReportedMonth();
        }
        else{
            updateUnReportedMonth();
        }

        if (monthToHours.containsKey(month)) {
            monthHours = (int) monthToHours.get(month);
        }
        else{
            monthHours = 0;
            monthToHours.put(calendarView.getCurrentPageDate().getTime(), 0);
        }
        updateTextViews();
        TextView monthName = findViewById(R.id.monthName);
        monthName.setText(getCurMonthName());
    }

    /**
     * Change a month to reported:
     *      1. report button color change to gray.
     *      2. report button text is changed.
     *      3. gray shade on month become visible.
     */
    private void updateReportedMonth(){
        TextView reportButton = findViewById(R.id.reportButton);
        reportButton.setText(getResources().getString(R.string.reported));
        reportButton.setBackground(getResources().getDrawable(R.drawable.pop_up_after_report));
        findViewById(R.id.reportedBackground).setVisibility(View.VISIBLE);
    }


    /**
     * Change a month to an unreported:
     *      1. report button color change to gray.
     *      2. report button text is changed.
     *      3. gray shade on month become visible.
     */
    private void updateUnReportedMonth(){
        TextView reportButton = findViewById(R.id.reportButton);
        reportButton.setText(getResources().getString(R.string.report));
        reportButton.setBackground(getResources().getDrawable(R.drawable.button_gradient));
        findViewById(R.id.reportedBackground).setVisibility(View.INVISIBLE);
    }

    /**
     * @return name of the current month in the calender
     */
    private String getCurMonthName(){
        Date reportedMonth = calendarView.getCurrentPageDate().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(reportedMonth);
        return String.format("%s %2s", month_names[c.get(Calendar.MONTH)], c.get(Calendar.YEAR));
    }

}

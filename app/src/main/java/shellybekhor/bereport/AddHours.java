
// =======================================================================
// BeReport App - project 1: Almog, Shelly, Carolina, Libi
// =======================================================================
// =======================================================================
// This activity is responsible for the poping up window in which the user
// selects the amount of hours volunteered.
// =======================================================================


// IMPORTS //

package shellybekhor.bereport;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
//import android.view.View;


public class AddHours extends AppCompatActivity {
    public static final String EXTRA_REPLY = "shellybekhor.bereport.extra.REPLY";
    private static final String LOG_TAG = AddHours.class.getSimpleName();

    /**
     * This function is opening a pop-up window for choosing hours
     * in case of pressing on a date in the calender
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hours);
    }

    public void returnOne(View view){
    }

    public void returnThree(View view){
    }

    public void customButtonClicked(View view){
    }

    public void approveHours(View view){
    }

}

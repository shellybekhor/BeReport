package shellybekhor.bereport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AddHours extends AppCompatActivity {

    public static final String EXTRA_REPLY = "shellybekhor.bereport.extra.REPLY";
    private static final String LOG_TAG = AddHours.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hours);
    }

    public void returnOne(View view){
    }

    public void returnThree(View view){
    }

    public void returnCustom(View view){
    }

    public void resetDay(View view){
    }

}

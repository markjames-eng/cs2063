package mobiledev.unb.ca.recyclerviewlab;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // TODO 1
        //  Get the intent that started this activity, and get the extras from it
        //  corresponding to the title and description of the course
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");


       // TODO 2
       //  Set the description TextView to be the course description
        TextView descrip = findViewById(R.id.description_textview);
        descrip.setText(description);

       // TODO 3
       //  Make the TextView scrollable
        descrip.setMovementMethod(new ScrollingMovementMethod());


       // TODO 4
       //  Set the title of the action bar to be the course title
        ActionBar action = getSupportActionBar();
        action.setTitle(title);

    }
}

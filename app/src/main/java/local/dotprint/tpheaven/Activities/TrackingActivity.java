package local.dotprint.tpheaven.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import local.dotprint.tpheaven.R;

public class TrackingActivity extends AppCompatActivity {

    private String UserData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            Intent intent = getIntent();
            UserData = intent.getStringExtra ("UserData");

            TextView view = (TextView) findViewById(R.id.user_data);
            view.setText(UserData);
        }
        catch(Exception e)
        {

        }
    }
}

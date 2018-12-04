package local.dotprint.tpheaven.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import local.dotprint.tpheaven.HeavenHR;
import local.dotprint.tpheaven.R;

public class TrackingActivity extends AppCompatActivity {

    private String UserData = "";
    private HeavenHR mHeaven;
    private Button pauseButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetupUI();
        GetDataFromIntent();
    }

    private void SetupUI(){
        setContentView(R.layout.activity_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pauseButton = (Button) findViewById(R.id.pause_resume_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mHeaven.Pause())
                    finishActivity(R.id.finishTrackingFailed);
            }
        });
        startButton =  (Button) findViewById(R.id.start_stop_button);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void GetDataFromIntent() {
        try {
            Intent intent = getIntent();
            UserData = intent.getStringExtra(getString(R.string.put_extra_user_data));
            TextView view = (TextView) findViewById(R.id.user_data);
            view.setText(UserData);
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        TrackingActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}

package local.dotprint.tpheaven.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Console;

import local.dotprint.tpheaven.HeavenHR;
import local.dotprint.tpheaven.R;

public class TrackingActivity extends AppCompatActivity {

    private HeavenHR mHeaven;
    private Button pauseButton;
    private Button startButton;
    private TextView mUserDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetupUI();
        mHeaven = new HeavenHR();
        GetDataFromIntent();
        new TrackingTask().execute((Void)null);
    }

    private void SetupUI(){
        setContentView(R.layout.activity_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pauseButton = (Button) findViewById(R.id.pause_resume_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PauseTask().execute((Void)null);
            }
        });
        startButton =  (Button) findViewById(R.id.start_stop_button);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new StartTask().execute((Void)null);
            }
        });

        mUserDataView =findViewById(R.id.user_data);
    }

    private void GetDataFromIntent() {
        try {
            Intent intent = getIntent();
            mHeaven = intent.getParcelableExtra(getString(R.string.put_extra_user_data));
            mUserDataView.setText(mHeaven.UserData);
        } catch (Exception e) {
            e.getCause();
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

    public class StartTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return mHeaven.Start();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUserDataView.setText(mHeaven.Status());
            if(!success)
                finishActivity(R.id.finishTrackingFailed);
        }

        @Override
        protected void onCancelled() {

        }
    }
    public class PauseTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return mHeaven.Pause();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUserDataView.setText(mHeaven.Status());
            if(!success)
                finishActivity(R.id.finishTrackingFailed);
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class TrackingTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            return mHeaven.Track();
        }

        @Override
        protected void onPostExecute(String s) {
            mUserDataView.setText(s);
        }
    }
}

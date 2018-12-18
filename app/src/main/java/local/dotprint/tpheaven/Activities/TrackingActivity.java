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
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Console;

import local.dotprint.tpheaven.HeavenHR;
import local.dotprint.tpheaven.R;

public class TrackingActivity extends AppCompatActivity {

    private HeavenHR mHeaven;
    private ImageButton pauseButton;
    private ImageButton startButton;
    private TextView mUserDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetupUI();
        mHeaven = new HeavenHR();
        GetDataFromIntent();
        new TrackingTask().execute((Void) null);
    }

    private void SetupUI() {
        setContentView(R.layout.activity_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pauseButton = (ImageButton) findViewById(R.id.pause_resume_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PauseTask().execute((Void) null);
            }
        });

        startButton = (ImageButton) findViewById(R.id.start_stop_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartTask().execute((Void) null);
            }
        });

        mUserDataView = findViewById(R.id.user_data);
    }

    private void GetDataFromIntent() {
        try {
            Intent intent = getIntent();
            mHeaven = intent.getParcelableExtra(getString(R.string.put_extra_user_data));
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

    public void OnStatusChanged(HeavenHR.TrackingState state) {
        switch (state){
            case CLOSED:
                pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                startButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                break;
            case PAUSED:
                pauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                startButton.setImageResource(R.drawable.ic_stop_black_24dp);
                break;
            case RUNNING:
                pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                startButton.setImageResource(R.drawable.ic_stop_black_24dp);
        }
    }

    public class StartTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return mHeaven.Start();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUserDataView.setText(mHeaven.Status().name());
            if (!success)
                finishActivity(R.id.finishTrackingFailed);
            OnStatusChanged(mHeaven.Status());
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
            mUserDataView.setText(mHeaven.Status().name());
            if (!success)
                finishActivity(R.id.finishTrackingFailed);
            OnStatusChanged(mHeaven.Status());
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class TrackingTask extends AsyncTask<Void, Void, HeavenHR.TrackingState> {

        @Override
        protected HeavenHR.TrackingState doInBackground(Void... voids) {
            mHeaven.GetWorkingTimes();
            return mHeaven.Track();
        }

        @Override
        protected void onPostExecute(HeavenHR.TrackingState s) {
            mUserDataView.setText(s.name());
            OnStatusChanged(s);
        }
    }
}

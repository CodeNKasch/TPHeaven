package local.dotprint.tpheaven.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
    protected void onResume() {
        super.onResume();
        new CheckSessionTask().execute((Void) null);
        new HourTrackingTask().execute((Void)null);
        new CheckSessionTask().execute((Void) null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new CheckSessionTask().execute((Void) null);
        new HourTrackingTask().execute((Void)null);
        new TrackingTask().execute((Void) null);
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
    public void OnTimeTrackingChanged(){
        String text = "%s %02d:%02d";

        Integer hours = mHeaven.total / 60;
        Integer minutes =  (int)((((float) mHeaven.total / 60.0) - hours ) * 60.0);
        mUserDataView.setText(String.format(text, mHeaven.Status().name(), hours, minutes));

        float total = (mHeaven.approved + mHeaven.requested);
        float fhours = total / 60;
        hours = (int) (fhours);
        minutes = (int) ((fhours - hours) * 60);
        //mUserDataView.setText(String.format(text, mHeaven.Status().name(), hours, minutes));
    }

    public void OnStatusChanged(HeavenHR.TrackingState state) {
        switch (state) {
            case CLOSED:
                pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                startButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                pauseButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                new HourTrackingTask().execute((Void)null);
                break;
            case PAUSED:
                pauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                startButton.setImageResource(R.drawable.ic_stop_black_24dp);
                pauseButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                break;
            case RUNNING:
                pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                startButton.setImageResource(R.drawable.ic_stop_black_24dp);
                pauseButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.VISIBLE);
        }
    }

    public void finishTrackingActivity() {
        Intent i = new Intent();
        i.putExtra(getString(R.string.put_extra_user_data), mHeaven);
        setResult(RESULT_CANCELED, i);
        finish();
    }

    public class StartTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return mHeaven.Start();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (!success)
                finishTrackingActivity();
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
            if (!success)
                finishTrackingActivity();
            OnStatusChanged(mHeaven.Status());
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class TrackingTask extends AsyncTask<Void, Void, HeavenHR.TrackingState> {

        @Override
        protected HeavenHR.TrackingState doInBackground(Void... voids) {
            return mHeaven.Track();
        }

        @Override
        protected void onPostExecute(HeavenHR.TrackingState s) {
            OnStatusChanged(s);
        }
    }

    public class CheckSessionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return mHeaven.CheckSessionIsExpired();
        }

        @Override
        protected void onPostExecute(Boolean isExpired) {
            super.onPostExecute(isExpired);
            if (!isExpired)
                return;
            finishTrackingActivity();
        }
    }

    public class HourTrackingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            boolean hasTotal = mHeaven.GetWorkingTimes();
            boolean hasSummery = mHeaven.WorkingTimeSummery();
            return (Void)null;
        }

        @Override
        protected void onPostExecute(Void n) {
            //tracking done mHeaven should have now the new properties
            OnTimeTrackingChanged();
        }
    }
}

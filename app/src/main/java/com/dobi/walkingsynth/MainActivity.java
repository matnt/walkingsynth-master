package com.dobi.walkingsynth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dobi.walkingsynth.accelerometer.Accelerometer;
import com.dobi.walkingsynth.accelerometer.Accelerometer2;
import com.dobi.walkingsynth.accelerometer.AccelerometerDetector;
import com.dobi.walkingsynth.accelerometer.AccelerometerGraph;
import com.dobi.walkingsynth.accelerometer.AccelerometerProcessing;
import com.dobi.walkingsynth.accelerometer.OnStepCountChangeListener;
import com.dobi.walkingsynth.accelerometer.TimeCounter;
import com.opencsv.CSVWriter;
import org.achartengine.GraphicalView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Starting point. Sets the whole UI.
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String PREFERENCES_NAME = "Values";
    private static final String PREFERENCES_VALUES_THRESHOLD_KEY = "threshold";
    private SharedPreferences preferences;
    private int mStepCount = 0;

    private AccelerometerDetector mAccelDetector;
    private AccelerometerGraph mAccelGraph;
    private final AccelerometerProcessing mAccelerometerProcessing = AccelerometerProcessing.getInstance();

    private TextView mThreshValTextView;
    private TextView mStepCountTextView;
    private TextView mTimeValTextView;

    //private int mFileIndex;

    private TimeCounter mTimer;
    private Handler mHandler = new Handler();

    // constant reference
    //private String[] data = {"STT", "Date time","MAGNITUDE", "MOV_AVERAGE","Step number" };
    //private String[] data = {"STT","MAGNITUDE", "MOV_AVERAGE","Step number" };
    private String[] data = {"STT","x", "y", "z", "Step number" };
    //private ArrayList<Accelerometer> arrayList = new ArrayList<>();
    private ArrayList<Accelerometer2> arrayList = new ArrayList<>();
    private Calendar c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mFileIndex = 0;
        //arrayList = new ArrayList<Accelerometer>();
        //arrayList = mAccelDetector.getArr();

        // set default locale:
        Locale.setDefault(Locale.ENGLISH);

        // accelerometer graph setup:
        mAccelGraph = new AccelerometerGraph(AccelerometerProcessing.THRESH_INIT_VALUE);

        // get and configure text views
        mThreshValTextView = (TextView)findViewById(R.id.threshval_textView);
        formatThreshTextView(AccelerometerProcessing.THRESH_INIT_VALUE);
        mStepCountTextView = (TextView)findViewById(R.id.stepcount_textView);
        mStepCountTextView.setText(String.valueOf(0));
        mTimeValTextView = (TextView)findViewById(R.id.timeVal_textView);

        // timer counter
        mTimer = new TimeCounter(mHandler,mTimeValTextView);
        mTimer.start();

        // UI default setup
        GraphicalView graphicalView = mAccelGraph.getView(this);
        graphicalView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout graphLayout = (LinearLayout)findViewById(R.id.graph_layout);
        graphLayout.addView(graphicalView);

        // initialize accelerometer
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelDetector = new AccelerometerDetector(sensorManager, mAccelGraph);
        mAccelDetector.setStepCountChangeListener(new OnStepCountChangeListener() {
            @Override
            public void onStepCountChange(long eventMsecTime) {
                ++mStepCount;
                mStepCountTextView.setText(String.valueOf(mStepCount));

            }
        });
        arrayList = mAccelDetector.getArr();

        // seek bar configuration
        initializeSeekBar();
        //initializeButtonLog();

    }

    public void initializeButtonLog(double a){
        //arrayList = mAccelDetector.getArr();
        String folder = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filename = folder + File.separator + "sensorfile_" + a + ".csv";
        CSVWriter csvWriter = null;
        try {
            File file = new File(filename);
            // nếu file k tồn tại
            if (!file.exists()) {
                file.createNewFile();
                csvWriter = new CSVWriter(new FileWriter(filename));
            } else {
                FileWriter writer = new FileWriter(filename, true);
                csvWriter = new CSVWriter(writer);
            }
            csvWriter.writeNext(data);
            for (int i = 0; i < arrayList.size(); i++){
                String stt = String.valueOf(i+1);
               // String date = getDate();
                String Ax = String.valueOf(arrayList.get(i).getX());
                String Ay = String.valueOf(arrayList.get(i).getY());
                String Az = String.valueOf(arrayList.get(i).getZ());
                String number = String.valueOf(arrayList.get(i).getN());
//                String tb = String.valueOf(arrayList.get(i).getTb());
//                String tb2 = String.valueOf(arrayList.get(i).getValueafterfilt());
//                String step_number = String.valueOf(arrayList.get(i).getStep_number());
                //String[] unitdata = new String[]{stt, date, tb, tb2, step_number};
                //String[] unitdata = new String[]{stt, tb, tb2, step_number};
                String[] unitdata = new String[]{stt, Ax, Ay,Az, number};
                csvWriter.writeNext(unitdata);
            }
            arrayList.clear();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public String getDate() {
//        int mYear = c.get(Calendar.YEAR);
//        int mMonth = c.get(Calendar.MONTH) + 1;
//        int mDay = c.get(Calendar.DATE);
//        int mHour = c.get(Calendar.HOUR_OF_DAY);
//        int mMinute = c.get(Calendar.MINUTE);
//        int mSec = c.get(Calendar.SECOND);
//
//        // Build the file name using date and time
//        return (mYear + "_" + mMonth + "_" + mDay + "   " + mSec + ":" + mMinute + ":" + mHour );
//
//    }

    /**
     * SeekBar is the publisher.
     * The subscribers are: AccelerometerGraph and AccelerometerProcessing instances.
     */
    private void initializeSeekBar() {
        final SeekBar seekBar = (SeekBar)findViewById(R.id.offset_seekBar);
        seekBar.setMax(40);
        seekBar.setProgress((int) AccelerometerProcessing.getInstance().getThresholdValue());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double threshold = AccelerometerProcessing.THRESH_INIT_VALUE * (progress + 90) / 100;
                Log.d("Progress value", progress+"");
                mAccelerometerProcessing.onThresholdChange(threshold);
                mAccelGraph.onThresholdChange(threshold);
                formatThreshTextView(threshold);
                initializeButtonLog(threshold);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void formatThreshTextView(double v) {
        final DecimalFormat df = new DecimalFormat("#.##");
        mThreshValTextView.setText(String.valueOf(df.format(v)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        // set string values for menu
        String[] titles = getResources().getStringArray(R.array.nav_drawer_items);
        for (int i = 0; i < titles.length; i++) {
            menu.getItem(i).setTitle(titles[i]);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_threshold:
                saveThreshold();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveThreshold() {
        preferences.edit().putFloat(
                PREFERENCES_VALUES_THRESHOLD_KEY,
                (float) AccelerometerProcessing.getInstance().getThresholdValue()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAccelDetector.startDetector();
        mTimer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause");
        mAccelDetector.stopDetector();
        mTimer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
    }
}

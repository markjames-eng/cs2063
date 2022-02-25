package mobiledev.unb.ca.threadinglab;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import mobiledev.unb.ca.threadinglab.model.GeoData;
import mobiledev.unb.ca.threadinglab.util.JsonUtils;

public class DownloaderTask {
    private static final int DOWNLOAD_TIME = 4;      // Download time simulation

    private final Context appContext;

    private final GeoDataListActivity activity;
    private Button refreshButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<GeoData> mGeoData;

    public DownloaderTask(GeoDataListActivity activity) {
        this.activity = activity;
        appContext = activity.getApplicationContext();
    }

    public DownloaderTask setRefreshButton(Button refreshButton) {
        this.refreshButton = refreshButton;
        return this;
    }

    public DownloaderTask setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public DownloaderTask setupRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    public void execute() {
        // TODO
        //  Disable the button so it can't be clicked again once a download has been started
        //  Hint: Button is subclass of TextView. Read this document to see how to disable it.
        //  http://developer.android.com/reference/android/widget/TextView.html

        Log.i("LAB6","execting");
        refreshButton.setEnabled(false);

        // TODO
        //  Set the progress bar's maximum to be DOWNLOAD_TIME, its initial progress to be
        //  0, and also make sure it's visible.
        //  Hint: Read the documentation on ProgressBar
        //  http://developer.android.com/reference/android/widget/ProgressBar.html

        progressBar.setMax(DOWNLOAD_TIME);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        // Perform background call to read the information from the URL
        Executors.newSingleThreadExecutor().execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Log.i("LAB6","in the thread");
            // TODO
            //  Create an instance of JsonUtils and get the data from it,
            //  store the data in mGeoDataList
            JsonUtils jutil = new JsonUtils();
            ArrayList<GeoData> mGeoDataList = new ArrayList<>(jutil.getGeoData());



            // Simulating long-running operation
            for (int i = 1; i < DOWNLOAD_TIME; i++) {
                sleep();
                // TODO
                //  Update the progress bar using values
                int finalI = i;
                mainHandler.post(()->{
                 progressBar.setProgress(finalI);
                });
                Log.i("LAB6","updating progress bar");

            }

            // TODO
            //  Using the updateDisplay method update the UI with the results

            mainHandler.post(()-> {
                        updateDisplay(mGeoDataList);
                    }
            );
            Log.i("LAB6","is online");

        });
    }

    private void sleep() {
        try {
            int mDelay = 500;
            Thread.sleep(mDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateDisplay(ArrayList<GeoData> mGeoDataList) {
        // TODO
        //  With the download completed, enable the button again
        refreshButton.setEnabled(true);

        // TODO
        //  Reset the progress bar, and make it disappear
        progressBar.setProgress(0);
        progressBar.setVisibility(View.INVISIBLE);


        // TODO
        //  Setup the RecyclerView

        setupRecyclerView(mGeoDataList);

        // TODO
        //  Create a Toast indicating that the download is complete. Set its text
        //  to be the result String from doInBackground


        CharSequence text = activity.getString(R.string.download_complete);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(appContext, text, duration);
        toast.show();

    }

    private void setupRecyclerView(List<GeoData> mGeoDataList) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mGeoDataList, activity, activity.isTwoPane()));
    }
}

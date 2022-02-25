package mobiledev.unb.ca.lab4skeleton;

import static android.content.Intent.ACTION_BATTERY_LOW;
import static android.content.Intent.ACTION_BATTERY_OKAY;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss";

    private AlarmManager alarmManagr;
    private PendingIntent alarmIntent;
    private static String CHANNEL_ID = "AlarmNoti";

    // Attributes for storing the file photo path
    private String currentPhotoPath;
    private String imageFileName;

    // Activity listeners
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating intent filters (note -  these ACTION_s needed to be imported)
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BATTERY_LOW);
        intentFilter.addAction(ACTION_BATTERY_OKAY);

        //register the battery info receiver
        registerReceiver(batteryInfoReceiver,intentFilter);

        createNotificationChannel();

        Button cameraButton = findViewById(R.id.button);
        cameraButton.setOnClickListener(view -> dispatchTakePhotoIntent());

        // Register the activity listener
        setCameraActivityResultLauncher();

        //alarm things
        alarmManagr = (AlarmManager)this.getSystemService(MainActivity.this.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);

        //setting the alarm
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 15);

        alarmManagr.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*5,alarmIntent);
        Log.i(TAG, "Starting Alarm");

    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == ACTION_BATTERY_LOW){
                if (alarmManagr!= null) {
                    alarmManagr.cancel(alarmIntent);
                }
                Log.i(TAG,"low battery - cancelling the alarm");
                Toast.makeText(createDeviceProtectedStorageContext().createDeviceProtectedStorageContext(), "Low battery - Cancelling alarm", Toast.LENGTH_SHORT).show();
            }
            if(intent.getAction() == ACTION_BATTERY_OKAY){
                //setting the alarm
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 15);

                alarmManagr.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*5,alarmIntent);
                Log.i(TAG,"Battery okay - starting alarm again");
                Toast.makeText(createDeviceProtectedStorageContext().createDeviceProtectedStorageContext(), "Starting Alarm again", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryInfoReceiver);
        Log.i(TAG, "BatteryInfoReceiver unregistered");

    }


    // Private Helper Methods
    private void setCameraActivityResultLauncher() {
        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        galleryAddPic();
                    }
                });
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there is a camera activity to handle the intent
        try {
            // Set the File object used to save the photo
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Exception found when creating the photo save file");
            }

            // Take the picture if the File object was created successfully
            if (null != photoFile) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "mobiledev.unb.ca.lab3intents.provider", //why is this lab3?
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Calling this method allows us to capture the return code
                cameraActivityResultLauncher.launch(takePictureIntent);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Unable to load activity", e);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(TIME_STAMP_FORMAT, Locale.getDefault()).format(new Date());
        imageFileName = "IMG_" + timeStamp + "_";

        File storageDir =  getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",   // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Log.d(TAG, "Saving image to the gallery");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above
            mediaStoreAddPicToGallery();
        } else {
            // Pre Android 10
            mediaScannerAddPicToGallery();
        }
        Log.i(TAG, "Image saved!");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void mediaStoreAddPicToGallery() {
        String name = imageFileName;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        ContentResolver resolver = getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try (OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri))) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e){
            Log.e(TAG,"Error saving the file ", e);
        }
    }

    private void mediaScannerAddPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}


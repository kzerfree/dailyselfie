package com.example.dailyselfie;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 10;
    public static final int REQUEST_CODE_INPUT=113;
    public static final int RESULT_CODE_SAVE = 115;
    private ImageAdapter adapter;
    private ListView listView;
    private String currentPhotoPath;
    private String currentSelfieName;
    private static final long INTERVAL_TIME = 30 * 1000L; // 30 second
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        listView = (ListView) findViewById(R.id.selfie_list);
        adapter = new ImageAdapter(getApplicationContext());
        listView.setAdapter(adapter);


        /// hien thi chi tiet anh
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageData item = (ImageData) adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, com.example.dailyselfie.ImageDetail.class);
                intent.putExtra("item",item);
                startActivityForResult(intent,REQUEST_CODE_INPUT);
            }
        });

        createAlarm();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_camera:
                openCameraActivityForResult();
                break;
            case R.id.action_delete_selected:
                deleteSelectedSelfies();
                break;
            case R.id.action_delete_all:
                deleteAllSelfies();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //Kiểm tra có đúng requestCode =REQUEST_CODE_INPUT hay không
        if(requestCode==REQUEST_CODE_INPUT) {
            switch (resultCode) {
                case RESULT_CODE_SAVE:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        ImageData item = (ImageData) b.getSerializable("data");
                        adapter.replace(item);
                        Log.i("test2", item.getDescription());
                        listView.setAdapter(adapter);
                    }
                    break;
                default:

                    break;
            }
        }
    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                   if (result.getResultCode() == Activity.RESULT_OK) {
                        File photoFile = new File(currentPhotoPath);
                        File selfieFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                currentSelfieName + ".jpg");
                        photoFile.renameTo(selfieFile);
                        ImageData data = new ImageData(selfieFile.getAbsolutePath(), currentSelfieName);
                        Log.i("test1", data.getPath() + " - " + data.getName());
                        adapter.add(data);

                    } else {
                        File photoFile = new File(currentPhotoPath);
                        photoFile.delete();
                    }


                }
            });


    //  hàm mở camera
    public void openCameraActivityForResult() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(MainActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                //Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraActivityResultLauncher.launch(takePictureIntent);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        currentSelfieName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + currentSelfieName + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void deleteSelectedSelfies() {
        ArrayList<ImageData> selectedSelfies = adapter.getSelectedItem();
        for (ImageData imageData : selectedSelfies) {
            File selfieFile = new File(imageData.getPath());
            selfieFile.delete();
        }
        adapter.clearSelected();
    }

    private void deleteAllSelfies() {
        for (ImageData imageData : adapter.getAll()) {
            File selfieFile = new File(imageData.getPath());
            selfieFile.delete();
        }
        adapter.clearAll();
    }

    private void createAlarm() {
        try {
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + INTERVAL_TIME,
                    INTERVAL_TIME,
                    pendingIntent);
        }
        catch (Exception exception) {
            Log.d("ALARM", exception.getMessage().toString());
        }
    }

    private void unsetAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

}
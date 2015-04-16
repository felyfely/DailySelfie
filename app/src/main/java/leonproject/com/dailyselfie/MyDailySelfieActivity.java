package leonproject.com.dailyselfie;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class MyDailySelfieActivity extends Activity {

    private PendingIntent pendingIntent;
    public static final int REQUEST_PHOTO = 0;

    private GridView gridView;
    private Intent i;
    private SelfieAdapter adapter;
    private SQLiteDatabase dbReader;
    private Cursor cursor;
    private ImageView full_image_display;
    private Uri outputFileUri = null;
    private SQLiteDatabase dbWriter;
    private SelfieDB selfieDB;
    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_daily_selfie);
        selfieDB = new SelfieDB(this);
        dbWriter = selfieDB.getWritableDatabase();
        initView();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MyDailySelfieActivity.this, 0, alarmIntent, 0);
        startAlarm();
    }

    public void startAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1 * 60 * 1000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public void deleteItem(){
        //TODO
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_daily_selfie, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_camera_button:
                takePhoto();
                return true;
            case R.id.cancel_alarm:
                cancelAlarm();
                break;
            case R.id.add_alarm:
                startAlarm();
                break;
            case R.id.action_delete_button:
                deleteItem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void takePhoto() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);//利用intent去開啟android本身的照相介面

        String imageFileName = "JPEG_" + UUID.randomUUID() + "_.jpg";


        File folder = new File(Environment.getExternalStorageDirectory() + "/DCIM/CAMERA/");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        photoFile = new File(
                Environment.getExternalStorageDirectory() + "/DCIM/CAMERA/",
                imageFileName);

        if (photoFile.exists()) {
            photoFile.delete();
        }

        outputFileUri = Uri.fromFile(photoFile);


        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, REQUEST_PHOTO);

    }

    public String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
        Date curDate = new Date();
        return format.format(curDate);
    }


    public void initView() {
        gridView = (GridView) findViewById(R.id.list);
        full_image_display = (ImageView) findViewById(R.id.image_display);

        selfieDB = new SelfieDB(this);
        dbReader = selfieDB.getReadableDatabase();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                cursor.moveToPosition(position);

                full_image_display.setImageBitmap(decodeFile(new File(cursor.getString(cursor.getColumnIndex(SelfieDB.PHOTOPATH))), 600));
                gridView.setVisibility(View.GONE);
                full_image_display.setVisibility(View.VISIBLE);
            }
        });



        full_image_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (full_image_display.getVisibility() == View.VISIBLE) {
                    full_image_display.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {

            addDB();


        }
    }

    private Bitmap decodeFile(File f, int requiredSize) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_SIZE = requiredSize;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Log.d("Daily", "FileNotFound");

            return null;
        }

    }

    public void selectDB() {
        cursor = dbReader.query(selfieDB.TABLE_NAME, null, null, null, null,
                null, null, null);
        adapter = new SelfieAdapter(this, cursor);
        gridView.setAdapter(adapter);

    }

    public void addDB() {
        ContentValues cv = new ContentValues();
        cv.put(SelfieDB.TIME, getTime());

        cv.put(SelfieDB.PHOTOPATH, outputFileUri.getPath());

        dbWriter.insert(SelfieDB.TABLE_NAME, null, cv);
    }

    public void deleteDB(String key) {
        String whereClause = SelfieDB.TIME+"?";
        String[] whereArgs = {key};
        dbWriter.delete(SelfieDB.TABLE_NAME, whereClause, whereArgs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDB();
    }
}

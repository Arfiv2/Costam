package com.example.artur.zaliczeniowa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PoTreninguActivity extends Activity
{
    Context context;
    Intent intent;
    TextView timerTextView, kmTextView, textVievKcal2;
    double odl, kcal, czas, predkosc;
    int min, sek;
    //String przekaz,przekaz1,przekaz2, przekaz3;
    String mImageFileLocation = "";
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 1;

    DecimalFormat REAL_FORMATTER = new DecimalFormat("0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potreningu);
        //FullScreencall();

        kmTextView = (TextView) findViewById(R.id.kmTextView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        textVievKcal2 = (TextView) findViewById(R.id.textVievKcal2);

        String name = getIntent().getStringExtra("name");
        String minuty = getIntent().getStringExtra("minuty");
        String sekundy = getIntent().getStringExtra("sekundy");

        odl = Double.parseDouble(name);
        min = Integer.parseInt(minuty);
        sek = Integer.parseInt(sekundy);

        czas = min+(1/(60/sek));
        odl = odl/1000;
        predkosc = czas / odl;

        if(predkosc >= 8)
        {
            predkosc = 8.1;
        }
        if(predkosc < 8 && predkosc >= 6.5)
        {
            predkosc = 9.9;
        }
        if(predkosc < 6.5 && predkosc > 4)
        {
            predkosc = 12;
        }
        if(predkosc <= 4)
        {
            predkosc = 16;
        }

        if(odl == 0)
        {
            kcal = 0;
        }
        else
        {
            kcal = czas * 0.01666667 * predkosc * 60;
        }

        timerTextView.setText(String.format("%d:%02d", min, sek));
        kmTextView.setText(name);
        textVievKcal2.setText(REAL_FORMATTER.format(kcal));
    }
    /*
    @Override
    public void onResume()
    {
        super.onResume();
        FullScreencall();
    }*/

    public void wyjdz(View view)
    {
        context = getApplicationContext();
        intent = new Intent(context,MainActivity.class);
        startActivity(intent);
    }
    public void bazar(View view)
    {
        /*przekaz = String.valueOf(odl);
        przekaz1 = String.valueOf(min);
        przekaz2 = String.valueOf(sek);
        przekaz3 = String.valueOf(kcal);
        */
        context = getApplicationContext();
        intent = new Intent(context,BazaActivity.class);
        //intent.putExtra("odleglosc", przekaz);
        //intent.putExtra("minuty", przekaz1);
        //intent.putExtra("sekundy", przekaz2);
        //intent.putExtra("kcal", przekaz3);
        startActivity(intent);
    }
    /*
    public void FullScreencall()
    {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19)
        {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        }
        else if(Build.VERSION.SDK_INT >= 19)
        {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }*/

    public void takePhoto(View view)
    {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            callCameraApp();
        }
        else
        {
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                Toast.makeText(this, "Kurwa, czemu to nie działa?", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permissions, int[] grantResults)
    {
        if(requestCode == REQUEST_EXTERNAL_STORAGE_RESULT)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callCameraApp();
            }
            else
            {
                Toast.makeText(this, "Kurwa, dalej nie działa", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void callCameraApp()
    {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try
        {
            photoFile = createImageFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK)
        {

        }
    }

    File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);
        mImageFileLocation = image.getAbsolutePath();

        return image;
    }

}

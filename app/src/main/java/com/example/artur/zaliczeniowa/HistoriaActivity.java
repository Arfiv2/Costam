package com.example.artur.zaliczeniowa;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class HistoriaActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia);
        FullScreencall();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        FullScreencall();
    }
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
    }
}

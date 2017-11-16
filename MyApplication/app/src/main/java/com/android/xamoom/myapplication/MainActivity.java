package com.android.xamoom.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.Normalizer;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    String appName = (String) getApplicationContext().getApplicationInfo().loadLabel(getApplicationContext().getPackageManager());
    appName = appName.replace("ä", "ae");
    appName = appName.replace("ö", "oe");
    appName = appName.replace("ü", "ue");
    appName = appName.replace("Ä", "AE");
    appName = appName.replace("Ö", "OE");
    appName = appName.replace("Ü", "UE");

    Log.v("Test", appName);
  }
}

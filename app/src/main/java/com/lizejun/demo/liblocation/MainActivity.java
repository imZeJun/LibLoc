package com.lizejun.demo.liblocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lib.location.LocExecutor;
import com.lib.location.LocParams;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocExecutor.getInstance().buildWorker(this, new LocParams.Builder().cacheTime(2000).build(), null);
    }
}

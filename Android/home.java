package com.paulfoleyblogs.paul.homeseccontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.view.View.OnClickListener;

/**
 * Created by pcfoley on 18/03/2016.
 */
public class home extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton nextCam = (ImageButton) findViewById(R.id.alarm);
        nextCam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent devIntent = new Intent(home.this, alarm.class);
                home.this.startActivity(devIntent);
            }
        });

        ImageButton nextDev = (ImageButton) findViewById(R.id.gates);
        nextDev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent devIntent = new Intent(home.this, com.paulfoleyblogs.paul.homeseccontrol.gates.class);
                home.this.startActivity(devIntent);
            }
        });

        ImageButton nextLog = (ImageButton) findViewById(R.id.camera);
        nextLog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent devIntent = new Intent(home.this, com.paulfoleyblogs.paul.homeseccontrol.camera.class);
                home.this.startActivity(devIntent);
            }
        });

        ImageButton nextSet = (ImageButton) findViewById(R.id.lights);
        nextSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent devIntent = new Intent(home.this, com.paulfoleyblogs.paul.homeseccontrol.lights.class);
                home.this.startActivity(devIntent);
            }
        });

    }
}


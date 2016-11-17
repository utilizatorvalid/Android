package com.tppa.l.cameraapp;


import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.tppa.l.cameraapp.CameraView.CameraView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private int nrPhotos = 0;
    private File sdCardDirectory = Environment.getExternalStorageDirectory();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            mCamera = Camera.open();
        }catch (Exception e){
            Log.d("Error","Failer to get Camera"+ e.getMessage());
        }

        if(mCamera != null){
            mCameraView = new CameraView(this, mCamera);
            FrameLayout camera_view  = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);
        }

        ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        ImageButton imgTake = (ImageButton)findViewById(R.id.imgTake);
        imgTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nrPhotos ++;
                File image = new File(sdCardDirectory,"test"+String.valueOf(nrPhotos)+".jpg");
                mCameraView.TakePicture(image.getAbsolutePath());
//                Toast.makeText(getBaseContext(), image.getAbsolutePath()+"saved",Toast.LENGTH_LONG);
                Log.d("YAH",image.getAbsolutePath()+"saved");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.tppa.l.cameraapp.CameraView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by L on 11/16/2016.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;
    private List<Camera.Size> mSupportedPreviewSizes;
    private String pictureFileName;
    private Camera.Size mPreviewSize;

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

        bestSize = sizeList.get(0);

        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) >
                    (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
        }

        return bestSize;
    }

    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mCamera.setDisplayOrientation(90);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        this.context = context;

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Camera.Parameters p = mCamera.getParameters();
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(p);

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("Error", "Camera Error on surfaceCreated" + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null)
            return;
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.d("Error", "Error while stop preview" + e.getMessage());
        }

        try {
            Camera.Parameters params = mCamera.getParameters();
            mPreviewSize = getBestPreviewSize(width, height, params);
            if (mPreviewSize != null)
                Log.d("CAMSIZE",String.valueOf(mPreviewSize.width)+"x"+String.valueOf(mPreviewSize.height));
                params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                params.setExposureCompensation(0);
                params.setPictureFormat(ImageFormat.JPEG);
                params.setPictureSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(params);

            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("Error", "Camera error on surfaceChanged" + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
//        mCamera.release();
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //camera.startPreview();
            File pictureFile = new File(pictureFileName);
            Toast.makeText(context, pictureFile.toString(), Toast.LENGTH_SHORT).show();
            if (pictureFileName == null)
                return;
            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
            try {
                FileOutputStream out = new FileOutputStream(pictureFileName);
                picture.compress(Bitmap.CompressFormat.JPEG,100, out);
                picture.recycle();
                mCamera.startPreview();
            }catch (Exception e) {
                Log.d("Error", "Error while saving taken phote" + e.getMessage());
            }
        }
    };

    public void TakePicture(String fileName) {
        if (mCamera != null) {
            pictureFileName = fileName;
            mCamera.takePicture(null, null, pictureCallback);
            Toast.makeText(context, pictureFileName, Toast.LENGTH_SHORT).show();
        }
    }

}

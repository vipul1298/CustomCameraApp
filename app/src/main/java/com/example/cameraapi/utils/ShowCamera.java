package com.example.cameraapi.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**This class is responsible for Camera action**/
public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {


    Camera camera;
    SurfaceHolder holder;

    public ShowCamera(Context context, Camera camera) {
        super(context);
        this.camera=camera;
        holder=getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Camera.Parameters params = camera.getParameters(); //Camera parameters

        List<Camera.Size> sizes = params.getSupportedPictureSizes(); //Listing all sizes available
        Camera.Size mSize = null;

        //getting the particular size ,if available in the above list
        for(Camera.Size size : sizes){
            if(size.width<=1024 && size.height<=768){
                if(mSize==null || (size.width>mSize.width && size.height>mSize.height)){
                    mSize=size;
                }
            }
        }


        //change the orientation of the camera

        if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        }
        else{
            params.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }


        params.setPictureSize(mSize.width,mSize.height);//setting the size
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//Auto focus mode

        camera.setParameters(params);//Setting the parameters

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (Exception e){
            Log.d("Show Camera", "Error starting camera preview: " + e.getMessage());
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        camera.stopPreview();

    }

}

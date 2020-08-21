package com.example.cameraapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cameraapi.utils.ShowCamera;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**Code by Vipul Chaurasia **/

public class MainActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    ImageView take_photo,gallery;


    @Override
    protected void onStart() {
        super.onStart();
        //Runtime Permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},101);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        frameLayout=findViewById(R.id.frame_layout);
        take_photo=findViewById(R.id.capture);
        gallery=findViewById(R.id.show_gallery);




        //open the camera
        camera = getCameraInstance();   //getting instance of the camera
        showCamera = new ShowCamera(this,camera);  //initializaton of helper class for camera action
        frameLayout.addView(showCamera);

        //click image
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera!=null){
                    camera.takePicture(null,null,mPictureCallback);
                }
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();

            }
        });

        //Intent to Gallery screen
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),GalleryActivity.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 101:
                if(grantResults.length>0){
                    Toast.makeText(this, "Restart the App again!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //callback to process the taken image
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            File pictureFile = getOutputMediaFile();

            if(pictureFile==null){
                return;
            }else{
                try{
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(bytes);//storing the images into the "MyImages" folder
                }catch (Exception e){

                }
            }
        }
    };

    // File management to store image into the external storage
    private File getOutputMediaFile(){
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        else{
            File  folder = new File(Environment.getExternalStorageDirectory()+File.separator+"MyImages");
            if(!folder.exists()){
                folder.mkdirs();
            }
            File outputFile = new File(folder,new Date().getTime()+".jpg");
            return outputFile;
        }
    }

    // release camera
    private void releaseCamera(){
        if (camera != null){
            camera.release();    // release the camera
            camera = null;
        }

    }


    // camera instance
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }



    //lifecycle handling
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(camera==null){
            camera=getCameraInstance();
        }
    }
}

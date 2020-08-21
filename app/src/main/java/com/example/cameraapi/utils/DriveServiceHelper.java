package com.example.cameraapi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.util.Pools;

import com.example.cameraapi.GalleryActivity;
import com.example.cameraapi.MainActivity;
import com.example.cameraapi.adapters.PictureAdapter;
import com.example.cameraapi.models.PictureItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**DriveServiceHelper class,for uploading the images**/
public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public DriveServiceHelper(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }


    //method to upload all images to google drive ,even if the app is closed
    public Task<String> createFile(String path){

        return Tasks.call(mExecutor,() ->{

            java.io.File folder = new java.io.File(path);
            java.io.File [] files = folder.listFiles();
            File myFile =null;

            for(int i=0;i<files.length;i++){

                File fileMetaData = new File();
                fileMetaData.setName(files[i].getName());

                java.io.File file = new java.io.File(path+files[i].getName());

                FileContent mediaContent = new FileContent("image/jpeg",file);

                try{
                    myFile=mDriveService.files().create(fileMetaData,mediaContent).execute();
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(myFile==null){
                    throw new IOException("Null result when creating file creation");
                }

            }

            return myFile.getId();

        });
    }

    /**pending**/
    // method to upload the files to the drive, which is left in the gallery even if the app is closed
    public Task<String> uploadLeftFile(String path) {
        return Tasks.call(mExecutor, () -> {

            /**Searching the file from drive which is already uploaded**/
            String pageToken =null;
            FileList result;
            do{
                result = mDriveService.files().list()
                        .setQ("mimeType ='image/jpeg'")
                        .setSpaces("drive")
                        .setFields("files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                pageToken=result.getNextPageToken();
            } while(pageToken!=null);

            File myFile=null;
            java.io.File folder = new java.io.File(path);
            java.io.File [] files = folder.listFiles();
            if (result.getFiles().size() > 0) {
                List<File> fileList = result.getFiles();
                for(int j=0;j<fileList.size();j++){
                    for(int i=0;i<files.length;i++){
                        if(fileList.get(j).hashCode()!=files[i].hashCode())
                        {

                            File fileMetaData = new File();
                            fileMetaData.setName(files[i].getName());

                            java.io.File file = new java.io.File(path+files[i].getName());

                            FileContent mediaContent = new FileContent("image/jpeg",file);

                            try{
                                myFile=mDriveService.files().create(fileMetaData,mediaContent).execute();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            if(myFile==null){
                                throw new IOException("Null result when creating file creation");
                            }
                        }


                    }
                }


            }


            return myFile.getId();
        });
    }

}

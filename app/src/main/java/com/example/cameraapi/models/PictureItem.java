package com.example.cameraapi.models;
import android.net.Uri;

/**POJO for clicked image**/
public class PictureItem {

    private String name;
    private Uri uri;
    private boolean uploadChecker;
    int id;


    public PictureItem() {
    }

    public PictureItem(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUploadChecker() {
        return uploadChecker;
    }

    public void setUploadChecker(boolean uploadChecker) {
        this.uploadChecker = uploadChecker;
    }

}


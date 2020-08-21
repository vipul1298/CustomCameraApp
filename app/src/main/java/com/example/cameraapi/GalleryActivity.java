package com.example.cameraapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cameraapi.adapters.PictureAdapter;
import com.example.cameraapi.models.PictureItem;
import com.example.cameraapi.utils.DriveServiceHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**Activity used to show pictures and also have functionality to upload the images to google drive**/
public class GalleryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PictureAdapter pictureAdapter;
    DriveServiceHelper driveServiceHelper;
    Drive googleDriveService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setTitle("Gallery");

        recyclerView=findViewById(R.id.recycler);

           pictureAdapter= new PictureAdapter(this,getData()); //adapter initialization
           pictureAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(pictureAdapter);//setting the adapter to recycler view

        requestSignIn();

    }

    //Request google Account
    private void requestSignIn() {

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        startActivityForResult(googleSignInClient.getSignInIntent(),200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 200:
                if(resultCode==RESULT_OK){
                    handleSignInIntent(data);
                }
                break;
        }
    }

    //Setting  the Google Drive Api to store image
    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleAccountCredential credential = GoogleAccountCredential
                                .usingOAuth2(GalleryActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));

                        credential.setSelectedAccount(googleSignInAccount.getAccount());


                        googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("My Custom Camera app")
                                .build();


                        driveServiceHelper=new DriveServiceHelper(googleDriveService);  //DriveServiceHelper class initialization
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    //Getting all the images from external storage to show in a recycler-view
    private List<PictureItem> getData(){
            List<PictureItem> pictureItemList = new ArrayList<>();
            //Target folder
        File folder = new File(Environment.getExternalStorageDirectory().getPath()+"/MyImages/");

        PictureItem pI;

        if(folder.exists()){
            File [] files =folder.listFiles();

            for (int i=0;i<files.length;i++){
                File file = files[i];
                pI=new PictureItem();
                pI.setId(file.hashCode());
                pI.setName(file.getName());
                pI.setUploadChecker(false);
                pI.setUri(Uri.fromFile(file));
                pictureItemList.add(pI);
            }
        }
        return pictureItemList;
    }





//Sync all images to your signedIn google account
    public void Upload(View view) {
        ProgressDialog progressDialog = new ProgressDialog(GalleryActivity.this);
        progressDialog.setTitle("Uploading to google drive");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String path = Environment.getExternalStorageDirectory().getPath()+"/MyImages/";
        driveServiceHelper.createFile(path)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        progressDialog.dismiss();
                        Toast.makeText(GalleryActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(GalleryActivity.this, "Check your google drive api key", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    //pending
    //upload images which is not uploaded to drive yet
    public void UploadLeft(View view) {


    }




}

package com.easysoftware.phototaker.view.main;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.easysoftware.phototaker.R;
import com.easysoftware.phototaker.model.database.Photo;
import com.easysoftware.phototaker.util.Utils;
import com.easysoftware.phototaker.view.photoviewer.PhotoViewerActivity;
import com.easysoftware.phototaker.viewmodel.PhotoViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements PhotoRecyclerViewAdapter.OnItemClickListener {
    private final static int REQUEST_PERMISSIONS = 101;
    private final static int REQUEST_PICTURE_CAPTURE = 202;

    private boolean mCameraPermissionGranted;
    private boolean mWriteExternalStoragePermissionGranted;

    private String mPhotoFilePath;

    private RecyclerView mPhotoRecyclerView;
    private PhotoRecyclerViewAdapter mPhotoRecyclerViewAdapter;
    private PhotoViewModel mPhotoViewModel;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // recyclerview
        mPhotoRecyclerViewAdapter = new PhotoRecyclerViewAdapter(this, mCompositeDisposable,this);
        mPhotoRecyclerView = findViewById(R.id.recyclerView);
        mPhotoRecyclerView.setAdapter(mPhotoRecyclerViewAdapter);
        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // viewmodel
        mPhotoViewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);
        // observe livedata changes
        mPhotoViewModel.getPhotos().observe(this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(@Nullable List<Photo> photos) {
                // update recyclerview
                mPhotoRecyclerViewAdapter.update(photos);
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraPermissionGranted && mWriteExternalStoragePermissionGranted) {
                    takePhoto();
                } else {
                    ArrayList<String> permissions = new ArrayList<>();
                    if (!mCameraPermissionGranted && needRequestPermission(Manifest.permission.CAMERA)) {
                        permissions.add(Manifest.permission.CAMERA);
                    }
                    if (!mWriteExternalStoragePermissionGranted && needRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }

                    if (permissions.size() > 0) {
                        String[] ps = new String[permissions.size()];
                        ps = permissions.toArray(ps);
                        ActivityCompat.requestPermissions(MainActivity.this, ps, REQUEST_PERMISSIONS);
                    } else {
                        takePhoto();
                    }
                }
            }
        });

    }

    private boolean needRequestPermission(String permission) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Marshmallow+
            return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; ++i) {
                if (permissions[i].equals(Manifest.permission.CAMERA) &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mCameraPermissionGranted = true;
                }
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mWriteExternalStoragePermissionGranted = true;
                }
            }

            if (mCameraPermissionGranted && mWriteExternalStoragePermissionGranted) {
                takePhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICTURE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                insertPhoto(mPhotoFilePath);
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    @Override
    public void onItemClick(Photo photo) {
        PhotoViewerActivity.startActivity(this, photo);
    }

    private File createPhotoFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String photoFileName = "PHOTO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(photoFileName, ".jpg", storageDir);

        mPhotoFilePath = photoFile.getAbsolutePath();

        return photoFile;
    }

    private void takePhoto() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pictureIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (pictureIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No photo app installed on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        mCompositeDisposable. add(Utils.createPhotoFile(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        mPhotoFilePath = file.getAbsolutePath();

                        String authority = getPackageName() + ".fileprovider";
                        Uri photoUri = FileProvider.getUriForFile(MainActivity.this, authority, file);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(pictureIntent, REQUEST_PICTURE_CAPTURE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    private void insertPhoto(String photoPath) {
        String name=photoPath.substring(photoPath.lastIndexOf("/")+1);
        Photo photo = new Photo(name, photoPath);
        mCompositeDisposable.add(
                mPhotoViewModel.insertPhoto(photo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }


}

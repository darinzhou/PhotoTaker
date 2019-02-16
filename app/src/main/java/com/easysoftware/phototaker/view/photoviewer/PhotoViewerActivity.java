package com.easysoftware.phototaker.view.photoviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.easysoftware.phototaker.R;
import com.easysoftware.phototaker.model.PhotoRepository;
import com.easysoftware.phototaker.model.database.Photo;
import com.easysoftware.phototaker.model.database.PhotoDatabase;
import com.easysoftware.phototaker.model.database.PhotoRepositoryForDatabase;
import com.easysoftware.phototaker.util.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PhotoViewerActivity extends AppCompatActivity {

    private Photo mPhoto;
    private ImageView mImageView;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private PhotoRepository mPhotoRepository;

    public static void startActivity(Context context, Photo photo) {
        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.putExtra(Photo.PHOTO_ID, photo.getId());
        intent.putExtra(Photo.PHOTO_NAME, photo.getName());
        intent.putExtra(Photo.PHOTO_URL, photo.getUrl());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // repository
        mPhotoRepository = new PhotoRepositoryForDatabase(PhotoDatabase.getInstance(this).photoDao());

        // init photo
        Intent intent = getIntent();
        if (intent != null) {
            mPhoto = new Photo(intent.getIntExtra(Photo.PHOTO_ID, -1),
                    intent.getStringExtra(Photo.PHOTO_NAME), intent.getStringExtra(Photo.PHOTO_URL));
        }

        // UI action
        mImageView = findViewById(R.id.ivPhoto);
        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCompositeDisposable.add(mPhotoRepository.deletePhoto(mPhoto)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                NavUtils.navigateUpFromSameTask(PhotoViewerActivity.this);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
                );
            }
        });

        // disply photo
        if (mPhoto != null) {
            mCompositeDisposable.add(Utils.decodeBitmap(mPhoto.getUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Bitmap>() {
                        @Override
                        public void onNext(Bitmap bitmap) {
                            mImageView.setImageBitmap(bitmap);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

}

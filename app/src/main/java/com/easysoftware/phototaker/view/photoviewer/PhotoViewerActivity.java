package com.easysoftware.phototaker.view.photoviewer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.easysoftware.phototaker.R;
import com.easysoftware.phototaker.model.PhotoRepository;
import com.easysoftware.phototaker.model.database.Photo;
import com.easysoftware.phototaker.model.database.PhotoDatabase;
import com.easysoftware.phototaker.model.database.PhotoRepositoryForDatabase;
import com.easysoftware.phototaker.util.Utils;
import com.easysoftware.phototaker.viewmodel.PhotoViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PhotoViewerActivity extends AppCompatActivity {

    private Photo mPhoto;
    private ViewPager mViewPager;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private PhotoViewModel mPhotoViewModel;
    private int mCurrentItem = -1;

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

        // init photo
        Intent intent = getIntent();
        if (intent != null) {
            mPhoto = new Photo(intent.getIntExtra(Photo.PHOTO_ID, -1),
                    intent.getStringExtra(Photo.PHOTO_NAME), intent.getStringExtra(Photo.PHOTO_URL));
        }

        // viewPager
        mViewPager = findViewById(R.id.viewPager);

        // hook viewmodel
        mPhotoViewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);
        // observe data
        mPhotoViewModel.getPhotos().observe(this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(@Nullable List<Photo> photos) {
                // check photo existance
                if (photos.isEmpty()) {
                    Toast.makeText(PhotoViewerActivity.this, "No photo in database", Toast.LENGTH_LONG);
                    mViewPager.setVisibility(View.GONE);
                    return;
                }

                mViewPager.setVisibility(View.VISIBLE);

                // find current
                if (mCurrentItem == -1) {
                    for (int i=0; i<photos.size(); ++i) {
                        Photo p = photos.get(i);
                        if (p.getId() == mPhoto.getId()) {
                            mCurrentItem = i;
                            break;
                        }
                    }
                }
                // not found, just display the first one
                if (mCurrentItem == -1) {
                    mCurrentItem = 0;
                }

                // update adapter
                PhotoViewerAdapter adapter = new PhotoViewerAdapter(PhotoViewerActivity.this,
                        mCompositeDisposable, mPhotoViewModel, photos);
                mViewPager.setAdapter(adapter);

                // set current item
                mViewPager.setCurrentItem(mCurrentItem);
            }
        });

        // UI action
        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentItem = mViewPager.getCurrentItem();
                mPhoto = ((PhotoViewerAdapter)mViewPager.getAdapter()).getItem(mCurrentItem);
                mCompositeDisposable.add(mPhotoViewModel.deletePhoto(mPhoto)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                mCurrentItem++;

                                // to end, just display the first one
                                if (mCurrentItem >= mViewPager.getAdapter().getCount()) {
                                    mCurrentItem = 0;
                                }

                                // set current item
                                mViewPager.setCurrentItem(mCurrentItem);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
                );
            }
        });
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

    static class PhotoViewerAdapter extends PagerAdapter {

        private Context mContext;
        private List<Photo> mPhotos;

        private CompositeDisposable mCompositeDisposable;
        private PhotoViewModel mPhotoViewModel;

        public PhotoViewerAdapter(Context context, CompositeDisposable compositeDisposable,
                                  PhotoViewModel photoViewModel, List<Photo> photos) {
            mContext = context;
            mCompositeDisposable = compositeDisposable;
            mPhotoViewModel = photoViewModel;
            mPhotos = photos;

        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View)object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            final View view = LayoutInflater.from(mContext).inflate(R.layout.page_photo_viewer, container, false);
            container.addView(view);

            // current photo
            Photo photo = mPhotos.get(position);

            // set photo
            mCompositeDisposable.add(Utils.decodeBitmap(photo.getUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Bitmap>() {
                        @Override
                        public void onNext(Bitmap bitmap) {
                            ((ImageView)view.findViewById(R.id.ivPhoto)).setImageBitmap(bitmap);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    })
            );

            return view;
        }

        public Photo getItem(int position) {
            return mPhotos.get(position);
        }
    }
}

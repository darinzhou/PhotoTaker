package com.easysoftware.phototaker.model.database;

import android.arch.lifecycle.LiveData;

import com.easysoftware.phototaker.model.PhotoRepository;

import java.util.List;

import io.reactivex.Completable;

public class PhotoRepositoryForDatabase implements PhotoRepository {

    private PhotoDao mPhotoDao;

    public PhotoRepositoryForDatabase(PhotoDao photoDao) {
        mPhotoDao = photoDao;
    }

    @Override
    public LiveData<List<Photo>> getPhotos() {
        return mPhotoDao.getPhotos();
    }

    @Override
    public Completable insertPhoto(Photo photo) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                mPhotoDao.insertPhoto(photo);
            }
        });
    }

    @Override
    public Completable deletePhoto(Photo photo) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                mPhotoDao.deletePhoto(photo);
            }
        });
    }
}

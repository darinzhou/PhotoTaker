package com.easysoftware.phototaker.model.remote;

import android.arch.lifecycle.LiveData;

import com.easysoftware.phototaker.model.PhotoRepository;
import com.easysoftware.phototaker.model.database.Photo;

import java.util.List;

import io.reactivex.Completable;

public class PhotoRepositoryForRestfulService implements PhotoRepository {

    private PhotoRestApi photoRestApi;

    public PhotoRepositoryForRestfulService() {
        photoRestApi = PhotoRetrofit.getInstance(PhotoRestApi.PHOTO_SERVICE_URL)
                .create(PhotoRestApi.class);
    }

    @Override
    public LiveData<List<Photo>> getPhotos() {
        photoRestApi.getPhotos("test");
        return null;
    }

    @Override
    public Completable insertPhoto(Photo photo) {
        return null;
    }

    @Override
    public Completable deletePhoto(Photo photo) {
        return null;
    }
}

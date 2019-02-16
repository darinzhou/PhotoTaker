package com.easysoftware.phototaker.model;

import android.arch.lifecycle.LiveData;

import com.easysoftware.phototaker.model.database.Photo;

import java.util.List;

import io.reactivex.Completable;

public interface PhotoRepository {
    LiveData<List<Photo>> getPhotos();
    Completable insertPhoto(Photo photo);
    Completable deletePhoto(Photo photo);
}

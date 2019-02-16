package com.easysoftware.phototaker.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.easysoftware.phototaker.model.database.Photo;
import com.easysoftware.phototaker.model.database.PhotoDatabase;
import com.easysoftware.phototaker.model.database.PhotoRepositoryForDatabase;
import com.easysoftware.phototaker.model.PhotoRepository;

import java.util.List;

import io.reactivex.Completable;

public class PhotoViewModel extends AndroidViewModel {
    private PhotoRepository mPhotoRepository;
    private LiveData<List<Photo>> mPhotos;

    public PhotoViewModel(@NonNull Application application) {
        super(application);

        mPhotoRepository = new PhotoRepositoryForDatabase(PhotoDatabase.getInstance(application).photoDao());
        mPhotos = mPhotoRepository.getPhotos();
    }

    public LiveData<List<Photo>> getPhotos() {
        return mPhotos;
    }

    public Completable insertPhoto(Photo photo) {
        return mPhotoRepository.insertPhoto(photo);
    }

    public Completable deletePhoto(Photo photo) {
        return mPhotoRepository.deletePhoto(photo);
    }
}

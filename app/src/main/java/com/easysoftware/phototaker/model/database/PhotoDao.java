package com.easysoftware.phototaker.model.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.easysoftware.phototaker.model.database.Photo;

import java.util.List;

@Dao
public interface PhotoDao {
    @Query("select * from photo_table")
    LiveData<List<Photo>> getPhotos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPhoto(Photo photo);

    @Delete
    void deletePhoto(Photo photo);
}

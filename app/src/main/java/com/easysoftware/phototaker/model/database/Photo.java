package com.easysoftware.phototaker.model.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "photo_table")
public class Photo {
    @Ignore
    public static final String PHOTO_ID = "PHOTO_ID";
    @Ignore
    public static final String PHOTO_NAME = "PHOTO_NAME";
    @Ignore
    public static final String PHOTO_URL = "PHOTO_URL";

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "url")
    private String mUrl;

    public Photo() {

    }

    @Ignore
    public Photo(String name, String url) {
        mName = name;
        mUrl = url;
    }

    @Ignore
    public Photo(int id, String name, String url) {
        mId = id;
        mName = name;
        mUrl = url;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

}

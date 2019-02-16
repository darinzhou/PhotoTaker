package com.easysoftware.phototaker.model.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Photo.class}, version = 1)
public abstract class PhotoDatabase extends RoomDatabase {
    private final static String DB_NAME = "photos.db";
    private static volatile PhotoDatabase sInstance;
    private static Object mutex = new Object();

    public abstract PhotoDao photoDao();

    public static PhotoDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (mutex) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), PhotoDatabase.class, DB_NAME)
                            .build();
                }
            }
        }

        return sInstance;
    }

}

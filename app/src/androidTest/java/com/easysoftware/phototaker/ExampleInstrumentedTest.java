package com.easysoftware.phototaker;

import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.easysoftware.phototaker.model.repository.database.Photo;
import com.easysoftware.phototaker.model.repository.database.PhotoDatabase;
import com.easysoftware.phototaker.model.repository.database.PhotoRepositoryForDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.easysoftware.phototaker", appContext.getPackageName());
    }

    @Test
    public void testPhotoDatabase() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        PhotoDatabase db = Room.inMemoryDatabaseBuilder(appContext, PhotoDatabase.class)
                .allowMainThreadQueries()
                .build();
        PhotoRepositoryForDatabase repos = new PhotoRepositoryForDatabase(db.photoDao());

        final Photo p1 = new Photo();
        p1.setName("111");
        p1.setUrl("data\\image\\111.png");

        final Photo p2 = new Photo();
        p2.setName("222");
        p2.setUrl("data\\image\\222.png");

        final Photo p3 = new Photo();
        p3.setName("333");
        p3.setUrl("data\\image\\111.png");

        final Photo p4 = new Photo();
        p4.setName("444");
        p4.setUrl("data\\image\\222.png");

        repos.getPhotos().observeForever(new Observer<List<Photo>>() {
            @Override
            public void onChanged(@Nullable List<Photo> photos) {

                if (photos == null) {
                } else if (photos.size() >= 1) {
                }

            }
        });

        repos.insertPhoto(p1).test().onComplete();
        repos.insertPhoto(p2).test().onComplete();
        repos.insertPhoto(p3).test().onComplete();
        repos.insertPhoto(p4).test().onComplete();

        repos.deletePhoto(p1).test().onComplete();
        repos.deletePhoto(p2).test().onComplete();
        repos.deletePhoto(p3).test().onComplete();
        repos.deletePhoto(p4).test().onComplete();

    }

}

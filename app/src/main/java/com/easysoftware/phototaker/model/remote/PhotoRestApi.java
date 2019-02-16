package com.easysoftware.phototaker.model.remote;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PhotoRestApi {
    String PHOTO_SERVICE_URL = "";

    @GET("album/{album}/photos")
    Observable<List<Photo>> getPhotos(@Path("album") String albumName);

    @POST("album/{album}")
    Observable<PhotoResponse> insertPhoto(@Path("album") String albumName, @Body PhotoRequest request);

    @POST("album/{album}/{id}")
    Observable<PhotoResponse> deletePhoto(@Path("album") String albumName, @Path("id") String photoId, @Body PhotoRequest request);

}

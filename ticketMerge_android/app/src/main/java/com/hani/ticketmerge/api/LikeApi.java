package com.hani.ticketmerge.api;

import com.hani.ticketmerge.model.ArtistLike;
import com.hani.ticketmerge.model.ConcertLike;
import com.hani.ticketmerge.model.ConcertRes;
import com.hani.ticketmerge.model.LikeRes;
import com.hani.ticketmerge.model.ArtistLikeRes;
import com.hani.ticketmerge.model.MyLike;
import com.hani.ticketmerge.model.Res;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LikeApi {

    // 좋아요 하는 API
    @POST("/like/{combined_id}")
    Call<Res> like(@Path("combined_id") int combinedId,
                   @Query("type") int type);

    // 좋아요 취소 API
    @DELETE("/like/{combined_id}")
    Call<Res> likeCancle (@Path("combined_id") int combinedId,
                          @Query("type") int type);

    // 좋아하는 장르 확인 API
    @GET("/mylike/genre")
    Call<LikeRes> likeGenre (@Query("offset") int offset,
                             @Query("limit") int limit);

    // 아티스트 전체목록 조회 API
    @GET("/artist")
    Call<ArtistLikeRes> artistListAll (@Query("offset") int offset,
                                      @Query("limit") int limit);

    // 내가 좋아요 한 아티스트 목록 조회 API
    @GET("/mylike/artist")
    Call<LikeRes> getLikeArtist (@Query("offset") int offset,
                                 @Query("limit") int limit);


    // 콘서트 전체목록 (가나다순) API
    @GET("concert/title")
    Call<ConcertLike> getAllConcert (@Query("offset") int offset,
                                     @Query("limit") int limit);

    // 내가 좋아요한 콘서트 목록 조회 API
    @GET("/mylike/concert")
    Call<ConcertLike> getLikeConcert (@Query("offset") int offset,
                                      @Query("limit") int limit);

    // 내 좋아요 목록 조회 API
    @GET("/mylike")
    Call<MyLike> getAllLike();

    @GET("/mylike/search")
    Call<ConcertRes> getLikeConcertArtistList (@Query("offset") int offset,
                                               @Query("limit") int limit,
                                              @Query("query") String keyword);

}

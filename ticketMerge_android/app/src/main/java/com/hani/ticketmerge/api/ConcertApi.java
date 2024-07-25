package com.hani.ticketmerge.api;

import com.hani.ticketmerge.model.ConcertLike;
import com.hani.ticketmerge.model.ConcertRes;
import com.hani.ticketmerge.model.ConcertDetailRes;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConcertApi {


    //  콘서트 보기 API
    @GET("/concert")
    Call<ConcertRes> getConcertList(@Query("offset") int offset,
                                    @Query("limit") int limit);

//    // 콘서트 조회순 보기 API 구버전
//    @GET("/concert/sort")
//    Call<ConcertRes> getConcertListView(@Query("offset") int offset,
//                                        @Query("limit") int limit);

    @GET("/concert/main")
    Call<ConcertRes> getConcertListView(@Query("offset") int offset,
                                        @Query("limit") int limit,
                                        @Query("type") int type,
                                        @Query("genre") int genre,
                                        @Query("place") int place,
                                        @Query("sort") int sort);
    @GET("/concert/search")
    Call<ConcertRes> getTextSearchList(@Query("offset") int offset,
                                       @Query("limit") int limit,
                                       @Query("query") String query);

    @Multipart
    @POST("/concert/image/search")
    Call<ConcertRes> getImageSearchList(
            @Part MultipartBody.Part photo);

    // 콘서트 상세보기
    @GET("/concert/information/{concertId}")
    Call<ConcertDetailRes> getConcertDetailView(@Path("concertId") int concertId);

    // 콘서트 및 아티스트 검색 API
    @GET("/concert/search")
    Call<ConcertRes> getConcertArtistList (@Query("offset") int offset,
                                           @Query("limit") int limit,
                                           @Query("query") String keyword);




}

package com.hani.ticketmerge.api;

import com.hani.ticketmerge.model.CommentRequest;
import com.hani.ticketmerge.model.PostRes;
import com.hani.ticketmerge.model.Res;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostApi {
    // 전체 게시글 보기 API, 게시글 검색 API
    @GET("/post")
    Call<PostRes> getPostList (@Query("type") int type,
                               @Query("offset") int offset,
                               @Query("limit") int limit
    ); //@Query("keyword") String keyword


    // 새로운 게시글 추가 API
    @Multipart
    @POST("/post/create")
    Call<PostRes> addPostList(@Header("Authorization") String token,
                              @Query("types") int types,
                              @Part("title") RequestBody title,
                              @Part("content") RequestBody content,
                              @Part MultipartBody.Part image);
    @GET("/post/search")
    Call<PostRes> getPostSearch(@Query("offset") int offset,
                                @Query("limit") int limit,
                                @Query("query") String query);

    @GET("/post/information/{postId}")
    Call<PostRes> getPostDetail(@Path("postId") int postId);


    @POST("/post/comment/{postId}")
    Call<Res> addComment(@Path("postId") int postId, @Body CommentRequest CommentRequest);


    @DELETE("/post/delete/{postId}")
    Call<Res> deletePost(@Path("postId") int postId);



}

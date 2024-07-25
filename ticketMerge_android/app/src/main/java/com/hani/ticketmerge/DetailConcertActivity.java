package com.hani.ticketmerge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hani.ticketmerge.adapter.CastingListAdapter;
import com.hani.ticketmerge.api.ConcertApi;
import com.hani.ticketmerge.api.LikeApi;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.config.Config;
import com.hani.ticketmerge.model.Artist;
import com.hani.ticketmerge.model.Concert;
import com.hani.ticketmerge.model.ConcertDetailRes;
import com.hani.ticketmerge.model.Res;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailConcertActivity extends AppCompatActivity {

    private ImageView imgViewThumbnail;
    private TextView txtTitle, txtDate, txtPlace, txtLink, txtLink2;
    private RecyclerView recyclerViewCastingList;
    private ImageView[] imgDetails;
    private  ImageView imgFavoriteConcert;
    private  Retrofit retrofit;
    private ConcertApi api;
    private Concert concert;
    private int concertId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_concert);

        retrofit = NetworkClient.getRetrofitClient(this);

        imgViewThumbnail = findViewById(R.id.imgViewThumbnail);
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        txtPlace = findViewById(R.id.txtPlace);
        txtLink = findViewById(R.id.txtLink);
        txtLink2 = findViewById(R.id.txtLink2);
        imgFavoriteConcert = findViewById(R.id.imgFavoriteConcert);
        recyclerViewCastingList = findViewById(R.id.recyclerViewCastingList);

        imgDetails = new ImageView[]{
                findViewById(R.id.imgDetail1), findViewById(R.id.imgDetail2), findViewById(R.id.imgDetail3),
                findViewById(R.id.imgDetail4), findViewById(R.id.imgDetail5), findViewById(R.id.imgDetail6),
                findViewById(R.id.imgDetail7), findViewById(R.id.imgDetail8), findViewById(R.id.imgDetail9),
                findViewById(R.id.imgDetail10), findViewById(R.id.imgDetail11), findViewById(R.id.imgDetail12),
                findViewById(R.id.imgDetail13), findViewById(R.id.imgDetail14), findViewById(R.id.imgDetail15),
                findViewById(R.id.imgDetail16)
        };

        api = retrofit.create(ConcertApi.class);


        concertId = getIntent().getIntExtra("ConcertId", 0);

        Log.i("DETAILMAIN", String.valueOf(concertId));

        if (concertId != 0) {
            getConcertDetail(concertId);
        }


        imgFavoriteConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (concert == null) {
                    Toast.makeText(DetailConcertActivity.this,
                            "콘서트 정보를 가져오는 중입니다. 잠시 후 다시 시도해 주세요.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sp = getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
                boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
                int combinedId = concertId;
                int type = 1;


                String token= sp.getString("token","");
                Log.i("MAIN TOKEN", "token: " + token );
                Log.i("MAIN LOGGEDIN", "LoggedInd: " + isLoggedIn);

                if (!isLoggedIn) {
                    // 로그인하지 않은 경우 로그인 액티비티로 이동
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailConcertActivity.this);
                    builder.create();
                    builder.setMessage("로그인하시겠습니까?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(DetailConcertActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 그냥 반환
                                    return;
                                }
                            });
                    builder.show();
                }

                if (concert.getLike() == 1) {
                    // 좋아요 취소 API 호출
                    LikeApi likeApi = retrofit.create(LikeApi.class);
                    Call<Res> call = likeApi.likeCancle(combinedId, type);
                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()) {
                                // 좋아요 상태 업데이트
                                concert.setLike(0);
                                imgFavoriteConcert.setImageResource(R.drawable.baseline_favorite_border_24);

//                                // 데이터 저장 로직 (필요 시 구현)
//                                SharedPreferences sp = getSharedPreferences(Config.SP_NAME,MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putInt("concertLikes", combinedId);
//                                editor.apply();

                            } else {
                                // 응답이 실패한 경우 처리
                                Toast.makeText(DetailConcertActivity.this,
                                        "좋아요 취소 실패",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {
                            // 호출 실패 처리
                            Toast.makeText(DetailConcertActivity.this,
                                    "네트워크 오류",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // 좋아요 API 호출
                    LikeApi likeApi = retrofit.create(LikeApi.class);
                    Call<Res> call = likeApi.like(combinedId, type);
                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()) {
                                // 좋아요 상태 업데이트
                                concert.setLike(1);
                                imgFavoriteConcert.setImageResource(R.drawable.baseline_favorite_24);
//                                // 데이터 저장 로직 (필요 시 구현)
//                                SharedPreferences sp = getSharedPreferences(Config.SP_NAME,MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putInt("concertLikes", combinedId);
//                                editor.apply();
                            } else if (response.code() == 401) {
                                // 401 Unauthorized 에러 처리
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailConcertActivity.this);
                                builder.setMessage("로그인하시겠습니까?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // 로그인 액티비티로 이동
                                                Intent intent = new Intent(DetailConcertActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // 그냥 반환
                                                return;
                                            }
                                        });
                                builder.create().show();
                            } else if (response.code() == 422) {
                                // 422 에러 처리
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailConcertActivity.this);
                                builder.setMessage("로그인하시겠습니까?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // 로그인 액티비티로 이동
                                                Intent intent = new Intent(DetailConcertActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // 그냥 반환
                                                return;
                                            }
                                        });
                                builder.create().show();
                            }else {
                                // 응답이 실패한 경우 처리
                                Toast.makeText(DetailConcertActivity.this,
                                        "좋아요 실패",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {
                            // 호출 실패 처리
                            Toast.makeText(DetailConcertActivity.this,
                                    "네트워크 오류",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void getConcertDetail(int concertId) {
        Call<ConcertDetailRes> call = api.getConcertDetailView(concertId);
        call.enqueue(new Callback<ConcertDetailRes>() {
            @Override
            public void onResponse(Call<ConcertDetailRes> call, Response<ConcertDetailRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ConcertDetailRes concertDetail = response.body();
//                    if (concertDetail != null && "success".equals(concertDetail.getResult())) {
//                        List<Concert> concertList = concertDetail.getConcertList();
//                        if (concertList != null && !concertList.isEmpty()) {
//                            updateUI(concertList.get(0), concertDetail.getArtistList());
//                        }
//                    }
                    if (concertDetail != null && "success".equals(concertDetail.getResult())) {
                        concert = concertDetail.getConcert(); // concert 객체 초기화
                        updateUI(concert, concertDetail.getArtistList()); // UI 업데이트
                    }
                }
            }

            @Override
            public void onFailure(Call<ConcertDetailRes> call, Throwable t) {
                Log.e("DetailConcertActivity", "Error fetching concert details", t);
            }
        });
    }

    private void updateUI(Concert concert, List<Artist> artist) {
        Glide.with(this)
                .load(concert.getThumbnailUrl())
                .into(imgViewThumbnail);
        txtTitle.setText(concert.getTitle());
        txtDate.setText(concert.getStartDate() + " - " + concert.getEndDate());
        txtPlace.setText(concert.getPlace());
        txtLink.setText(concert.getUrl());
        txtLink2.setText(concert.getUrl());

        if (concert.getLike() == 1) {
            imgFavoriteConcert.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            imgFavoriteConcert.setImageResource(R.drawable.baseline_favorite_border_24);
        }

        // 상세 이미지 설정
        String[] contentUrls = concert.getContentUrl().split(",");
        for (int i = 0; i < contentUrls.length && i < imgDetails.length; i++) {
            Glide.with(this)
                    .load(contentUrls[i].trim()) // trim() 메서드를 사용하여 공백을 제거
                    .placeholder(R.drawable.placeholder) // 이미지 로드 전 표시할 플레이스홀더 이미지
                    .error(R.drawable.error) // 이미지 로드 실패 시 표시할 에러 이미지
                    .override(Target.SIZE_ORIGINAL)
                    .into(imgDetails[i]);
            imgDetails[i].setVisibility(View.VISIBLE); // ImageView를 보이도록 설정
        }
// 이미지 없을 때 숨기기
        for (int i = contentUrls.length; i < imgDetails.length; i++) {
            imgDetails[i].setVisibility(View.GONE); // 남은 ImageView는 숨기기
        }

        // 캐스팅 리스트 설정
        if (artist != null && !artist.isEmpty()) {
            CastingListAdapter adapter = new CastingListAdapter(artist, retrofit);
            recyclerViewCastingList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewCastingList.setAdapter(adapter);
        } else {
            recyclerViewCastingList.setVisibility(RecyclerView.GONE);
        }

    }

    protected void onResume() {
        super.onResume();
        Log.i("RESUME MAIN",  String.valueOf(concertId));
        getConcertDetail(concertId);

    }

}

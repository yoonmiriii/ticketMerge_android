package com.hani.ticketmerge;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hani.ticketmerge.api.LikeApi;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.model.GenreLike;
import com.hani.ticketmerge.model.LikeRes;
import com.hani.ticketmerge.model.Res;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FgenreActivity extends AppCompatActivity {

    Button btnRock, btnBallade, btnDance, btnInKorea, btnHiphop, btnTrot, btnEtc;
    private LikeApi likeApi;
    private List<String> likedGenres = new ArrayList<>();

    private Button[] genreButtons;
    private String[] genreNames = {"락", "발라드", "댄스", "내한", "힙합", "트로트", "그 외"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgenre);

        // 버튼 초기화
        btnRock = findViewById(R.id.btnRock);
        btnBallade = findViewById(R.id.btnBallade);
        btnDance = findViewById(R.id.btnDance);
        btnInKorea = findViewById(R.id.btnInKorea);
        btnHiphop = findViewById(R.id.btnHiphop);
        btnTrot = findViewById(R.id.btnTrot);
        btnEtc = findViewById(R.id.btnEtc);

        // Retrofit 초기화
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        likeApi = retrofit.create(LikeApi.class);

        // 좋아요 장르 가져오기
        fetchLikedGenres();

        // 버튼 클릭 리스너 설정
        btnBallade.setOnClickListener(v -> toggleLike("발라드", 1, btnBallade));
        btnDance.setOnClickListener(v -> toggleLike("댄스", 2, btnDance));
        btnHiphop.setOnClickListener(v -> toggleLike("힙합", 3, btnHiphop));
        btnRock.setOnClickListener(v -> toggleLike("락", 4, btnRock));
        btnTrot.setOnClickListener(v -> toggleLike("트로트", 5, btnTrot));
        btnInKorea.setOnClickListener(v -> toggleLike("내한", 6, btnInKorea));
        btnEtc.setOnClickListener(v -> toggleLike("그 외", 7, btnEtc));
    }

    private void fetchLikedGenres() {
        likeApi.likeGenre(0, 10).enqueue(new Callback<LikeRes>() {
            @Override
            public void onResponse(Call<LikeRes> call, Response<LikeRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LikeRes likeRes = response.body();
                    if ("success".equals(likeRes.getResult())) {
                        likedGenres.clear();
                        for (GenreLike genreLike : likeRes.getGenreLike()) {
                            likedGenres.add(genreLike.getGenre());
                        }
                        updateButtonColors();
                    } else {
                        showToast("장르를 가져오는데 실패했습니다");
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeRes> call, Throwable t) {
                showToast("장르를 가져오는데 실패했습니다");
            }
        });
    }

    private void toggleLike(String genreName, int combinedId, Button button) {
        if (likedGenres.contains(genreName)) {
            // 장르 좋아요 취소
            likeApi.likeCancle(combinedId, 3).enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    if (response.isSuccessful()) {
                        likedGenres.remove(genreName);
                        updateButtonColors();
                    } else {
                        showToast("좋아요 취소 실패");
                    }
                }

                @Override
                public void onFailure(Call<Res> call, Throwable t) {
                    showToast("좋아요 취소 실패");
                }
            });
        } else {
            // 장르 좋아요
            likeApi.like(combinedId, 3).enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    if (response.isSuccessful()) {
                        likedGenres.add(genreName);
                        updateButtonColors();
                    } else {
                        showToast("좋아요 실패");
                    }
                }

                @Override
                public void onFailure(Call<Res> call, Throwable t) {
                    showToast("좋아요 실패");
                }
            });
        }
    }

    private void updateButtonColors() {
        setButtonColor(btnRock, "락");
        setButtonColor(btnBallade, "발라드");
        setButtonColor(btnDance, "댄스");
        setButtonColor(btnInKorea, "내한");
        setButtonColor(btnHiphop, "힙합");
        setButtonColor(btnTrot, "트로트");
        setButtonColor(btnEtc, "그 외");
    }

    private void setButtonColor(Button button, String genreName) {
        if (likedGenres.contains(genreName)) {
            button.setBackgroundColor(Color.parseColor("#7fffd4"));
        } else {
            button.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }
    }

    private void showToast(String message) {
        Toast.makeText(FgenreActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
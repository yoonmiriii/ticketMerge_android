package com.hani.ticketmerge;

import static com.hani.ticketmerge.api.LikeApi.*;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hani.ticketmerge.adapter.ArtistAdapter;
import com.hani.ticketmerge.api.LikeApi;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.model.ArtistLike;
import com.hani.ticketmerge.model.ArtistLikeRes;
import com.hani.ticketmerge.model.ConcertRes;
import com.hani.ticketmerge.model.LikeRes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FartistListActivity extends AppCompatActivity {

    TextView txtView;
    EditText txtSearch;
    ImageButton btnSearch;
    RecyclerView recyclerView;

    ArtistAdapter artistAdapter;
    private List<ArtistLike> artistList = new ArrayList<>();

    LikeApi likeApi;

    private int currentPage = 0;
    private int pageSize = 20;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fartist);

        txtSearch = findViewById(R.id.txtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        txtView = findViewById(R.id.txtView);
        txtView.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        artistAdapter = new ArtistAdapter(FartistListActivity.this, new ArrayList<>());

        recyclerView.setAdapter(artistAdapter);

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        likeApi = retrofit.create(LikeApi.class);

        fetchArtistList(currentPage, pageSize);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = txtSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    searchConcerts(keyword);
                } else {
                    Toast.makeText(FartistListActivity.this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 검색창에서 엔터 == 검색버튼
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    btnSearch.performClick();
                    return true;
                }
                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    if (!isLoading && lastVisibleItemPosition >= totalItemCount - 2 && totalItemCount >= pageSize) {
                        currentPage = currentPage + 10;
                        fetchArtistList(currentPage, pageSize);
                    }
                }
            }
        });
    }

    private void fetchArtistList(int page, int limit) {
        isLoading = true;
        Call<LikeRes> call = likeApi.getLikeArtist(page, limit);
        call.enqueue(new Callback<LikeRes>() {
            @Override
            public void onResponse(Call<LikeRes> call, Response<LikeRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArtistLike> newArtists = response.body().getArtistLike();
                    if (newArtists != null) {
                        if (currentPage == 0) {
                            artistAdapter.setData(newArtists); // 초기 데이터 세팅
                        } else {
                            artistAdapter.addData(newArtists); // 추가 데이터 세팅
                        }
                    }
                } else {
                    Toast.makeText(FartistListActivity.this,
                            "데이터를 가져오는데 실패",
                            Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<LikeRes> call, Throwable t) {
                Toast.makeText(FartistListActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.i("ARTIST LIST", t.getMessage());
                isLoading = false;
            }
        });
    }
    private void searchConcerts(String keyword) {
        isLoading = true;

        Call<ConcertRes> call = likeApi.getLikeConcertArtistList(0, pageSize, keyword);
        call.enqueue(new Callback<ConcertRes>() {
            @Override
            public void onResponse(Call<ConcertRes> call, Response<ConcertRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body().getResult();
                    List<ArtistLike> artist = response.body().getArtist();
                    if ("success".equals(result)) {
                        if (artist != null && !artist.isEmpty()) {
                            artistAdapter.setData(artist); // 기존 데이터를 대체
                            recyclerView.setVisibility(View.VISIBLE);
                            txtView.setVisibility(View.GONE);
                        } else {
                            // 콘서트 데이터와 아티스트 모두 없는 경우
                            artistAdapter.setData(new ArrayList<>());
                            recyclerView.setVisibility(View.GONE);
                            txtView.setVisibility(View.VISIBLE);

                        }
                    } else {
                        Toast.makeText(FartistListActivity.this, "검색 결과를 가져오는데 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FartistListActivity.this, "검색 결과를 가져오는데 실패", Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<ConcertRes> call, Throwable t) {
                Toast.makeText(FartistListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("CONCERT SEARCH", t.getMessage());
                isLoading = false;
            }
        });
    }
}
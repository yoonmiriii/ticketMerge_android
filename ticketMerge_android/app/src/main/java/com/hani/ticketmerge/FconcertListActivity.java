package com.hani.ticketmerge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
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

import com.hani.ticketmerge.adapter.ConcertAdapter;
import com.hani.ticketmerge.api.ConcertApi;
import com.hani.ticketmerge.api.LikeApi;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.model.Concert;
import com.hani.ticketmerge.model.ConcertLike;
import com.hani.ticketmerge.model.ConcertRes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FconcertListActivity extends AppCompatActivity {

    TextView txtView;
    EditText txtSearch;
    ImageButton btnSearch;
    RecyclerView recyclerView;

    ConcertAdapter concertAdapter;

    private List<Concert> concertList = new ArrayList<>();

    LikeApi likeApi;
    ConcertApi concertApi;


    private int currentPage = 0;
    private int pageSize = 20;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fconcert);

        txtSearch = findViewById(R.id.txtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        txtView = findViewById(R.id.txtView);
        txtView.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        concertAdapter = new ConcertAdapter(FconcertListActivity.this, concertList);
        recyclerView.setAdapter(concertAdapter);

        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        likeApi = retrofit.create(LikeApi.class);
        concertApi = retrofit.create(ConcertApi.class);

        fetchConcertList(currentPage, pageSize);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = txtSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    searchConcerts(keyword);
                } else {
                    Toast.makeText(FconcertListActivity.this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                        currentPage = currentPage + 20;
                        fetchConcertList(currentPage, pageSize);
                    }
                }
            }
        });

    }

    private void fetchConcertList(int page, int limit) {
        isLoading = true;
        Call<ConcertLike> call = likeApi.getLikeConcert(page, limit);
        call.enqueue(new Callback<ConcertLike>() {
            @Override
            public void onResponse(Call<ConcertLike> call, Response<ConcertLike> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Concert> newConcerts = response.body().getItems();
                    if (newConcerts != null) {
                        if (currentPage == 0) {
                            concertAdapter.setData(newConcerts);
                        } else {
                            concertAdapter.addData(newConcerts);
                        }
                    }
                } else {
                    Toast.makeText(FconcertListActivity.this,
                            "데이터를 가져오는데 실패",
                            Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<ConcertLike> call, Throwable t) {
                Toast.makeText(FconcertListActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.i("CONCERT LIST", t.getMessage());
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
                    List<Concert> searchResults = response.body().getItems();
                    if ("success".equals(result)) {
                        if (searchResults != null && !searchResults.isEmpty()) {
                            concertAdapter.setData(searchResults); // 기존 데이터를 대체
                            recyclerView.setVisibility(View.VISIBLE);
                            txtView.setVisibility(View.GONE);
                        } else {
                            // 콘서트 데이터와 아티스트 모두 없는 경우
                            concertAdapter.setData(new ArrayList<>());
                            recyclerView.setVisibility(View.GONE);
                            txtView.setVisibility(View.VISIBLE);

                        }
                    } else {
                        Toast.makeText(FconcertListActivity.this, "검색 결과를 가져오는데 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FconcertListActivity.this, "검색 결과를 가져오는데 실패", Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<ConcertRes> call, Throwable t) {
                Toast.makeText(FconcertListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("CONCERT SEARCH", t.getMessage());
                isLoading = false;
            }
        });
    }
}

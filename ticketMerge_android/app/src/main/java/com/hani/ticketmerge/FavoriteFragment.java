package com.hani.ticketmerge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import com.hani.ticketmerge.api.LikeApi;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.config.Config;

import com.hani.ticketmerge.model.Artist;
import com.hani.ticketmerge.model.Concert;
import com.hani.ticketmerge.model.MyLike;
import com.hani.ticketmerge.model.GenreLike;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private TextView txtName1,txtName2,txtName3,txtName4;
    private TextView txtGenre1,txtGenre2,txtGenre3,txtGenre4,txtGenre5,txtGenre6,txtGenre7;
    private TextView concertName1,concertName2,concertName3,concertName4,concertName5,concertName6,concertName7,concertName8;

    private ImageView imgArtist1,imgArtist2,imgArtist3,imgArtist4;
    private LinearLayout artistArea;

    Button btnGenre, btnArtist, btnConcert, btnGenreList, btnArtistList, btnConcertList;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        artistArea = view.findViewById(R.id.artistArea);
        imgArtist1 = view.findViewById(R.id.imgArtist1);
        imgArtist2 = view.findViewById(R.id.imgArtist2);
        imgArtist3 = view.findViewById(R.id.imgArtist3);
        imgArtist4 = view.findViewById(R.id.imgArtist4);
        txtName1 = view.findViewById(R.id.txtName1);
        txtName2 = view.findViewById(R.id.txtName2);
        txtName3 = view.findViewById(R.id.txtName3);
        txtName4 = view.findViewById(R.id.txtName4);



        btnGenre = view.findViewById(R.id.btnGenre);
        btnGenreList = view.findViewById(R.id.btnGenreList);
        btnArtist = view.findViewById(R.id.btnArtist);
        btnArtistList = view.findViewById(R.id.btnArtistList);
        btnConcert = view.findViewById(R.id.btnConcert);
        btnConcertList = view.findViewById(R.id.btnConcertList);


        btnGenre.setOnClickListener(v -> {
            SharedPreferences sp = getContext().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

            String token= sp.getString("token","");
            Log.i("MAIN TOKEN", "token: " + token );
            Log.i("MAIN LOGGEDIN", "LoggedInd: " + isLoggedIn);

            if (!isLoggedIn) {
                // 로그인하지 않은 경우 로그인 액티비티로 이동
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("로그인하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 그냥 반환
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // 로그인된 경우 FgenreActivity로 이동
                Intent intent = new Intent(getContext(), FgenreActivity.class);
                startActivity(intent);
            }

        });
        btnGenreList.setOnClickListener(v -> {
            SharedPreferences sp = getContext().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

            String token= sp.getString("token","");
            Log.i("MAIN TOKEN", "token: " + token );
            Log.i("MAIN LOGGEDIN", "LoggedInd: " + isLoggedIn);

            if (!isLoggedIn) {
                // 로그인하지 않은 경우 로그인 액티비티로 이동
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("로그인하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 그냥 반환
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // 로그인된 경우 FgenreActivity로 이동
                Intent intent = new Intent(getContext(), FgenreActivity.class);
                startActivity(intent);
            }

        });

        btnArtist.setOnClickListener(v -> {
            SharedPreferences sp = getContext().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

            String token= sp.getString("token","");
            Log.i("MAIN TOKEN", "token: " + token );
            Log.i("MAIN LOGGEDIN", "LoggedInd: " + isLoggedIn);

            if (!isLoggedIn) {
                // 로그인하지 않은 경우 로그인 액티비티로 이동
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("로그인하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 그냥 반환
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // 로그인된 경우 FartistActivity로 이동
                Intent intent = new Intent(getContext(), FartistActivity.class);
                startActivity(intent);
            }
        });

        btnArtistList.setOnClickListener(v -> {
            SharedPreferences sp = getContext().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

            String token= sp.getString("token","");
            Log.i("MAIN TOKEN", "token: " + token );
            Log.i("MAIN LOGGEDIN", "LoggedInd: " + isLoggedIn);

            if (!isLoggedIn) {
                // 로그인하지 않은 경우 로그인 액티비티로 이동
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("로그인하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 그냥 반환
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {

                Intent intent = new Intent(getContext(), FartistListActivity.class);
                startActivity(intent);
            }
        });

        btnConcert.setOnClickListener(v -> {
            SharedPreferences sp = getContext().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

            String token= sp.getString("token","");
            Log.i("MAIN TOKEN", "token: " + token );
            Log.i("MAIN LOGGEDIN", "LoggedInd: " + isLoggedIn);

            if (!isLoggedIn) {
                // 로그인하지 않은 경우 로그인 액티비티로 이동
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("로그인하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 그냥 반환
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // 로그인된 경우 FconcertActivity로 이동
                Intent intent = new Intent(getContext(), FconcertActivity.class);
                startActivity(intent);
            }
        });

        btnConcertList.setOnClickListener(v -> {
            SharedPreferences sp = getContext().getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

            String token= sp.getString("token","");
            Log.i("MAIN TOKEN", "token: " + token );
            Log.i("MAIN LOGGEDIN", "LoggedInd: " + isLoggedIn);

            if (!isLoggedIn) {
                // 로그인하지 않은 경우 로그인 액티비티로 이동
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("로그인하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 그냥 반환
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // 로그인된 경우 FconcertActivity로 이동
                Intent intent = new Intent(getContext(), FconcertListActivity.class);
                startActivity(intent);
            }
        });


        // TextView 및 ImageView 초기화
        initializeViews(view);

        // UI 요소의 초기 상태를 GONE으로 설정
        setInitialVisibility();

        // API 호출
        fetchUserLikes();

        return view;
    }

    private void fetchUserLikes() {
        LikeApi likeApi = NetworkClient.getRetrofitClient(getContext()).create(LikeApi.class);
        Call<MyLike> call = likeApi.getAllLike();

        call.enqueue(new Callback<MyLike>() {
            @Override
            public void onResponse(Call<MyLike> call, Response<MyLike> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyLike myLike = response.body();

                    setInitialVisibility();
                    // 장르 업데이트
                    updateGenres(myLike.getGenreLike());

                    // 아티스트 업데이트
                    updateArtists(myLike.getArtistLike());

                    // 콘서트 업데이트
                    updateConcerts(myLike.getConcertLike());
                } else {
                    Log.e("FavoriteFragment", "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<MyLike> call, Throwable t) {
                Log.e("FavoriteFragment", "Network error: " + t.getMessage());
            }
        });
    }



    private void setInitialVisibility() {
        txtGenre1.setVisibility(View.GONE);
        txtGenre2.setVisibility(View.GONE);
        txtGenre3.setVisibility(View.GONE);
        txtGenre4.setVisibility(View.GONE);
        txtGenre5.setVisibility(View.GONE);
        txtGenre6.setVisibility(View.GONE);
        txtGenre7.setVisibility(View.GONE);
        artistArea.setVisibility(View.GONE);
        imgArtist1.setVisibility(View.GONE);
        imgArtist2.setVisibility(View.GONE);
        imgArtist3.setVisibility(View.GONE);
        imgArtist4.setVisibility(View.GONE);
        txtName1.setVisibility(View.GONE);
        txtName2.setVisibility(View.GONE);
        txtName3.setVisibility(View.GONE);
        txtName4.setVisibility(View.GONE);
        concertName1.setVisibility(View.GONE);
        concertName2.setVisibility(View.GONE);
        concertName3.setVisibility(View.GONE);
        concertName4.setVisibility(View.GONE);
        concertName5.setVisibility(View.GONE);
        concertName6.setVisibility(View.GONE);
        concertName7.setVisibility(View.GONE);
        concertName8.setVisibility(View.GONE);
    }

    private void initializeViews(View view) {
        // TextView 초기화
        txtGenre1 = view.findViewById(R.id.txtGenre1);
        txtGenre2 = view.findViewById(R.id.txtGenre2);
        txtGenre3 = view.findViewById(R.id.txtGenre3);
        txtGenre4 = view.findViewById(R.id.txtGenre4);
        txtGenre5 = view.findViewById(R.id.txtGenre5);
        txtGenre6 = view.findViewById(R.id.txtGenre6);
        txtGenre7 = view.findViewById(R.id.txtGenre7);

        txtName1 = view.findViewById(R.id.txtName1);
        txtName2 = view.findViewById(R.id.txtName2);
        txtName3 = view.findViewById(R.id.txtName3);
        txtName4 = view.findViewById(R.id.txtName4);

        concertName1 = view.findViewById(R.id.concertName1);
        concertName2 = view.findViewById(R.id.concertName2);
        concertName3 = view.findViewById(R.id.concertName3);
        concertName4 = view.findViewById(R.id.concertName4);
        concertName5 = view.findViewById(R.id.concertName5);
        concertName6 = view.findViewById(R.id.concertName6);
        concertName7 = view.findViewById(R.id.concertName7);
        concertName8 = view.findViewById(R.id.concertName8);

    }

    private void updateGenres(List<GenreLike> genreLikes) {
        TextView[] genreViews = {txtGenre1, txtGenre2, txtGenre3, txtGenre4, txtGenre5, txtGenre6, txtGenre7};
        int index = 0;

        for (GenreLike genreLike : genreLikes) {
            if (index < genreViews.length) {
                genreViews[index].setText(genreLike.getGenre());
                genreViews[index].setVisibility(View.VISIBLE);
                index++;
            } else {
                break;
            }
        }
    }

    private void updateArtists(List<Artist> artistLikes) {
        ImageView[] artistImages = {imgArtist1, imgArtist2, imgArtist3, imgArtist4};
        TextView[] artistNames = {txtName1, txtName2, txtName3, txtName4};
        int index = 0;

        for (Artist artistLike : artistLikes) {
            if (index < artistImages.length) {
                Glide.with(this).load(artistLike.getUrl()).into(artistImages[index]);
                artistImages[index].setVisibility(View.VISIBLE);
                artistNames[index].setText(artistLike.getName());
                artistNames[index].setVisibility(View.VISIBLE);
                index++;
            } else {
                break;
            }
        }
        if (index > 0) {
            artistArea.setVisibility(View.VISIBLE);
        }
    }

    private void updateConcerts(List<Concert> concertLikes) {
        TextView[] concertViews = {concertName1, concertName2, concertName3, concertName4, concertName5, concertName6, concertName7, concertName8};
        int index = 0;

        for (Concert concertLike : concertLikes) {
            if (index < concertViews.length) {
                concertViews[index].setText(concertLike.getTitle());
                concertViews[index].setVisibility(View.VISIBLE);
                index++;
            } else {
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchUserLikes();
    }


}

package com.hani.ticketmerge.adapter;

import static com.hani.ticketmerge.api.NetworkClient.retrofit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.hani.ticketmerge.LoginActivity;
import com.hani.ticketmerge.R;
import com.hani.ticketmerge.api.LikeApi;
import com.hani.ticketmerge.config.Config;
import com.hani.ticketmerge.model.ArtistLike;
import com.hani.ticketmerge.model.Res;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context context;
    private List<ArtistLike> artistList;

    public ArtistAdapter(Context context, List<ArtistLike> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favoriteartist_row, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        ArtistLike artist = artistList.get(position);
        holder.txtName.setText(artist.getName());
        String gender = artist.getGender();
        String member = artist.getMember();
        String GM = gender + " / " + member;
        holder.txtGenderMember.setText(GM);
        holder.txtGenre.setText(artist.getGenre());

        Glide.with(context)
                .load(artist.getUrl())
                .into(holder.imgArtist);

        // Set favorite icon
        if (artist.getIsLike() == 1) {
            holder.imgFavoriteArtist.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            holder.imgFavoriteArtist.setImageResource(R.drawable.baseline_favorite_border_24);
        }

        // 좋아요 이미지 클릭 리스너 설정
        holder.imgFavoriteArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int combinedId = artist.getId();
                int type = 2; // 예시로 2를 사용하였으나, 실제로는 해당하는 타입을 설정해야 합니다.

                SharedPreferences sp = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
                boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

                if (!isLoggedIn) {
                    // 로그인하지 않은 경우 로그인 액티비티로 이동
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("로그인하시겠습니까?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 그냥 반환
                                }
                            });
                    builder.show();
                    return;
                }

                LikeApi likeApi = retrofit.create(LikeApi.class);
                Call<Res> call;
                if (artist.getIsLike() == 1) {
                    // 좋아요 취소 API 호출
                    call = likeApi.likeCancle(combinedId, type);
                } else {
                    // 좋아요 API 호출
                    call = likeApi.like(combinedId, type);
                }

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {

                        if (response.isSuccessful()) {
                            // 좋아요 상태 업데이트
                            artist.setIsLike(artist.getIsLike() == 1 ? 0 : 1);
                            notifyDataSetChanged();
                            // 데이터 저장 로직 (필요 시 구현)
                            SharedPreferences sp = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("artistLikes", combinedId); // 예시로 artistLikes를 저장하는 예시 코드입니다.
                            editor.apply();
                            notifyDataSetChanged();
                        } else if (response.code() == 401) {
                            // 401 Unauthorized 에러 처리
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("로그인하시겠습니까?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 로그인 액티비티로 이동
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            context.startActivity(intent);
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
                            // 401 Unauthorized 에러 처리
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("로그인하시겠습니까?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 로그인 액티비티로 이동
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            context.startActivity(intent);
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
                            Toast.makeText(context,
                                    "좋아요 처리 실패",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        // 호출 실패 처리
                        Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public void setData(List<ArtistLike> newArtistList) {
        this.artistList.clear();
        this.artistList.addAll(newArtistList);
        notifyDataSetChanged();
    }

    public void addData(List<ArtistLike> newArtistList) {
        this.artistList.addAll(newArtistList);
        notifyDataSetChanged();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtGenderMember, txtGenre;
        ImageView imgArtist, imgFavoriteArtist;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtGenderMember = itemView.findViewById(R.id.txtGenderMember);
            txtGenre = itemView.findViewById(R.id.txtGenre);
            imgArtist = itemView.findViewById(R.id.imgArtist);
            imgFavoriteArtist = itemView.findViewById(R.id.imgFavoriteArtist);
        }
    }
}
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
import com.hani.ticketmerge.model.Concert;
import com.hani.ticketmerge.model.Res;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder> {
    private Context context;
    private List<Concert> concertList;

    public ConcertAdapter(Context context, List<Concert> concertList) {
        this.context = context;
        this.concertList = concertList;
    }

    @NonNull
    @Override
    public ConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favoriteconcert_row, parent, false);
        return new ConcertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcertViewHolder holder, int position) {
        Concert concert = concertList.get(position);
        holder.txtTitle.setText(concert.getTitle());
        String date1 = concert.getStartDate();
        String date2 = concert.getEndDate();
        holder.txtDate.setText( date1 + " - " + date2);
        holder.txtPlace.setText(concert.getPlace());

        Glide.with(context)
                .load(concert.getThumbnailUrl())
                .into(holder.imgViewThumbnail);

        if (concert.getIsLike() == 1) {
            holder.imgFavoriteConcert.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            holder.imgFavoriteConcert.setImageResource(R.drawable.baseline_favorite_border_24);
        }

        // 좋아요 이미지 클릭 리스너 설정
        holder.imgFavoriteConcert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int combinedId = concert.getId();
                int type = 1;

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
                if (concert.getIsLike() == 1) {
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
                            concert.setIsLike(concert.getIsLike() == 1 ? 0 : 1);
                            notifyDataSetChanged();
                            // 데이터 저장 로직 (필요 시 구현)
                            SharedPreferences sp = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("concertLikes", combinedId); // 예시로 concertLikes를 저장하는 예시 코드입니다.
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
        return concertList.size();
    }

    public void setData(List<Concert> newConcerts) {
        concertList.clear();
        concertList.addAll(newConcerts);
        notifyDataSetChanged();
    }

    public void addData(List<Concert> newConcerts) {
        concertList.addAll(newConcerts);
        notifyDataSetChanged();
    }

    public static class ConcertViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate, txtPlace;
        ImageView imgViewThumbnail, imgFavoriteConcert;

        public ConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            imgViewThumbnail = itemView.findViewById(R.id.imgViewThumbnail);
            imgFavoriteConcert = itemView.findViewById(R.id.imgFavoriteConcert);
        }
    }
}

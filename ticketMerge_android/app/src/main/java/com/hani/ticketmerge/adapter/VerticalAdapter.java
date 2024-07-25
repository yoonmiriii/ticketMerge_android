package com.hani.ticketmerge.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hani.ticketmerge.DetailConcertActivity;
import com.hani.ticketmerge.R;
import com.hani.ticketmerge.config.Config;
import com.hani.ticketmerge.model.Concert;

import java.util.List;

public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.ViewHolder> {

    private static Context context;
    private List<Concert> mData;


    public VerticalAdapter(Context context, List<Concert> data) {
        this.context = context;
        this.mData = data;
    }

    public void setData(List<Concert> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical, parent, false);
        return new ViewHolder(view, context, mData);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Concert item = mData.get(position);
        holder.title.setText(item.getTitle());
        holder.txtDate.setText(item.getStartDate() + " - " + item.getEndDate());
        holder.txtPlace.setText(item.getPlace());

        // 이미지 로딩 (Glide 사용 예시)
        Glide.with(holder.itemView.getContext())
                .load(item.getThumbnailUrl())
                .placeholder(R.drawable.placeholder) // 로딩 중일 때 보여줄 이미지
                .error(R.drawable.error) // 로딩 실패 시 보여줄 이미지
                .into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView title, txtDate, txtPlace;
        CardView cardView;
        private Context context;
        private List<Concert> data;

        public ViewHolder(View itemView, Context context, List<Concert> data) {
            super(itemView);
            this.context = context;
            this.data = data;

            imgView = itemView.findViewById(R.id.imgView);
            title = itemView.findViewById(R.id.title);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Log.i("HOME MAIN", String.valueOf(index));
                    if (index != RecyclerView.NO_POSITION) {
                        Concert concert = data.get(index);

                        Intent intent = new Intent(context, DetailConcertActivity.class);
                        intent.putExtra("ConcertId", concert.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
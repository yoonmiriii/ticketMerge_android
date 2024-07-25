package com.hani.ticketmerge.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hani.ticketmerge.DetailBoardActivity;
import com.hani.ticketmerge.R;
import com.hani.ticketmerge.model.Post;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;

    public BoardAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public void setData(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boader_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post item = postList.get(position);
        holder.txtName.setText(item.getName());
        holder.txtType.setText(item.getType());
        String createdAt = item.getCreatedAt();

        if (createdAt != null) {
            holder.txtDate.setText(createdAt.replace("T", " "));
        } else {
            holder.txtDate.setText(""); // 기본 텍스트 설정
        }
        holder.txtTitle.setText(item.getTitle());
        holder.commentCnt.setText(String.valueOf(item.getCommentCnt()));
        holder.viewCnt.setText(String.valueOf(item.getViewCnt()));


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = holder.getAdapterPosition();
                Log.i("BOARD", String.valueOf(index));
                if (index != RecyclerView.NO_POSITION) {
                    Post post = postList.get(index);

                    Intent intent = new Intent(context, DetailBoardActivity.class);
                    intent.putExtra("post", post); // Corrected key
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void searchItems(List<Post> list) {
        postList = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtDate;
        TextView txtTitle;
        TextView commentCnt;
        ImageView imgView;
        TextView viewCnt;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtType = itemView.findViewById(R.id.txtType);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            commentCnt = itemView.findViewById(R.id.commentCnt);
            imgView = itemView.findViewById(R.id.imgView);
            viewCnt = itemView.findViewById(R.id.viewCnt);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

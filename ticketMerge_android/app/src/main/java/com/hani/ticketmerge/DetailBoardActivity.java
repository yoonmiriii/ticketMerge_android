package com.hani.ticketmerge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.hani.ticketmerge.adapter.CommentAdapter;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.api.PostApi;
import com.hani.ticketmerge.api.UserApi;
import com.hani.ticketmerge.config.Config;
import com.hani.ticketmerge.model.CommentRequest;
import com.hani.ticketmerge.model.Comments;
import com.hani.ticketmerge.model.ImageUrl;
import com.hani.ticketmerge.model.MyInfo;
import com.hani.ticketmerge.model.Post;
import com.hani.ticketmerge.model.PostRes;
import com.hani.ticketmerge.model.Res;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailBoardActivity extends AppCompatActivity {

    private TextView txtName, txtType, txtDate, txtTitle, txtcontent, commentCnt, viewCnt,txtDelete;
    private Post post;
    private RecyclerView recyclerView;
    private ArrayList<Comments> commentArrayList = new ArrayList<>();
    private CommentAdapter adapter;
    ImageView imageView3;
    private int postId;
    EditText editText;
    Button btnSave;
    String comment;
    private String loggedInUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board);

        // Find views
        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);
        txtDate = findViewById(R.id.txtDate);
        txtTitle = findViewById(R.id.txtTitle);
        txtcontent = findViewById(R.id.txtcontent);
        commentCnt = findViewById(R.id.commentCnt);
        viewCnt = findViewById(R.id.viewCnt);
        txtDelete = findViewById(R.id.txtDelete);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnSave =findViewById(R.id.btnSave);
        editText = findViewById(R.id.editText);
        imageView3 = findViewById(R.id.imageView3);


        getmyinfo();
        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
                boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
                String token = sp.getString("token", null);

                if (!isLoggedIn) {
                    // 로그인하지 않은 경우 로그인 액티비티로 이동
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailBoardActivity.this);
                    builder.setMessage("로그인하시겠습니까?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(DetailBoardActivity.this, LoginActivity.class);
                                    startActivity(intent);
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
                comment=editText.getText().toString().trim();
                if(comment.isEmpty()){
                    Snackbar.make(btnSave,"댓글을 입력 해주세요",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                editText.getText().clear();
                Log.i("COMMENT",comment);

                addComment(postId);
            }
        });

        // Retrieve the post from the intent
        post = (Post) getIntent().getSerializableExtra("post");
        if (post != null) {
            postId = post.getId(); // Assuming postId is part of the Post object
            // Populate the views with data from the post object
            txtName.setText(post.getName());
            txtType.setText(post.getType());
            txtTitle.setText(post.getTitle());
            txtcontent.setText(post.getContent());
            String imgUrl = post.getImgUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                // Load the image using an image loading library like Glide or Picasso
                // Example using Glide
                Glide.with(this)
                        .load(imgUrl)
                        .into(imageView3);
                imageView3.setVisibility(View.VISIBLE);
            } else {
                imageView3.setVisibility(View.GONE);
            }





            // Extract date part and set txtDate
            String createdAt = post.getCreatedAt();
            if (createdAt != null && createdAt.contains("T")) {
                String[] parts = createdAt.split("T");
                txtDate.setText(parts[0]); // Display only the date part (yyyy-MM-dd)
            } else {
                txtDate.setText(createdAt); // Fallback if format is unexpected
            }

            commentCnt.setText(String.valueOf(post.getCommentCnt()));
            viewCnt.setText(String.valueOf(post.getViewCnt()));
        } else {
            // Handle the case where post is null
        }

        // Initialize RecyclerView adapter
        adapter = new CommentAdapter(this, commentArrayList); // Pass context and data to adapter
        recyclerView.setAdapter(adapter);

        // Fetch comments data from network
        getNetworkData(postId);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailBoardActivity.this);
        builder.setCancelable(true);
        builder.setTitle(" 게시글 삭제");
        builder.setMessage("정말 삭제 하시겠습니까??");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Retrofit retrofit = NetworkClient.getRetrofitClient(DetailBoardActivity.this);

                PostApi api = retrofit.create(PostApi.class);

                Call<Res> call = api.deletePost(postId);
                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable throwable) {

                    }
                });

            }
        });
        builder.setNegativeButton("No", null);
        builder.show();


    }

    private void getmyinfo() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(DetailBoardActivity.this);
        UserApi api = retrofit.create(UserApi.class);
        Call<MyInfo> call = api.getmyinfo();
        call.enqueue(new Callback<MyInfo>() {
            @Override
            public void onResponse(Call<MyInfo> call, Response<MyInfo> response) {
                if(response.isSuccessful() && response.body() != null){
                    MyInfo myInfo = response.body();
                    if(myInfo.getItems() != null && !myInfo.getItems().isEmpty()){
                        // 첫 번째 사용자의 ID를 가져와서 loggedInUserId 설정
                        loggedInUserId = String.valueOf(myInfo.getUserId());

                        // 로그인된 사용자의 ID와 포스트의 작성자 ID를 비교하여 삭제 버튼 보이기/숨기기 설정
                        if (loggedInUserId.equals(String.valueOf(post.getUserId()))) {
                            txtDelete.setVisibility(View.VISIBLE); // 삭제 버튼 보이기
                        } else {
                            txtDelete.setVisibility(View.GONE); // 삭제 버튼 숨기기
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyInfo> call, Throwable throwable) {

            }
        });


    }


    private void addComment(int postId) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(DetailBoardActivity.this);
        PostApi api = retrofit.create(PostApi.class);
        CommentRequest commentRequest = new CommentRequest(comment);
// 댓글 추가 요청
        Call<Res> call = api.addComment(postId, commentRequest);
        Log.i("COMMENT",comment);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                if (response.isSuccessful()) {
                    // 처리 성공
                    Snackbar.make(btnSave, "댓글이 추가되었습니다", Snackbar.LENGTH_SHORT).show();
                    getNetworkData(postId);
                } else {
                    // 처리 실패
                    Snackbar.make(btnSave, "댓글 추가 실패", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable throwable) {
                // 네트워크 오류 등 호출 실패 시 처리
                Snackbar.make(btnSave, "댓글 추가 실패: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void getNetworkData(int postId) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        PostApi api = retrofit.create(PostApi.class);
        Call<PostRes> call = api.getPostDetail(postId);
        call.enqueue(new Callback<PostRes>() {
            @Override
            public void onResponse(Call<PostRes> call, Response<PostRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostRes postRes = response.body();
                    List<Comments> comments = postRes.getComments();

                    if (comments != null) {
                        commentArrayList.clear();
                        commentArrayList.addAll(comments);
                        adapter.notifyDataSetChanged(); // Adapter에 데이터 변경 알림

                        Log.d("DetailBoardActivity", "댓글 개수: " + commentArrayList.size());
                    } else {
                        Log.e("DetailBoardActivity", "댓글 리스트가 null입니다.");
                        // 댓글 목록이 null인 경우 적절히 처리
                    }
                } else {
                    Log.e("DetailBoardActivity", "API 호출이 실패하였습니다.");
                    // API 호출 실패에 대한 처리
                }
            }

            @Override
            public void onFailure(Call<PostRes> call, Throwable throwable) {
                // Handle failure to fetch data
            }
        });
    }
    @Override
    public void onResume() {

        super.onResume();
    }

}

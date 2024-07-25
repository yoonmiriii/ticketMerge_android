package com.hani.ticketmerge;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.api.PostApi;
import com.hani.ticketmerge.config.Config;
import com.hani.ticketmerge.model.Post;
import com.hani.ticketmerge.model.PostRes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddBoardActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    public EditText editTitle;
    public EditText editContent;
    public Button btnSave;
    private Spinner spinner;
    private SharedPreferences sp;
    private int selectedType = 1;
    private Uri imageUri;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);
        spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.border_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = position + 1; // 1: 자유게시판, 2: 같이 가요, 3: 후기
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
            }
        });
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

        }
    }

    // 게시물 추가 후 호출되는 메서드
    public void addNewPostAndFinish(Post newPost) {
        // BoardFragment 인스턴스를 가져오기
        BoardFragment boardFragment = (BoardFragment) getSupportFragmentManager().findFragmentById(R.id.boardFragment);

        if (boardFragment != null) {
            // BoardFragment의 addNewPost 메서드 호출
            boardFragment.addNewPost(newPost);
        }
        // Activity 종료
        finish();
    }


    public void savePost() {
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("제목을 입력하세요");
            editTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            editContent.setError("내용을 입력하세요");
            editContent.requestFocus();
            return;
        }

        // 스피너에서 선택한 게시판 유형
        String selectedBoard = spinner.getSelectedItem().toString();
        int types = getBoardType(selectedBoard);

        // 토큰 가져오기
        sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "토큰이 없습니다. 다시 로그인하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrofit을 사용하여 API 호출
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        PostApi postApi = retrofit.create(PostApi.class);

        RequestBody typesBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(types));
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                File file = new File(getCacheDir(), "image.jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                fileOutputStream.close();
                inputStream.close();
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Call<PostRes> call = postApi.addPostList("Bearer " + token, types, titleBody, contentBody, imagePart);

        call.enqueue(new Callback<PostRes>() {
            @Override
            public void onResponse(Call<PostRes> call, Response<PostRes> response) {
                if (response.isSuccessful()) {
                    PostRes postRes = response.body();
                    if (postRes != null && postRes.getResult().equals("success")) {
                        Toast.makeText(AddBoardActivity.this, "게시글이 저장되었습니다", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        Post newPost = new Post();
                        resultIntent.putExtra("newPost", newPost);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(AddBoardActivity.this, "게시글 저장에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddBoardActivity.this, "서버 응답을 받지 못했습니다", Toast.LENGTH_SHORT).show();
                    Log.e("AddBoardActivity", "Response error: " + response.code());
                    try {
                        Log.e("AddBoardActivity", "Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("AddBoardActivity", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostRes> call, Throwable t) {
                Toast.makeText(AddBoardActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AddBoardActivity", "Network error: ", t);
            }
        });
    }

    //
    private int getBoardType(String selectedBoard){
        switch (selectedBoard){
            case "자유게시판":
                return 1;
            case "같이 가요":
                return 2;
            case "후기":
                return 3;
            default:
                return 1;

        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}
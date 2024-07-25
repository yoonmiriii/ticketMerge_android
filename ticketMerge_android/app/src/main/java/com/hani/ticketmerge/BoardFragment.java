package com.hani.ticketmerge;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hani.ticketmerge.adapter.BoardAdapter;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.api.PostApi;
import com.hani.ticketmerge.model.Post;
import com.hani.ticketmerge.model.PostRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public BoardFragment() {}

    public static BoardFragment newInstance(String param1, String param2) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public EditText editSearch;
    public Button btnSearch;
    public String keyword = "";

    public Button btnPost;
    public Button btnFull;
    public Button btnFbb;
    public Button btnWith;
    public Button btnEpl;
    private RecyclerView boardRecyclerview;
    private BoardAdapter boardAdapter;
    private SharedPreferences sp;
    private boolean isLoggedIn;
    private boolean isLoadingPost = false;
    private List<Post> postList = new ArrayList<>();

    private static final int REQUEST_ADD_POST = 1;
    private int limit = 20;
    private int offset = 0;
    private int type = 0;
    private TextView textView3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);

        boardRecyclerview = rootView.findViewById(R.id.boardRecyclerview);
        editSearch = rootView.findViewById(R.id.editSearch);
        btnSearch = rootView.findViewById(R.id.btnSave);
        btnFull = rootView.findViewById(R.id.btnFull);
        btnFbb = rootView.findViewById(R.id.btnFbb);
        btnWith = rootView.findViewById(R.id.btnWith);
        btnEpl = rootView.findViewById(R.id.btnEpl);
        btnPost = rootView.findViewById(R.id.btnPost);
        textView3 = rootView.findViewById(R.id.textView3);

        boardRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        boardAdapter = new BoardAdapter(getContext(), new ArrayList<>());
        boardRecyclerview.setAdapter(boardAdapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = editSearch.getText().toString().trim();

                if (keyword.isEmpty()) {
                    Snackbar.make(btnSearch, "검색어를 입력하세요", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                postList.clear();
                fetchPostSearchData();
            }
        });

        btnFull.setTextColor(getResources().getColor(R.color.purple_500));
        Collections.reverse(postList);
        boardAdapter.setData(postList);

        btnFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0;
                offset = 0;
                postList.clear();
                fetchPostData();

                btnFull.setTextColor(getResources().getColor(R.color.purple_500));
                btnFbb.setTextColor(Color.BLACK);
                btnWith.setTextColor(Color.BLACK);
                btnEpl.setTextColor(Color.BLACK);

                Collections.reverse(postList);
                boardAdapter.setData(postList);
            }
        });

        btnFbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                offset = 0;
                postList.clear();
                fetchPostData();

                btnFbb.setTextColor(getResources().getColor(R.color.purple_500));
                btnWith.setTextColor(Color.BLACK);
                btnFull.setTextColor(Color.BLACK);
                btnEpl.setTextColor(Color.BLACK);
            }
        });

        btnWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                offset = 0;
                postList.clear();
                fetchPostData();

                btnWith.setTextColor(getResources().getColor(R.color.purple_500));
                btnFbb.setTextColor(Color.BLACK);
                btnFull.setTextColor(Color.BLACK);
                btnEpl.setTextColor(Color.BLACK);

                Collections.reverse(postList);
                boardAdapter.setData(postList);
            }
        });

        btnEpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 3;
                offset = 0;
                postList.clear();
                fetchPostData();

                btnEpl.setTextColor(getResources().getColor(R.color.purple_500));
                btnFbb.setTextColor(Color.BLACK);
                btnFull.setTextColor(Color.BLACK);
                btnWith.setTextColor(Color.BLACK);

                Collections.reverse(postList);
                boardAdapter.setData(postList);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                isLoggedIn = sp.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    Intent intent = new Intent(getActivity(), AddBoardActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_POST);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("로그인하시겠습니까?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
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
            }
        });

        boardRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 맨 마지막 데이터가 화면에 보이게 되면,
                // 네트워크 통해서 데이터를 추가로 가져오도록 한다.
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if (lastPosition + 1 == totalCount) {
                    // 네트워크로부터 데이터를 추가로 받아온다.
                    fetchPostData();
                }
            }
        });

        fetchPostData();
        return rootView;
    }

    private void fetchPostSearchData() {
        PostApi postApi = NetworkClient.getRetrofitClient(getContext()).create(PostApi.class);
        Call<PostRes> call = postApi.getPostSearch(offset, limit, keyword);
        call.enqueue(new Callback<PostRes>() {
            @Override
            public void onResponse(Call<PostRes> call, Response<PostRes> response) {
                isLoadingPost = false;
                textView3.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    PostRes postRes = response.body();
                    if (postRes.getResult().equals("success")) {
                        List<Post> posts = postRes.getItems();
                        if (posts != null && !posts.isEmpty()) {
                            postList.clear();
                            postList.addAll(posts);
                            boardAdapter.setData(postList);
                        } else {
                            textView3.setText("검색 결과가 없습니다");
                            textView3.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), "데이터를 가져오지 못했습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "응답을 받지 못했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostRes> call, Throwable t) {
                isLoadingPost = false;
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchPostData() {
        boardAdapter.notifyDataSetChanged();

        isLoadingPost = true;
        textView3.setVisibility(View.GONE);
        PostApi postApi = NetworkClient.getRetrofitClient(getContext()).create(PostApi.class);
        Call<PostRes> call = postApi.getPostList(type, offset, limit);

        call.enqueue(new Callback<PostRes>() {
            @Override
            public void onResponse(Call<PostRes> call, Response<PostRes> response) {
                isLoadingPost = false;
                if (response.isSuccessful() && response.body() != null) {
                    PostRes postRes = response.body();
                    if (postRes.getResult().equals("success")) {
                        List<Post> posts = postRes.getItems();
                        if (posts != null && !posts.isEmpty()) {
                            postList.clear();
                            postList.addAll(posts);

                            boardAdapter.setData(postList);
                        } else {
                            Toast.makeText(getContext(), "데이터가 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "데이터를 가져오지 못했습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "응답을 받지 못했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostRes> call, Throwable t) {
                isLoadingPost = false;
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String addNewPost(Post newPost) {
        if (boardAdapter != null) {
            postList.add(0, newPost);
            boardAdapter.notifyItemInserted(0);
            boardRecyclerview.scrollToPosition(0);
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_POST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra("newPost")) {
                Post newPost = (Post) data.getSerializableExtra("newPost");
                addNewPost(newPost);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchPostData();
        boardAdapter.notifyDataSetChanged();
    }
}

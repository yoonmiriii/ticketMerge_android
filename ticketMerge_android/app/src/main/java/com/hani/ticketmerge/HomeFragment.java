package com.hani.ticketmerge;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hani.ticketmerge.adapter.HorizontalAdapter;
import com.hani.ticketmerge.adapter.VerticalAdapter;
import com.hani.ticketmerge.api.ConcertApi;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.model.Concert;
import com.hani.ticketmerge.model.ConcertRes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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


    private RecyclerView horizontalRecyclerView;
    private RecyclerView verticalRecyclerView;
    private VerticalAdapter verticalAdapter;
    private HorizontalAdapter horizontalAdapter;
    private List<Concert> verticalConcertList = new ArrayList<>();
    private List<Concert> horizontalConcertList = new ArrayList<>();

    private static final int PAGE_SIZE = 10;
    private int verticalPage = 0;
    private int horizontalPage = 0;
    private boolean isLoadingVertical = false;
    private boolean isLoadingHorizontal = false;

    private RadioGroup radioGroupCategory;
    private RadioGroup radioGroupOrder;
    private LinearLayout genreAndPlace;

    private TextView txt0, txt1, txt2, txt3, txt4, txt5, txt6, txt7, txt8;
    private RadioButton rdbtnGenre, rdbtnPlace;
    private RadioButton rdbtnRecommended, rdbtnView, rdbtnDatelated;


    private int selectedGenre = 0;
    private int selectedPlace = 0;
    private int selectedSortType = 1;

    private int lastSelectedGenre = 0;
    private int lastSelectedPlace = 0;
    private int lastSelectedSortType = 1;


    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // RecyclerView 초기화
        verticalRecyclerView = rootView.findViewById(R.id.verticalRecyclerView);
        horizontalRecyclerView = rootView.findViewById(R.id.horizontalRecyclerView);

        // LayoutManager 설정
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Adapter 초기화
        verticalAdapter = new VerticalAdapter(requireContext(), new ArrayList<>());
        horizontalAdapter = new HorizontalAdapter(requireContext(), new ArrayList<>());

        // RecyclerView에 Adapter 설정
        verticalRecyclerView.setAdapter(verticalAdapter);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        // RadioGroup 및 RadioButton 초기화
        radioGroupCategory = rootView.findViewById(R.id.radioGroupCategory);
        radioGroupOrder = rootView.findViewById(R.id.radioGroupOrder);

        rdbtnGenre = rootView.findViewById(R.id.rdbtnGenre);
        rdbtnPlace = rootView.findViewById(R.id.rdbtnPlace);

        rdbtnRecommended = rootView.findViewById(R.id.rdbtnRecommended);
        rdbtnView = rootView.findViewById(R.id.rdbtnView);
        rdbtnDatelated = rootView.findViewById(R.id.rdbtnDatelated);

        genreAndPlace = rootView.findViewById(R.id.genreAndPlace);

        // TextView 초기화
        txt0 = rootView.findViewById(R.id.txt0);
        txt1 = rootView.findViewById(R.id.txt1);
        txt2 = rootView.findViewById(R.id.txt2);
        txt3 = rootView.findViewById(R.id.txt3);
        txt4 = rootView.findViewById(R.id.txt4);
        txt5 = rootView.findViewById(R.id.txt5);
        txt6 = rootView.findViewById(R.id.txt6);
        txt7 = rootView.findViewById(R.id.txt7);
        txt8 = rootView.findViewById(R.id.txt8);

        // 초기 상태 설정
        rdbtnGenre.setChecked(true);
        rdbtnRecommended.setChecked(true);
        updateScrollViewForGenre();
        updateRadioGroupCategory();
        updateRadioGroupOrder();



        // 카테고리 변경 이벤트 처리
        radioGroupCategory.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedCategoryRadioButton = rootView.findViewById(checkedId);
            String categoryString = selectedCategoryRadioButton.getText().toString().trim();

            if (categoryString.equals("장르별선택")) {
                selectedGenre = getSelectedGenre();
                updateScrollViewForGenre();
            } else if (categoryString.equals("지역별선택")) {
                selectedPlace = getSelectedPlace();
                updateScrollViewForPlace();
            }
            verticalConcertList.clear();
            verticalAdapter.notifyDataSetChanged();
            verticalPage = 0 ;
            fetchData();
        });

        radioGroupOrder.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedOrderRadioButton = rootView.findViewById(checkedId);
            String orderString = selectedOrderRadioButton.getText().toString().trim();

            if (orderString.equals("추천순")) {
                rdbtnRecommended.setChecked(true);
                rdbtnView.setChecked(false);
                rdbtnDatelated.setChecked(false);
                selectedSortType = 1;
            } else if (orderString.equals("조회순")) {
                rdbtnView.setChecked(true);
                rdbtnRecommended.setChecked(false);
                rdbtnDatelated.setChecked(false);
                selectedSortType = 2;
            } else if (orderString.equals("공연임박순")) {
                rdbtnDatelated.setChecked(true);
                rdbtnRecommended.setChecked(false);
                rdbtnView.setChecked(false);
                selectedSortType = 3;
            }
            verticalConcertList.clear();
            verticalAdapter.notifyDataSetChanged();
            verticalPage = 0;
            fetchData();
        });


        // 모든 TextView들에 대해 클릭 리스너 설정
        for (int i = 0; i < genreAndPlace.getChildCount(); i++) {
            final int index = i;
            View childView = genreAndPlace.getChildAt(i);
            if (childView instanceof TextView) {
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleTextViewClick(index);
                    }
                });
            }
        }
        // 기본값으로 첫 번째 TextView 선택 처리
        selectTextView(0); // 예를 들어, 전체(0번째)를 기본으로 선택

        verticalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    if (!isLoadingVertical && lastVisibleItemPosition >= totalItemCount - 2
                            && totalItemCount >= PAGE_SIZE) {
                        verticalPage += PAGE_SIZE;
                        fetchData();
                    }
                }
            }
        });


        horizontalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoadingHorizontal && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 1
                        && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    isLoadingHorizontal = true;
                    horizontalPage += PAGE_SIZE;
                    fetchHorizontalConcertData();
                }
            }
        });


        fetchHorizontalConcertData();
        fetchData();
        return rootView;
    }
    private void updateScrollViewForGenre() {
        txt0.setText("전체");
        txt1.setText("발라드");
        txt2.setText("댄스");
        txt3.setText("힙합");
        txt4.setText("락");
        txt5.setText("트로트");
        txt6.setText("내한");
        txt7.setText("그 외");
        txt8.setVisibility(View.INVISIBLE);
        // 텍스트뷰 색상 초기화
        txt0.setTextColor(getResources().getColor(R.color.purple_500));
        txt1.setTextColor(getResources().getColor(R.color.black));
        txt2.setTextColor(getResources().getColor(R.color.black));
        txt3.setTextColor(getResources().getColor(R.color.black));
        txt4.setTextColor(getResources().getColor(R.color.black));
        txt5.setTextColor(getResources().getColor(R.color.black));
        txt6.setTextColor(getResources().getColor(R.color.black));
        txt7.setTextColor(getResources().getColor(R.color.black));
    }

    private void updateScrollViewForPlace() {
        txt0.setText("전체");
        txt1.setText("서울");
        txt2.setText("경기/인천");
        txt3.setText("강원/춘천/속초");
        txt4.setText("충청/세종/대전");
        txt5.setText("전라/광주");
        txt6.setText("경북/대구");
        txt7.setText("경남/울산/부산");
        txt8.setText("제주");
        txt8.setVisibility(View.VISIBLE);
        // 텍스트뷰 색상 초기화
        txt0.setTextColor(getResources().getColor(R.color.purple_500));
        txt1.setTextColor(getResources().getColor(R.color.black));
        txt2.setTextColor(getResources().getColor(R.color.black));
        txt3.setTextColor(getResources().getColor(R.color.black));
        txt4.setTextColor(getResources().getColor(R.color.black));
        txt5.setTextColor(getResources().getColor(R.color.black));
        txt6.setTextColor(getResources().getColor(R.color.black));
        txt7.setTextColor(getResources().getColor(R.color.black));
        txt8.setTextColor(getResources().getColor(R.color.black));
    }

    // 클릭된 TextView 처리 메서드
    private void handleTextViewClick(int selectedPosition) {
        // 선택된 TextView의 색상 변경
        selectTextView(selectedPosition);

        // 선택된 위치에 따라 다른 처리를 수행할 수 있음
        switch (selectedPosition) {
            case 0:
                selectedGenre = 0;
                selectedPlace = 0;
                break;
            case 1:
                selectedGenre = 1;
                selectedPlace = 1;
                break;
            case 2:
                selectedGenre = 2;
                selectedPlace = 2;
                break;
            case 3:
                selectedGenre = 3;
                selectedPlace = 3;
                break;
            case 4:
                selectedGenre = 4;
                selectedPlace = 4;
                break;
            case 5:
                selectedGenre = 5;
                selectedPlace = 5;
                break;
            case 6:
                selectedGenre = 6;
                selectedPlace = 6;
                break;
            case 7:
                selectedGenre = 7;
                selectedPlace = 7;
                break;
            case 8:
                selectedGenre = 8;
                selectedPlace = 8;
                break;

        }

        // 선택된 값에 따른 추가 로직 수행
        verticalConcertList.clear();
        verticalAdapter.notifyDataSetChanged();
        verticalPage = 0 ;
        fetchData(); // 예시로 데이터를 다시 가져오는 메서드 호출
    }

    // TextView 선택 처리 메서드
    private void selectTextView(int position) {
        for (int i = 0; i < genreAndPlace.getChildCount(); i++) {
            View childView = genreAndPlace.getChildAt(i);
            if (childView instanceof TextView) {
                TextView textView = (TextView) childView;
                if (i == position) {
                    // 선택된 텍스트뷰의 색상을 파란색으로 변경
                    textView.setTextColor(getResources().getColor(R.color.purple_500));
                } else {
                    // 선택되지 않은 텍스트뷰의 색상을 검정색으로 변경
                    textView.setTextColor(getResources().getColor(R.color.black));
                }
            }
        }
    }
    private void updateRadioGroupCategory() {
        rdbtnGenre.setChecked(true);  // 장르별선택 기본 선택
    }
    private void updateRadioGroupOrder() {
        rdbtnRecommended.setChecked(true);  // 추천순 기본 선택
    }

    private void fetchData() {
        if (isLoadingVertical) {
            return; // 이미 데이터를 로딩 중이면 더 이상 호출하지 않음
        }
        isLoadingVertical = true;

        // 선택된 라디오 버튼 상태에 따라 쿼리 파라미터 설정
        int genre = selectedGenre;
        int place = selectedPlace;
        int sort = selectedSortType;
        int category = rdbtnGenre.isChecked() ? 1 : 2;  // 장르 1, 장소 2


        Log.i("HOME MAIN","type: " +category+ ", genre : "+ genre+", Place: "+ place+ ", Sort: "+ sort);
        // API 호출
        ConcertApi concertApi = NetworkClient.getRetrofitClient(getContext()).create(ConcertApi.class);
        Call<ConcertRes> call = concertApi.getConcertListView(verticalPage, PAGE_SIZE, category, genre, place, sort);


        call.enqueue(new Callback<ConcertRes>() {
            @Override
            public void onResponse(Call<ConcertRes> call, Response<ConcertRes> response) {
                isLoadingVertical = false;
                if (response.isSuccessful() && response.body() != null) {
                    ConcertRes concertRes = response.body();
                    if (concertRes.getResult().equals("success")) {
                        List<Concert> concerts = concertRes.getItems();
                        if (concerts != null && !concerts.isEmpty()) {
                            verticalConcertList.addAll(concerts);
                            verticalAdapter.setData(verticalConcertList);
                        } else {
                            Toast.makeText(getContext(),
                                    "데이터가 없습니다",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "데이터를 가져오지 못했습니다",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "응답을 받지 못했습니다",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConcertRes> call, Throwable t) {
                isLoadingVertical = false;
                Toast.makeText(getContext(),
                        "네트워크 오류: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.i("FAIL VERTICAL", t.getMessage());
            }
        });
    }


    private void fetchHorizontalConcertData() {
        isLoadingHorizontal = true;
        ConcertApi concertApi = NetworkClient.getRetrofitClient(getContext()).create(ConcertApi.class);
        Call<ConcertRes> call = concertApi.getConcertList(horizontalPage, PAGE_SIZE);

        call.enqueue(new Callback<ConcertRes>() {
            @Override
            public void onResponse(Call<ConcertRes> call, Response<ConcertRes> response) {
                isLoadingHorizontal = false;
                if (response.isSuccessful() && response.body() != null) {
                    ConcertRes concertRes = response.body();
                    if (concertRes.getResult().equals("success")) {
                        List<Concert> concerts = concertRes.getItems();
                        if (concerts != null && !concerts.isEmpty()) {
                            horizontalConcertList.addAll(concerts);
                            horizontalAdapter.setData(horizontalConcertList);
                        } else {
                            Toast.makeText(getContext(),
                                    "데이터가 없습니다",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "데이터를 가져오지 못했습니다",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "응답을 받지 못했습니다",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConcertRes> call, Throwable t) {
                isLoadingHorizontal = false;
                Toast.makeText(getContext(),
                        "네트워크 오류: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.i("FAIL HORIZONTAL", t.getMessage());
            }
        });
    }


    private int getSelectedGenre() {
        int selectedId = radioGroupCategory.getCheckedRadioButtonId();
        if (selectedId == R.id.txt0) {
            return 0; // 전체
        } else if (selectedId == R.id.txt1) {
            return 1; // 발라드
        } else if (selectedId == R.id.txt2) {
            return 2; // 댄스
        } else if (selectedId == R.id.txt3) {
            return 3; // 힙합
        } else if (selectedId == R.id.txt4) {
            return 4; // 락
        } else if (selectedId == R.id.txt5) {
            return 5; // 트로트
        } else if (selectedId == R.id.txt6) {
            return 6; // 내한
        } else if (selectedId == R.id.txt7) {
            return 7; // 그 외
        } else {
            return 0; // 기본값으로 전체
        }
    }
    private int getSelectedPlace() {
        int selectedId = radioGroupCategory.getCheckedRadioButtonId();
        if (selectedId == R.id.txt0) {
            return 0; // 전체
        } else if (selectedId == R.id.txt1) {
            return 1; // 서울
        } else if (selectedId == R.id.txt2) {
            return 2; // 경기/인천
        } else if (selectedId == R.id.txt3) {
            return 3; // 강원/춘천/속초
        } else if (selectedId == R.id.txt4) {
            return 4; // 충청/세종/대전
        } else if (selectedId == R.id.txt5) {
            return 5; // 전라/광주
        } else if (selectedId == R.id.txt6) {
            return 6; // 경북/대구
        } else if (selectedId == R.id.txt7) {
            return 7; // 경남/울산/부산
        } else if (selectedId == R.id.txt8) {
            return 8; // 제주
        } else {
            return 0; // 기본값으로 전체
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchData();
        fetchHorizontalConcertData();
        verticalAdapter.notifyDataSetChanged();
        horizontalAdapter.notifyDataSetChanged();
    }


}
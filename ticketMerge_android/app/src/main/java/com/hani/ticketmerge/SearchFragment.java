package com.hani.ticketmerge;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hani.ticketmerge.adapter.ArtistSearchAdapter;
import com.hani.ticketmerge.adapter.VerticalAdapter;
import com.hani.ticketmerge.api.ConcertApi;
import com.hani.ticketmerge.api.NetworkClient;
import com.hani.ticketmerge.model.ArtistLike;
import com.hani.ticketmerge.model.Concert;
import com.hani.ticketmerge.model.ConcertRes;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchFragment extends Fragment {


    private EditText editText;
    private RecyclerView recyclerView;
    private ImageView imgSearchView;
    private ImageView imgTextView;
    private String keyword;
    private TextView txtComment;
    private RecyclerView artistRecyclerView;
    private ImageView imageView;
    private VerticalAdapter verticalAdapter;
    private ArtistSearchAdapter artistAdapter;
    private ArrayList<ArtistLike> artistArrayList = new ArrayList<>();
    private List<Concert> verticalConcertList = new ArrayList<>();
    private File photoFile;
    Retrofit retrofit;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(VerticalAdapter adapter) {
        SearchFragment fragment = new SearchFragment();
        fragment.verticalAdapter = adapter;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        editText = rootView.findViewById(R.id.editText);
        imgSearchView = rootView.findViewById(R.id.imgSearchView);
        imgTextView = rootView.findViewById(R.id.imgTextView);
        recyclerView = rootView.findViewById(R.id.verticalRecyclerView);
        artistRecyclerView = rootView.findViewById(R.id.artistRecyclerView);
        txtComment = rootView.findViewById(R.id.txtTitle);
        imageView = rootView.findViewById(R.id.imageView3);
        imageView.setVisibility(View.GONE);

        retrofit = NetworkClient.getRetrofitClient(getContext());

        imgSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
                showDialog();
                resizeImageView(100, 100);
            }
        });

        imgTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
                keyword = editText.getText().toString().trim();
                if (keyword.isEmpty()) {
                    Snackbar.make(imgTextView, "검색어를 입력하세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                getNetworkData();
            }
        });

        // RecyclerView와 VerticalAdapter 연결
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        verticalAdapter = new VerticalAdapter(getContext(), verticalConcertList); // 어댑터 초기화
        recyclerView.setAdapter(verticalAdapter);

        // RecyclerView와 ArtistAdapter 연결
        artistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        artistAdapter = new ArtistSearchAdapter(artistArrayList, retrofit);
        artistRecyclerView.setAdapter(artistAdapter);

        return rootView;
    }

    private void resizeImageView(int width, int height) {
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = dpToPx(width);
        params.height = dpToPx(height);
        imageView.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void getNetworkData() {
        // 데이터 초기화
        verticalConcertList.clear();
        artistArrayList.clear();
        verticalAdapter.notifyDataSetChanged();
        artistAdapter.notifyDataSetChanged();


        ConcertApi concertApi = NetworkClient.getRetrofitClient(getContext()).create(ConcertApi.class);
        Call<ConcertRes> call = concertApi.getTextSearchList(0, 30, keyword);
        call.enqueue(new Callback<ConcertRes>() {
            @Override
            public void onResponse(Call<ConcertRes> call, Response<ConcertRes> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ConcertRes concertRes = response.body();
                    if (concertRes.getResult().equals("success")) {
                        List<Concert> concerts = concertRes.getItems();
                        List<ArtistLike> artists = concertRes.getArtist();
                        txtComment.setText(concertRes.comment);

                        if(concerts != null && ! concerts.isEmpty()){
                            verticalConcertList.addAll(concerts);
                            verticalAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                        }else {
                            recyclerView.setVisibility(View.GONE);
                        }

                        if (artists != null && !artists.isEmpty()) {
                            artistArrayList.addAll(artists);
                            artistAdapter.notifyDataSetChanged();
                            artistRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            artistRecyclerView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ConcertRes> call, Throwable t) {

                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.alert_title);
        builder.setItems(R.array.alert_photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    // 카메라로 사진 찍기
                    camera();

                } else if (i == 1) {
                    // 앨범에서 사진 선택
                    album();
                }
            }
        });
        builder.show();
    }

    private void camera() {
        int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 1000);
            Toast.makeText(requireActivity(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            photoFile = getPhotoFile(fileName);

            Uri fileProvider = FileProvider.getUriForFile(requireActivity(), "com.hani.ticketmerge.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(requireActivity(), "이 폰에는 카메라 앱이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void album() {
        if (checkPermission()) {
            displayFileChoose();
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
    }

    private void displayFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지 선택"), 300);
    }

    private File getPhotoFile(String fileName) {
        File storageDirectory = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireActivity(), "카메라 권한이 허가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 500:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireActivity(), "저장소 권한이 허가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), "저장소 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == requireActivity().RESULT_OK) {
            // 카메라로 촬영한 이미지 처리
            handleCameraPhoto();
        } else if (requestCode == 300 && resultCode == requireActivity().RESULT_OK && data != null) {
            // 갤러리에서 선택한 이미지 처리
            handleAlbumPhoto(data.getData());
        }
    }

    private void handleCameraPhoto() {
        Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        rotateImageIfNeeded(photoFile.getAbsolutePath(), photo);
        uploadImageToServer(photoFile);
        imageView.setImageBitmap(photo);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void handleAlbumPhoto(Uri albumUri) {
        String fileName = getFileName(albumUri);
        try {
            ParcelFileDescriptor parcelFileDescriptor = requireActivity().getContentResolver().openFileDescriptor(albumUri, "r");
            if (parcelFileDescriptor == null) return;
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            photoFile = new File(requireActivity().getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        rotateImageIfNeeded(photoFile.getAbsolutePath(), photo);
        uploadImageToServer(photoFile);
        imageView.setImageBitmap(photo);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private void rotateImageIfNeeded(String photoPath, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(photoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap rotatedBitmap = rotateBitmap(bitmap, orientation);
            OutputStream os = new FileOutputStream(photoFile);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadImageToServer(File imageFile) {
        imageView.setVisibility(View.VISIBLE);

//        verticalConcertList.clear();
//        artistArrayList.clear();
        verticalAdapter.notifyDataSetChanged();
        artistAdapter.notifyDataSetChanged();
        ConcertApi concertApi = NetworkClient.getRetrofitClient(getContext()).create(ConcertApi.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", imageFile.getName(), requestBody);
        Call<ConcertRes> call = concertApi.getImageSearchList(photoPart);
        call.enqueue(new Callback<ConcertRes>() {
            @Override
            public void onResponse(Call<ConcertRes> call, Response<ConcertRes> response) {

                if(response.isSuccessful() && response.body() != null){
                    ConcertRes concertRes = response.body();;
                    if(concertRes.getResult().equals("success")){
                        List<Concert> concerts = concertRes.getItems();
                        List<ArtistLike> artists = concertRes.getArtist();
                        verticalConcertList.clear(); // 네트워크 요청 전에 기존 데이터를 초기화
                        artistArrayList.clear(); // 네트워크 요청 전에 기존 데이터를 초기화
                        txtComment.setText(concertRes.comment);

                        if(artists != null && !artists.isEmpty()){
                            artistArrayList.addAll(artists);
                            artistAdapter.notifyDataSetChanged();
                            verticalAdapter.notifyDataSetChanged();
                            artistRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            verticalAdapter.notifyDataSetChanged();
                            artistAdapter.notifyDataSetChanged();
                            artistRecyclerView.setVisibility(View.GONE);
                        }

                        if(concerts != null && ! concerts.isEmpty()){
                            verticalConcertList.addAll(concerts);
                            verticalAdapter.notifyDataSetChanged();
                            artistAdapter.notifyDataSetChanged();

                            recyclerView.setVisibility(View.VISIBLE);
                        }else{
                            verticalAdapter.notifyDataSetChanged();
                            artistAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.GONE);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ConcertRes> call, Throwable t) {
                // API 호출 실패 처리
            }
        });
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setScale(1, -1);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotatedBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (photoFile != null) {
            getNetworkData();
            uploadImageToServer(photoFile);
        }
        verticalAdapter.notifyDataSetChanged();
        artistAdapter.notifyDataSetChanged();
    }
}

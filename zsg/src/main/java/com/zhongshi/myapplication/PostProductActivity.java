package com.zhongshi.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.zhongshi.myapplication.model.ApiResponse;
import com.zhongshi.myapplication.model.Product;
import com.zhongshi.myapplication.network.ApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostProductActivity extends AppCompatActivity {

    private ImageView ivProduct;
    private EditText etTitle, etPrice, etCondition, etStory;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) openCamera();
                else Toast.makeText(this, "需要相机权限才能拍照", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && selectedImageUri != null) {
                    ivProduct.setImageURI(selectedImageUri);
                }
            });

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProduct.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);

        ivProduct = findViewById(R.id.ivProduct);
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etCondition = findViewById(R.id.etCondition);
        etStory = findViewById(R.id.etStory);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnPickImage = findViewById(R.id.btnPickImage);
        Button btnPublish = findViewById(R.id.btnPublish);

        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndOpen());
        btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnPublish.setOnClickListener(v -> submitProduct());
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File dir = new File(getCacheDir(), "images");
            if (!dir.exists()) dir.mkdirs();
            File imageFile = new File(dir, "IMG_" + System.currentTimeMillis() + ".jpg");
            selectedImageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
            takePictureLauncher.launch(selectedImageUri);
        } catch (Exception e) {
            Toast.makeText(this, "打开相机失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void submitProduct() {
        String title = etTitle.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String condition = etCondition.getText().toString().trim();
        String story = etStory.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty() || story.isEmpty()) {
            Toast.makeText(this, "请填写标题、价格和故事", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody titleBody = toTextBody(title);
        RequestBody priceBody = toTextBody(price);
        RequestBody conditionBody = toTextBody(condition);
        RequestBody storyBody = toTextBody(story);
        RequestBody sellerBody = toTextBody("游客用户");

        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            File imageFile = copyUriToTempFile(selectedImageUri);
            if (imageFile != null) {
                RequestBody fileBody = RequestBody.create(imageFile, MediaType.parse("image/*"));
                imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), fileBody);
            }
        }

        ApiClient.getApiService()
                .createProduct(titleBody, priceBody, conditionBody, storyBody, sellerBody, imagePart)
                .enqueue(new Callback<ApiResponse<Product>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(PostProductActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PostProductActivity.this, "发布失败，请检查后端接口", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                        Toast.makeText(PostProductActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private RequestBody toTextBody(String value) {
        return RequestBody.create(value, MediaType.parse("text/plain"));
    }

    private File copyUriToTempFile(Uri uri) {
        try {
            File file = new File(getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int len;
            while (inputStream != null && (len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            if (inputStream != null) inputStream.close();
            outputStream.close();
            return file;
        } catch (Exception e) {
            Toast.makeText(this, "图片读取失败", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}

package com.zhongshi.myapplication.network;

import com.zhongshi.myapplication.model.ApiResponse;
import com.zhongshi.myapplication.model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @GET("products")
    Call<ApiResponse<List<Product>>> getProducts();

    @Multipart
    @POST("products")
    Call<ApiResponse<Product>> createProduct(
            @Part("title") RequestBody title,
            @Part("price") RequestBody price,
            @Part("conditionTag") RequestBody conditionTag,
            @Part("story") RequestBody story,
            @Part("sellerNickname") RequestBody sellerNickname,
            @Part MultipartBody.Part image
    );
}

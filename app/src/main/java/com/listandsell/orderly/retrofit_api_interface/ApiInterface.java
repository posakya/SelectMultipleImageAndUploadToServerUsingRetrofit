package com.listandsell.orderly.retrofit_api_interface;


import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by lokesh on 6/1/18.
 */

public interface ApiInterface {

    ///// login api interface
    @POST("uploadSubmit")
    Call<ResponseBody> submitData(@Body RequestBody body);


}

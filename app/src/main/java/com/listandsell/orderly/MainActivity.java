package com.listandsell.orderly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.listandsell.orderly.retrofit_api_client.RetrofitClient;
import com.listandsell.orderly.retrofit_api_interface.ApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> filePaths=new ArrayList<String>();

    GridView gv;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gv= findViewById(R.id.gv);

        FloatingActionButton fab = findViewById(R.id.fab);

        ////// click to open gallery
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // filePaths.clear();
                filePaths.iterator();
                FilePickerBuilder.getInstance().setMaxCount(5)
                        .setSelectedFiles(filePaths)
                        .setActivityTheme(R.style.AppTheme)
                        .pickPhoto(MainActivity.this);
            }

        });
    }



    ///// get image from gallery and display in grid view

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE:

                if(resultCode==RESULT_OK && data!=null)
                {

                    filePaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS);
                    ModelClass s;
                    ArrayList<ModelClass> modelClasses =new ArrayList<>();
                    try
                    {
                        for (String path:filePaths) {
                            s=new ModelClass();
                            s.setName(path.substring(path.lastIndexOf("/")+1));

                            file = new File(path);
                            s.setUri(Uri.fromFile(file));

                            modelClasses.add(s);
                        }


                        CustomAdapter customAdapter = new CustomAdapter(this, modelClasses);
                        gv.setAdapter(customAdapter);

                        Toast.makeText(MainActivity.this, "Total = "+String.valueOf(modelClasses.size()), Toast.LENGTH_SHORT).show();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
        }
    }

    ////// click to submit the array of images and product_id to server

    public void Submit(View view) {
        for (String file1:filePaths){
            postData("2", new File(file1));
        }

    }


    /////////// post data to server using retrofit

    public void postData(String product_id,File image){

        MediaType MEDIA_TYPE_PNG;
        MEDIA_TYPE_PNG = image.getName().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");

        ApiInterface postInterface = RetrofitClient.getFormData().create(ApiInterface.class);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("product_id",product_id)
                .addFormDataPart("image[]", image.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, image))
                .build();

        postInterface.submitData(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                /////// validation with json response /////

                if(response.isSuccessful()) {

                    ResponseBody responseBody =response.body();

                    String responseBodyString= null;
                    try {
                        responseBodyString = responseBody.string();
                        Log.d("Response body", responseBodyString);

                        try {
                            JSONObject jsonObjet = new JSONObject(responseBodyString);
                            String code = jsonObjet.optString("status");
                            String message = jsonObjet.optString("message");

                            if (code.equals("0")){

                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            else if (code.equals("1")) {


                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else  {
                    Log.d("Response errorBody", String.valueOf(response.errorBody()));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("Error : "+t.getMessage());
            }
        });
    }



    /////// create grid view custom adapter

    public class CustomAdapter extends BaseAdapter {

        Context c;
        ArrayList<ModelClass> modelClasses;

        public CustomAdapter(Context c, ArrayList<ModelClass> modelClasses) {
            this.c = c;
            this.modelClasses = modelClasses;
        }

        @Override
        public int getCount() {
            return modelClasses.size();
        }

        @Override
        public Object getItem(int i) {
            return modelClasses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                //INFLATE CUSTOM LAYOUT
                view = LayoutInflater.from(c).inflate(R.layout.model, viewGroup, false);
            }

            final ModelClass s = (ModelClass) this.getItem(i);

            TextView nameTxt = (TextView) view.findViewById(R.id.nameTxt);
            ImageView img = (ImageView) view.findViewById(R.id.spacecraftImg);
            TextView cancel = view.findViewById(R.id.cancel);

            //BIND DATA
            nameTxt.setText(s.getName());
            Picasso.with(c).load(s.getUri()).placeholder(R.drawable.ic_camera).into(img);


            //////// set the position
            cancel.setTag(i);

            ///// click to remove item from gridview and arraylist
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int positionToRemove = (int)view.getTag(); //get the position of the view to delete stored in the tag
                    modelClasses.remove(positionToRemove);
                    filePaths.remove(positionToRemove);
                    notifyDataSetChanged();
                    Toast.makeText(c, s.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }
    }

}

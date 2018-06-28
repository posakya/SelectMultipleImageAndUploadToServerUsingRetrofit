# SelectMultipleImageAndUploadToServerUsingRetrofit


////// required dependencies


    implementation 'com.android.support:cardview-v7:27.+'
    implementation 'com.squareup.picasso:picasso:2.5.2'
    implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
    implementation 'com.squareup.retrofit2:retrofit:2.4.0'
    
    /////// library for multiple image select
    implementation 'com.droidninja:filepicker:1.0.0'
  



///// laravel method to upload multiple images  /////////

 



public function uploadSubmit(Request $request)
    {
    
        $product_id = $request->input('product_id');
      
        if($files=$request->file('image')){

            foreach($files as $file){
                $destinationPath = public_path(). '/image/';
                $name=$file->getClientOriginalName();
                $image1 = time().$name;
                
                $file->move($destinationPath,$image1);

                $data= Image::create( [
                     'product_id'=>  $product_id, 
                     'image'=> url('/image/').'/'.$image1

                ]);

            }

            return response()->json(['status'=>'1',"message"=>'SuccessFully Posted']);

        }
     }

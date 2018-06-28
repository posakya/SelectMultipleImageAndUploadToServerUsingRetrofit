# SelectMultipleImageAndUploadToServerUsingRetrofit

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

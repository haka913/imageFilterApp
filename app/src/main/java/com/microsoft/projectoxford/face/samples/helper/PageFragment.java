package com.microsoft.projectoxford.face.samples.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.microsoft.projectoxford.face.samples.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by paul on 2018-05-27.
 */

public class PageFragment extends Fragment {
    private int imageResource;
    private Bitmap bitmap;

    public static PageFragment getInstance(int resourceID) {
        PageFragment f = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("image_source", resourceID);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageResource = getArguments().getInt("image_source");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

//    private byte[] bitmapToByte(Bitmap bitmap){
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        return byteArray;
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.images);

        byte[] bytt = ((filter)getActivity()).getbytesAsBitmap();
        bitmap = BitmapFactory.decodeByteArray(bytt,0,bytt.length);

        if(imageResource==1){
            //bitmap = ((MainActivity)getActivity()).getBaseBitmap();
        }
        else if(imageResource==2){
            bitmap = filter.toGrayscale(bitmap);
        }
        else if(imageResource==3){
            bitmap = filter.toBinary(bitmap);
        }
        else if(imageResource==4){
            bitmap = filter.toSepia2(bitmap);
        }
        else if(imageResource==5){
            bitmap = filter.toVintage(bitmap);
        }
        else if(imageResource==6){
            bitmap = filter.toWorn(bitmap);
        }
        else if(imageResource==7){
            bitmap = filter.toStark(bitmap);
        }
        else if(imageResource==8){
            bitmap = filter.toSunnyside(bitmap);
        }
        else if(imageResource==9){
            bitmap = filter.toSetPopArtGradientFromBitmap(bitmap);
        }
        else if(imageResource==10){
            bitmap = filter.toApplyReflection(bitmap);
        }
        else if(imageResource==11){
            bitmap = filter.toApplyGaussianBlur(bitmap);
        }
        else if(imageResource==12){
            bitmap = filter.toMoreRed(bitmap);
        }
        else if(imageResource==13){
            bitmap = filter.toMoreYello(bitmap);
        }
        else if(imageResource==14){
            bitmap = filter.toTintImage(bitmap,50);
        }
        else if(imageResource==15){
            bitmap = filter.toOnlyblue(bitmap);
        }
        else if(imageResource==16){
            bitmap = filter.toOnlyred(bitmap);
        }

        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
        bitmap = null;
    }


}

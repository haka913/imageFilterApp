package com.microsoft.projectoxford.face.samples.helper;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.microsoft.projectoxford.face.samples.R;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.microsoft.projectoxford.face.samples.helper.ViewPagerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class filter extends AppCompatActivity {
    public static final double PI = 3.14159d;
    public static final double FULL_CIRCLE_DEGREE = 360d;
    public static final double HALF_CIRCLE_DEGREE = 180d;
    public static final double RANGE = 256d;


    private ArrayList<Integer> images;
    private BitmapFactory.Options options;
    private ViewPager viewPager;
    private View btnNext, btnPrev;
    private FragmentStatePagerAdapter adapter;
    private LinearLayout thumbnailsContainer;

    private Bitmap afterBitmap;
    private Paint paint;
    private Canvas canvas;
    private Bitmap baseBitmap;
    private byte[] bytes;

    Drawable drawable;


    /*private final static int[] resourceIDs = new int[]{ R.drawable.a, R.drawable.b,
            R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g};*/

    private final static int[] resourceIDs = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    private final static String[] resourceID = new String[]{"original", "gray", "binary" , "sepia", "toVintage", "toWorn"};

    private static final int REQUEST_PERMISSION_CODE = 200;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    String emotion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        images = new ArrayList<>();

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        Intent intent = getIntent();
        bytes = intent.getByteArrayExtra("image");
        baseBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        emotion = intent.getStringExtra("emotion");

        //find view by id
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        thumbnailsContainer = (LinearLayout) findViewById(R.id.container);
        btnNext = findViewById(R.id.next);
        btnPrev = findViewById(R.id.prev);

        btnPrev.setOnClickListener(onClickListener(0));
        btnNext.setOnClickListener(onClickListener(1));

        setImagesData();

        // init viewpager adapter and attach
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), images);
        viewPager.setAdapter(adapter);

        inflateThumbnails();
    }

    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public Bitmap getBaseBitmap(){
        return baseBitmap;
    }

    public byte[] getbytesAsBitmap(){
        return bytes;
    }
//    private void printKeyHash(){
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.pusan.cse.imagefilternew", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.action_save:
                saveImage();
                return true;
            case R.id.action_share:
                shareImage();
//                Toast.makeText(this,"share to facebook", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String getDateString(){
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.KOREA);
        String dateStr = df.format(new Date());
        return dateStr;
    }
    private void saveImage(){
        requestPermission();
        ImageView imageView = (ImageView)findViewById(R.id.images);

        Bitmap saveImg = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//        BitmapDrawable d = (BitmapDrawable)  getResources().getDrawable(R.drawable.a);
//        Bitmap saveImg = d.getBitmap();

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        String dateStr = getDateString();
        String fileName = path+"/"+dateStr+".jpg";
        File saveFile = new File(fileName);

        FileOutputStream out = null;
        try {
            saveFile.createNewFile();
            out = new FileOutputStream(saveFile);
            saveImg.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
            Toast.makeText(this,"파일 저장성공\n"+fileName ,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"파일 저장실패\n"+fileName,Toast.LENGTH_LONG).show();
        }
    }

    //manifest에 저장을 허용한다고 해도 안될 시 앱 자제에서 허용을 request를 해야한다
    private void requestPermission() {
        ActivityCompat.requestPermissions(filter.this,new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    ///////////////share
    private void shareImage(){
        ImageView imageView = (ImageView)findViewById(R.id.images);

        Bitmap shareImg = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        SharePhoto photo = new SharePhoto.Builder()
                .setUserGenerated(true)
                .setBitmap(shareImg)
                .setCaption("test")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .setShareHashtag(new ShareHashtag.Builder()
                  .setHashtag("#"+emotion)
                  .build())
                .build();
        if (shareDialog.canShow(SharePhotoContent.class)){
            shareDialog.show(content);
        }
        else{
            Log.d("Activity", "you cannot share photos :(");
        }

//        ShareLinkContent content1 = new ShareLinkContent.Builder()
//                .setContentUrl(bitmapToUriConverter(shareImg))
//                .setShareHashtag(new ShareHashtag.Builder()
//                    .setHashtag("#testhashtag")
//                    .build())
//                .build();
//        //shareDialog.show(content);
//
//
//        if (shareDialog.canShow(ShareLinkContent.class)){
//            shareDialog.show(content1);
//        }
//        else{
//            Log.d("Activity", "you cannot share photos :(");
//        }

        Toast.makeText(this, "call shareImage func", Toast.LENGTH_SHORT).show();

    }
    public Uri bitmapToUriConverter(Bitmap shareImg) {
        Uri uri = null;
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String dateStr = getDateString();
            String fileName = path+"/"+dateStr+".jpg";
            File saveFile = new File(fileName);

            FileOutputStream out = null;

            saveFile.createNewFile();
            out = new FileOutputStream(saveFile);
            shareImg.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();


            //get absolute path
            String realPath = saveFile.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }


    private View.OnClickListener onClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i > 0) {
                    //next page
                    if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1,true);
                    }
                } else {
                    //previous page
                    if (viewPager.getCurrentItem() > 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1,true);
                    }
                }
            }
        };
    }

    private void setImagesData() {
        for (int i = 0; i < resourceIDs.length; i++) {
            images.add(resourceIDs[i]);
        }
    }

    private void inflateThumbnails() {

        for (int i = 0; i < images.size(); i++) {
            View imageLayout = getLayoutInflater().inflate(R.layout.item_image, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img_thumb);
            imageView.setOnClickListener(onChagePageClickListener(i));
            options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            options.inDither = false;

            Bitmap bitma = BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);

            if(images.get(i)==1){
                bitma = bitma;
            }
            else if(images.get(i)==2){
                bitma = toGrayscale(bitma);
            }
            else if(images.get(i)==3){
                bitma = toBinary(bitma);
            }
            else if(images.get(i)==4){
                bitma = toSepia2(bitma);
            }
            else if(images.get(i)==5){
                bitma = toVintage(bitma);
            }
            else if(images.get(i)==6){
                bitma = toWorn(bitma);
            }
            else if(images.get(i)==7){
                bitma = toStark(bitma);
            }
            else if(images.get(i)==8){
                bitma = toSunnyside(bitma);
            }
            else if(images.get(i)==9){
                bitma = toSetPopArtGradientFromBitmap(bitma);
            }
            else if(images.get(i)==10){
                bitma = toApplyReflection(bitma);
            }
            else if(images.get(i)==11){
                bitma = toApplyGaussianBlur(bitma);
            }
            else if(images.get(i)==12){
                bitma = toMoreRed(bitma);
            }
            else if(images.get(i)==13){
                bitma = toMoreYello(bitma);
            }
            else if(images.get(i)==14){
                bitma = toTintImage(bitma,50);
            }
            else if(images.get(i)==15){
                bitma = toOnlyblue(bitma);
            }
            else if(images.get(i)==16){
                bitma = toOnlyred(bitma);
            }
            //set to image view
            imageView.setImageBitmap(bitma);
            //add imageview
            thumbnailsContainer.addView(imageLayout);
        }
    }

    private View.OnClickListener onChagePageClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(i);
            }
        };
    }


////////////////////filter 코드
    //gray-filter
    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    //toBinary
    public static Bitmap toBinary(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                54.315f, 182.325f, 18.359999f, 1, -32640,
                54.315f, 182.325f, 18.359999f, 1, -32640,
                54.315f, 182.325f, 18.359999f, 1, -32640,
                0, 0, 0, 1, 0 });
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;

    }


    //sepia2-filter
    public static Bitmap toSepia2(Bitmap bmpOriginal)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ColorMatrix cm = new ColorMatrix(
                new float[] { 0.3930000066757202f, 0.7689999938011169f,
                        0.1889999955892563f, 0, 0, 0.3490000069141388f,
                        0.6859999895095825f, 0.1679999977350235f, 0, 0,
                        0.2720000147819519f, 0.5339999794960022f,
                        0.1309999972581863f, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 });

        ColorMatrixColorFilter sepia = new ColorMatrixColorFilter(cm);

        paint.setColorFilter(sepia);
        canvas.drawBitmap(bmpOriginal, 0,0, paint);

        return bitmap;
    }


    //toVintage
    public static Bitmap toVintage(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                1, 0, 0, 0, -60,
                0, 1, 0, 0, -60,
                0, 0, 1, 0, -60,
                0, 0, 0, 1, 0 });
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }

    //toWorn
    public static Bitmap toWorn(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                1, 0, 0, 0, -60,
                0, 1, 0, 0, -60,
                0, 0, 1, 0, -90,
                0, 0, 0, 1, 0 });
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }

    //toStark
    public static Bitmap toStark(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);



        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                1, 0, 0, 0, -90,
                0, 1, 0, 0, -90,
                0, 0, 1, 0, -90,
                0, 0, 0, 1, 0 });
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }

    //toSunnyside
    public static Bitmap toSunnyside(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                1, 0, 0, 0, 10,
                0, 1, 0, 0, 10,
                0, 0, 1, 0, -60,
                0, 0, 0, 1, 0 });
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }

    //gradiation
    public static Bitmap toSetPopArtGradientFromBitmap(Bitmap bmp) {
        //순서대로 위에서 부터 아래로의 색깔 지정
        int[] co = new int[]{Color.parseColor("#FFD900"),Color.parseColor("#FF5300"),
                Color.parseColor("#FF0D00"),Color.parseColor("#AD009F"),Color.parseColor("#1924B1")};
        float[] coP = new float[]{0.2f,0.4f,0.6f,0.8f,1.0f};

        Bitmap bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

    /* Create your gradient. */
        LinearGradient grad = new LinearGradient(0, 0, 0, canvas.getHeight(), co, coP, Shader.TileMode.CLAMP);

    /* Draw your gradient to the top of your bitmap. */
        Paint p = new Paint();
        p.setAlpha(110);
        p.setShader(grad);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p);
        //canvas.drawBitmap(bitmap,canvas.getWidth(),canvas.getHeight(),p);


        return bitmap;
    }

    ////////mirrormode-filter
    public static Bitmap toApplyReflection(Bitmap originalImage) {
        // gap space between original and reflected
        final int reflectionGap = 4;
        // get image size
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // this will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // create a Bitmap with the flip matrix applied to it.
        // we only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);

        // create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Bitmap.Config.ARGB_8888);

        // create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // draw in the reflection
        canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);

        // create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);
        // set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    //gaussianBlur-filter
    public static Bitmap toApplyGaussianBlur(Bitmap src) {
        double[][] GaussianBlurConfig = new double[][] {
                { 1, 2, 1 },
                { 2, 4, 2 },
                { 1, 2, 1 }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(GaussianBlurConfig);
        convMatrix.Factor = 16;
        convMatrix.Offset = 0;
//        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
        src = ConvolutionMatrix.computeConvolution3x3(src,convMatrix);
        return src;
    }


    //toMoreRed
    public static Bitmap toMoreRed(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                0.8745f, 0.4f, 0.4f, 0,0,
                0.2f, 0.4f, 0.7490f, 0, 0,
                0.2f, 0.4f, 0.7020f, 0, 0,
                0, 0, 0, 1, 0,});
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }

    //toMoreYello
    public static Bitmap toMoreYello(Bitmap bmpOriginal)
    {
        int[] co = new int[]{Color.parseColor("#b77d21"),Color.parseColor("#382c34")};
        float[] coP = new float[]{0.2f,0.4f};

        Bitmap bitmap = bmpOriginal.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

    /* Create gradient. */
        LinearGradient grad = new LinearGradient(0, 0, 0, canvas.getHeight(), co, coP, Shader.TileMode.CLAMP);

    /* Draw gradient to the top of your bitmap. */
        Paint p = new Paint();
        p.setAlpha(110);
        p.setShader(grad);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p);

        return bitmap;
    }


    //toOnlyblue
    public static Bitmap toOnlyblue(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                -0.41f, 0.539f, 0.873f, 0, 0,
                0.452f, 0.666f, -0.11f,0, 0,
                -0.3f, 1.71f, -0.4f, 0, 0,
                0,     0,     0,     1, 0 });
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }
    //toOnlyred
    public static Bitmap toOnlyred(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] {
                -0.36f, 1.691f, -0.32f, 0, 0,
                0.325f, 0.398f, 0.275f,0, 0,
                0.79f, 0.796f, -0.76f, 0, 0,
                0,     0,     0,     1, 0 });
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bitmap;
    }
    /////////////tint filter
    public static Bitmap toTintImage(Bitmap src, int degree) {

        int width = src.getWidth();
        int height = src.getHeight();

        int[] pix = new int[width * height];
        src.getPixels(pix, 0, width, 0, 0, width, height);

        int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;

        int S = (int)(RANGE * Math.sin(angle));
        int C = (int)(RANGE * Math.cos(angle));

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = ( pix[index] >> 16 ) & 0xff;
                int g = ( pix[index] >> 8 ) & 0xff;
                int b = pix[index] & 0xff;
                RY = ( 70 * r - 59 * g - 11 * b ) / 100;
                GY = (-30 * r + 41 * g - 11 * b ) / 100;
                BY = (-30 * r - 59 * g + 89 * b ) / 100;
                Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
                RYY = ( S * BY + C * RY ) / 256;
                BYY = ( C * BY - S * RY ) / 256;
                GYY = (-51 * RYY - 19 * BYY ) / 100;
                R = Y + RYY;
                R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
                G = Y + GYY;
                G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
                B = Y + BYY;
                B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
                pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
            }

        Bitmap bitmap = Bitmap.createBitmap(width, height, src.getConfig());
        bitmap.setPixels(pix, 0, width, 0, 0, width, height);

        pix = null;

        return bitmap;
    }

}
class ConvolutionMatrix
{
    public static final int SIZE = 3;

    public double[][] Matrix;
    public double Factor = 1;
    public double Offset = 1;

    public ConvolutionMatrix(int size) {
        Matrix = new double[size][size];
    }

    public void setAll(double value) {
        for (int x = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                Matrix[x][y] = value;
            }
        }
    }

    public void applyConfig(double[][] config) {
        for(int x = 0; x < SIZE; ++x) {
            for(int y = 0; y < SIZE; ++y) {
                Matrix[x][y] = config[x][y];
            }
        }
    }

    public static Bitmap computeConvolution3x3(Bitmap src, ConvolutionMatrix matrix) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int sumR, sumG, sumB;
        int[][] pixels = new int[SIZE][SIZE];

        for(int y = 0; y < height - 2; ++y) {
            for(int x = 0; x < width - 2; ++x) {

                // get pixel matrix
                for(int i = 0; i < SIZE; ++i) {
                    for(int j = 0; j < SIZE; ++j) {
                        pixels[i][j] = src.getPixel(x + i, y + j);
                    }
                }

                // get alpha of center pixel
                A = Color.alpha(pixels[1][1]);

                // init color sum
                sumR = sumG = sumB = 0;

                // get sum of RGB on matrix
                for(int i = 0; i < SIZE; ++i) {
                    for(int j = 0; j < SIZE; ++j) {
                        sumR += (Color.red(pixels[i][j]) * matrix.Matrix[i][j]);
                        sumG += (Color.green(pixels[i][j]) * matrix.Matrix[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * matrix.Matrix[i][j]);
                    }
                }

                // get final Red
                R = (int)(sumR / matrix.Factor + matrix.Offset);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                // get final Green
                G = (int)(sumG / matrix.Factor + matrix.Offset);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                // get final Blue
                B = (int)(sumB / matrix.Factor + matrix.Offset);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // apply new pixel
                result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }

        // final image
        return result;
    }
}
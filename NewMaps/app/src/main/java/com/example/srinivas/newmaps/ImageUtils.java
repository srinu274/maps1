package com.example.srinivas.newmaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

/**
 * Created by Srinivas on 03-07-2016.
 */
public class ImageUtils {

    public static Bitmap getRoundedBitmap(Bitmap bitmap,int pixels) {
        Bitmap output=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(output);
        final int color=0xff424242;
        final Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        final RectF rectF=new RectF(rect);
        final Paint paint=new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF,pixels,pixels,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,rect,rect,paint);
        return output;
    }

    public static Bitmap getBitmap(String str){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap= BitmapFactory.decodeFile(str,options);
        return bitmap;
    }

    public static Bitmap getRoundedBitmap(String str) {
        Bitmap bitmap=getBitmap(str);
        if(bitmap==null) return null;
        int pixels=bitmap.getHeight()>bitmap.getWidth()?bitmap.getHeight():bitmap.getWidth();
        return getRoundedBitmap(bitmap,pixels);
    }
}

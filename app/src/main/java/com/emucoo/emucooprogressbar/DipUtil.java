package com.emucoo.emucooprogressbar;

import android.util.TypedValue;

public class DipUtil {
    public static int screenWidth(){

        return App.getInstance().getResources().getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(){

        return App.getInstance().getResources().getDisplayMetrics().heightPixels;
    }



    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px( float dpValue) {

        final float scale = App.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip( float pxValue) {
        final float scale = App.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);  
    }

    public static int convertSpToPixels(float sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, App.getInstance().getResources().getDisplayMetrics());
        return px;
    }
}  
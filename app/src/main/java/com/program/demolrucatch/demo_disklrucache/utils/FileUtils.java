package com.program.demolrucatch.demo_disklrucache.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.EventListener;

public class FileUtils {


    /**
     * 得到本地缓存的路径
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskLruCacheDir(Context context ,String uniqueName){
        String cachePath;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())||!Environment.isExternalStorageRemovable()){
            cachePath=context.getExternalCacheDir().getPath();
        }else{
            cachePath=context.getCacheDir().getPath();
        }
        return new File(cachePath+File.separator+uniqueName);
    }




}

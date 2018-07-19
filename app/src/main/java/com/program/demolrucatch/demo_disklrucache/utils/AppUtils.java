package com.program.demolrucatch.demo_disklrucache.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppUtils {


    //得到app版本号
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo= context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}

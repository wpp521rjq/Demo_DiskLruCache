package com.program.demolrucatch.demo_disklrucache;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.FileUriExposedException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.program.demolrucatch.demo_disklrucache.utils.AppUtils;
import com.program.demolrucatch.demo_disklrucache.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    public final int WRITE_EXTERNAL_STORAGE = 4;//读写权限
    public final int READ_EXTERNAL_STORAGE = 5;//

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.img_main_disklrycache);
        requestPremission();
        //进行从中取出来
        getCache();
    }



    public Bitmap getCache(){


        Bitmap bitmap=null;
        String imageUrl="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531999149985&di=fa5502c9198f8c881f2ddf5dd205e23b&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F09fa513d269759eeef490028befb43166d22df3c.jpg";
        String key = MD5CacheKeyForDisk(imageUrl);
        try {
            DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
            //
            if(snapShot!=null){
                InputStream inputStream=snapShot.getInputStream(0);
                bitmap= BitmapFactory.decodeStream(inputStream);

                final Bitmap finalBitmap = bitmap;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        imageView.setImageBitmap(finalBitmap);
                    }
                });

                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }




    DiskLruCache mDiskLruCache=null;
    public DiskLruCache getDiskLruCache(){
        File cacheDir= FileUtils.getDiskLruCacheDir(this,"bitmap");
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        try {
            mDiskLruCache=DiskLruCache.open(cacheDir, AppUtils.getAppVersion(this),1,10*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mDiskLruCache;
    }



    public void flushDiskLruCache()
    {
        try {
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 从url下载stream
     * @param url
     * @param outputStream
     * @return
     */
    public boolean downLoadFromUrlToStrean(String url, OutputStream outputStream){
        HttpURLConnection mHttpURLConnection=null;
        BufferedInputStream mBufferedInputStream=null;
        BufferedOutputStream mBufferedOutputStream=null;
            try {
                URL url1=new URL(url);
                mHttpURLConnection= (HttpURLConnection) url1.openConnection();
                mBufferedInputStream=new BufferedInputStream(mHttpURLConnection.getInputStream(),8*1024);
                mBufferedOutputStream=new BufferedOutputStream(outputStream,8*1024);
                //进行循环写入
                int b;
                while ((b=mBufferedInputStream.read())!=-1){
                    mBufferedOutputStream.write(b);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(mHttpURLConnection!=null){
                    mHttpURLConnection.disconnect();
                }
                if(mBufferedOutputStream!=null){
                    try {
                        mBufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(mBufferedInputStream!=null){
                    try {
                        mBufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
    }



    public String MD5CacheKeyForDisk(String key){
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();

    }






    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        requestHttp();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                } else {
//                    ToastUtils.show("权限已被拒绝");
                }
                break;
            case READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestHttp();
                } else {
//                    ToastUtils.show("权限已被拒绝");
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPremission() {
        //进入室内的时候进行权限申请
            //申请读写权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //申请读取权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    requestHttp();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            }
        }

    /**
     * 申请权限之后获取数据将数据写入
     */
    private void requestHttp() {
        getDiskLruCache();
        //再子线程里面进行写入
        new Thread(new Runnable() {
            @Override
            public void run() {
                String imageUrl="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531999149985&di=fa5502c9198f8c881f2ddf5dd205e23b&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F09fa513d269759eeef490028befb43166d22df3c.jpg";
                String key=MD5CacheKeyForDisk(imageUrl);
                try {
                    DiskLruCache.Editor editor=mDiskLruCache.edit(key);
                    //将其写入
                    if(editor!=null){
                        OutputStream outputStream=editor.newOutputStream(0);
                        if(downLoadFromUrlToStrean(key,outputStream)){
                            editor.commit();
                        }else{
                            editor.abort();//终止
                        }
                    }
                    flushDiskLruCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



    public void removeCache(){
        String imageUrl="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531999149985&di=fa5502c9198f8c881f2ddf5dd205e23b&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F09fa513d269759eeef490028befb43166d22df3c.jpg";
        String key = MD5CacheKeyForDisk(imageUrl);
        try {
            mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}

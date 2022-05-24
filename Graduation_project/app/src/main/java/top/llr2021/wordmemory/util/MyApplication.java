package top.llr2021.wordmemory.util;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import top.llr2021.wordmemory.qqLogin.QQLoginHelper;
import top.llr2021.wordmemory.config.ConfigData;

import org.litepal.LitePal;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);
        QQLoginHelper.getInstance().setAppID("101975102")
                .setPackageName("top.llr2021.wordmemory")
                .init();

        if (ConfigData.getIsNight()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
     }

    }


    public static Context getContext() {
        return context;
    }

}

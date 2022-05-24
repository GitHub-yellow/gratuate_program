package top.llr2021.wordmemory.qqLogin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import top.llr2021.wordmemory.util.MyApplication;

public class QQLoginHelper {

    private Tencent mTencent;
    private QQAuthorListener mAuthorListener;
    private OnLoginListener mOnLoginListener;
    private String mAppId;
    private String mPackageName;

    private QQLoginHelper() {
    }

    public static class Holder {
        private static QQLoginHelper instance = new QQLoginHelper();
    }

    public static QQLoginHelper getInstance() {
        return Holder.instance;
    }

    /***
     * 设置appId
     *
     * @param appId  字符串,在qq互联上建立项目后出现的appid,如：101342775
     *
     * @return
     */
    public QQLoginHelper setAppID(String appId){
        this.mAppId=appId;
        return QQLoginHelper.this;
    }

    /***
     * 设置项目包名
     *
     * @param packageName  eg：com.demo
     * @return
     */
    public QQLoginHelper setPackageName(String packageName){
        this.mPackageName=packageName;
        return QQLoginHelper.this;
    }

    /**初始化**/
    public void init(){
        if(isEmpty(mAppId)){
            throw new NullPointerException("=====请调用setAppID(String appId)设置appId======");
        }
        if(isEmpty(mPackageName)){
            throw new NullPointerException("=====请调用setPackageName(String packageName)设置packageName======");
        }
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        // 其中Authorities为 Manifest文件中注册FileProvider时设置的authorities属性值
        mTencent = Tencent.createInstance(mAppId, MyApplication.getContext(),mPackageName);
    }

    /**登录**/
    public void login(Context context, OnLoginListener listener) {
        this.mOnLoginListener=listener;
        if (context!=null) {
            mAuthorListener=new QQAuthorListener(mTencent,listener);
            //三个参数：第一个是  上下文，第二个是 权限，此处是 “all”，表示所有权限，第三个参数是登陆的回调，我们登陆的结果是在这个类中处理的
            mTencent.login((AppCompatActivity)context, "all", mAuthorListener);
        }
    }

    /**在Activity的onActivityResult方法中调用**/
    public void onActivityResultData(int requestCode, int resultCode, Intent data) {
        if (mTencent != null && mAuthorListener != null) {
            if (requestCode == Constants.REQUEST_LOGIN) {
                mTencent.onActivityResultData(requestCode, resultCode, data, mAuthorListener);
            }
        }
    }

    /**退出登录**/
    public void loginOut(Context context){
        if(mTencent!=null&&context!=null) {
            mTencent.logout(context);
            if(mOnLoginListener!=null){
                mOnLoginListener.loginStatus(QQStatus.LOGIN_OUT_SUCCESS,"退出登录成功",null);
            }
        }
    }

    /**登录是否成功的监听**/
    public interface OnLoginListener{

        void loginStatus(int code, String message, Object obj);
    }


    public static boolean isEmpty(String input) {
        if (input == null || input.trim().length() == 0 || input.equals("null"))
            return true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

}

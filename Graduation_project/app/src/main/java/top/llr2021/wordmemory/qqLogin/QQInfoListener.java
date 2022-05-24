package top.llr2021.wordmemory.qqLogin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQInfoListener implements IUiListener {

    private Tencent mTencent;
    private QQLoginHelper.OnLoginListener mOnLoginListener;
    private Gson mGson;

    public QQInfoListener(Tencent tencent, QQLoginHelper.OnLoginListener listener, Gson gson){
        this.mTencent=tencent;
        this.mOnLoginListener=listener;
        this.mGson=gson;
    }

    @Override
    public void onComplete(Object obj) {
        if(mOnLoginListener!=null){
            if (obj != null) {
                String userString=obj.toString();
                try {
                    LoginInfo loginInfo=mGson.fromJson(userString, LoginInfo.class);
                    loginInfo.setOpenId(mTencent.getOpenId());
                    mOnLoginListener.loginStatus(QQStatus.LOGIN_INFO_SUCCESS,"获取登录信息成功",loginInfo);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    mOnLoginListener.loginStatus(QQStatus.LOGIN_INFO_PARSE_FAILED,"登录信息解析错误",obj);
                }
            } else {
                mOnLoginListener.loginStatus(QQStatus.LOGIN_INFO_PARSE_FAILED_NULL,"登录信息解析失败",null);
            }
        }
    }

    @Override
    public void onError(UiError uiError) {
        if(mOnLoginListener!=null) {
            if(uiError!=null){
                mOnLoginListener.loginStatus(QQStatus.LOGIN_INFO_FAILED,"获取用户登录信息失败",uiError);
            }else{
                mOnLoginListener.loginStatus(QQStatus.LOGIN_INFO_FAILED_NULL,"获取用户登录信息失败",null);
            }
        }
    }

    @Override
    public void onCancel() {
        if (mOnLoginListener != null) {
            mOnLoginListener.loginStatus(QQStatus.LOGIN_INFO_CANCEL,"拉取用户登录信息已被取消",null);
        }
    }

    @Override
    public void onWarning(int i) {

    }
}

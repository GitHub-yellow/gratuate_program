package top.llr2021.wordmemory.activity;

import  android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.llr2021.wordmemory.config.ServerData;
import top.llr2021.wordmemory.qqLogin.LoginInfo;
import top.llr2021.wordmemory.qqLogin.QQLoginHelper;
import top.llr2021.wordmemory.qqLogin.QQStatus;
import top.llr2021.wordmemory.R;
import top.llr2021.wordmemory.config.ConfigData;
import top.llr2021.wordmemory.database.User;
import top.llr2021.wordmemory.database.UserConfig;
import top.llr2021.wordmemory.object.JsonQQ;
import top.llr2021.wordmemory.util.ActivityCollector;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private ImageView imgPic;

    // 登录按钮
    private CardView cardLogin;

    private LinearLayout linearLayout;

    private static final String TAG = "LoginActivity";

    private final int NewUser = 1;
    private final int OldUser = 2;
    private final int FAILED = 3;

    private ProgressDialog progressDialog;


    private String content;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FAILED:
                    Toast.makeText(LoginActivity.this, "登录失败，请检查服务器与网络状态", Toast.LENGTH_SHORT).show();
                    break;
                case NewUser:
                     ActivityCollector.startOtherActivity(LoginActivity.this, ChooseWordDBActivity.class);
                    break;
                case OldUser:
                    ActivityCollector.startOtherActivity(LoginActivity.this, MainActivity.class);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        Glide.with(this).load(R.drawable.a_learn).into(imgPic);

        // 渐变动画
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        imgPic.startAnimation(animation);

        cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("提示")
                        .setMessage("本软件仅收集用户名、ID、头像三个必要的信息，我们不会泄露您的个人隐私，仅作为标识使用。请放心使用")
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                QQlogin(LoginActivity.this);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();


            }
        });

    }

    private void init() {
        imgPic = findViewById(R.id.img_inbetweening);
        cardLogin = findViewById(R.id.card_QQ_login);
        linearLayout = findViewById(R.id.linear_login);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("提示")
                .setMessage("确定要退出吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       ActivityCollector.finishAll();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    private void QQlogin(Context context){

        //登录
        QQLoginHelper.getInstance().login(context, new QQLoginHelper.OnLoginListener() {
            @Override
            public void loginStatus(int code, String message, Object obj) {
                switch (code) {
                    case QQStatus.START_LOGIN_APPLY://正在申请登录授权
                        Toast.makeText(LoginActivity.this,"正在申请登录授权",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_APPLY_CANCEL://登录授权取消
                        Toast.makeText(LoginActivity.this,"登录授权取消",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_APPLY_FAILED_NULL://登录授权失败
                        Toast.makeText(LoginActivity.this,"登录授权失败",Toast.LENGTH_SHORT).show();
                    case QQStatus.LOGIN_APPLY_FAILED://登录授权失败
                        Toast.makeText(LoginActivity.this,"登入授权失败",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_APPLY_PARSE_FAILED_NULL://登录授权数据解析失败
                    case QQStatus.LOGIN_APPLY_PARSE_FAILED://登录授权数据解析失败
                        Toast.makeText(LoginActivity.this,"登录授权数据解析失败",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_APPLY_SUCCESS://登录授权成功,正在获取登录信息
                        Toast.makeText(LoginActivity.this,"登录授权成功,正在获取登录信息",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_INVALID://未登录或登录过期
                        Toast.makeText(LoginActivity.this,"未登录或登录过期",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_INFO_CANCEL://拉取用户登录信息已被取消
                        Toast.makeText(LoginActivity.this,"拉取用户登录信息已被取消",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_INFO_FAILED_NULL://拉取用户登录信息失败
                    case QQStatus.LOGIN_INFO_FAILED://拉取用户登录信息失败
                        Toast.makeText(LoginActivity.this,"拉取用户登录信息失败",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_INFO_PARSE_FAILED_NULL://拉取用户登录信息解析失败
                    case QQStatus.LOGIN_INFO_PARSE_FAILED://拉取用户登录信息解析失败
                        Toast.makeText(LoginActivity.this,"拉取用户登录信息解析失败",Toast.LENGTH_SHORT).show();
                        break;
                    case QQStatus.LOGIN_INFO_SUCCESS://获取登录信息成功(登录成功)
                        //打印用户信息
                        LoginInfo loginInfo = (LoginInfo) obj;
                        JsonQQ jsonQQ = new JsonQQ();
                        jsonQQ.setId(loginInfo.getOpenId());
                        jsonQQ.setName(loginInfo.getNickname());
                        jsonQQ.setProfile_image_url(loginInfo.getFigureurl_2());
                        initLogin(jsonQQ);
                        break;
                    case QQStatus.LOGIN_OUT_SUCCESS://退出登录成功
                        break;
                    default:
                        break;
                }
            }
        });
    }
    private void initLogin(JsonQQ jsonQQ){

        List<User> users = LitePal.where("userId = ?", jsonQQ.getId() + "").find(User.class);

        if (users.isEmpty()) {
            User user = new User();
            user.setUserName(jsonQQ.getName());
            user.setUserProfile(jsonQQ.getProfile_image_url());
            user.setUserId(jsonQQ.getId());
            // 测试
            user.setUserMoney(0);
            user.setUserWordNumber(0);
            boolean b =user.save();
        }
        // 查询在用户配置表中，是否存在该用户，若没有，则新建数据
        List<UserConfig> userConfigs = LitePal.where("userId = ?", jsonQQ.getId() + "").find(UserConfig.class);
        Message message = new Message();
        if (userConfigs.isEmpty()) {
            UserConfig userConfig = new UserConfig();
            userConfig.setUserId(jsonQQ.getId());
            userConfig.setCurrentBookId(-1);
            userConfig.assignBaseObjId(0);
            boolean b =userConfig.save();

            message.what = NewUser;
        }else{
            if(userConfigs.get(0).getCurrentBookId()!=-1) {
                message.what = OldUser;
            }
        }
        // 默认已登录并设置已登录的QQ
        ConfigData.setIsLogged(true);
        ConfigData.setQQNumLogged(jsonQQ.getId());

        handler.sendMessage(message);

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody formBody = new FormBody.Builder()
                        .add(ServerData.LOGIN_QQ_NUM, jsonQQ.getId() + "")
                        .add(ServerData.LOGIN_QQ_NAME, jsonQQ.getName())
                        .build();
                Request request = new Request.Builder()
                        .post(formBody)
                        .url(ServerData.SERVER_LOGIN_ADDRESS)
                        .build();

                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }
                        });
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        QQLoginHelper.getInstance().onActivityResultData(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}



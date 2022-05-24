package top.llr2021.wordmemory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import top.llr2021.wordmemory.R;
import top.llr2021.wordmemory.activity.service.NotifyLearnService;
import top.llr2021.wordmemory.config.ConfigData;
import top.llr2021.wordmemory.config.ServerData;
import top.llr2021.wordmemory.database.User;
import top.llr2021.wordmemory.database.UserConfig;
import top.llr2021.wordmemory.listener.PermissionListener;
//import top.llr2021.wordmemory.util.BaiduHelper;
import top.llr2021.wordmemory.util.ActivityCollector;
import top.llr2021.wordmemory.util.MyApplication;
import top.llr2021.wordmemory.util.MyPopWindow;
import top.llr2021.wordmemory.util.TimeController;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout LinearWelCome;
    // 弹出-同意按钮
    private CardView cardAgree;
    // 弹出-不同意按钮
    private TextView textNotAgree;
    // 弹出视图
    private MyPopWindow welWindow;
    // 缩放动画
    private ScaleAnimation animation;

    private static final String TAG = "WelcomeActivity";

    private final int FINISH = 1;

    private String rootPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        rootPath = Environment.getDataDirectory().getPath();
        Log.d(TAG, "路径" + rootPath);

        // 防止重复
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            ActivityCollector.removeActivity(this);
            finish();
            return;
        }

        init();

        // 设置透明度
        //zhu cardWelCome.getBackground().setAlpha(200);

        // 如果是第一次运行
        if (ConfigData.getIsFirst()) {
            welWindow.setClipChildren(false)
                    .setBlurBackgroundEnable(true)
                    .setOutSideDismiss(false)
                    .showPopupWindow();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LinearWelCome.startAnimation(animation);
                }
            }, 500);
            // 设置学习提醒
           if (ConfigData.getIsAlarm()) {
                int hour = Integer.parseInt(ConfigData.getAlarmTime().split("-")[0]);
                int minute = Integer.parseInt(ConfigData.getAlarmTime().split("-")[1]);
                AlarmActivity.startAlarm(hour, minute, false, false);
            }
            // 设置通知栏单词
            if (ConfigData.getIsNotifyLearn()) {
                if (!BaseActivity.isServiceExisted(MyApplication.getContext(), NotifyLearnService.class.getName())) {
                    // 检查当前是否数据有效
                    LearnInNotifyActivity.checkIsAvailable();
                    LearnInNotifyActivity.startService(ConfigData.getNotifyLearnMode());
                }
            }
        }

        MainActivity.lastFragment = 0;
        //zhu MainActivity.needRefresh = true;

    }

    private void init() {
        // 设置权限弹出框
        welWindow = new MyPopWindow(this);
        LinearWelCome = findViewById(R.id.text_wel);
        cardAgree = welWindow.findViewById(R.id.card_agree);
        cardAgree.setOnClickListener(this);
        textNotAgree = welWindow.findViewById(R.id.text_not_agree);
        textNotAgree.setOnClickListener(this);
        animationConfig();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_agree:
                requestPermission();
                break;
            case R.id.text_not_agree:
                Toast.makeText(this, "抱歉，程序即将退出", Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                break;
        }
    }

    // 权限管理
    private void requestPermission() {
        requestRunPermission(ConfigData.permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                welWindow.dismiss();
                // 设置第一次运行的值为否
                ConfigData.setIsFirst(false);
                // 延迟时间再开始动画
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LinearWelCome.startAnimation(animation);
                    }
                }, MyPopWindow.animatTime);
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                if (!deniedPermission.isEmpty()) {
                    Toast.makeText(WelcomeActivity.this, "无法获得权限，程序即将退出", Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                }
            }
        });
    }

    // 缩放动画配置
    private void animationConfig() {
        // 从原图大小，放大到1.5倍
        animation = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, 1, 0.5f);
        // 设置持续时间
        animation.setDuration(1500);
        // 设置动画结束之后的状态是否是动画的最终状态
        animation.setFillAfter(true);
        // 设置循环次数
        animation.setRepeatCount(0);
        // 设置动画结束后事件
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 已登录，进入首页/选择词书
                if (ConfigData.getIsLogged()) {
                    List<UserConfig> userConfigs = LitePal.where("userId = ?", ConfigData.getQQNumLogged() + "").find(UserConfig.class);
                    if (userConfigs.get(0).getCurrentBookId() == -1) {
                       Intent intent = new Intent(WelcomeActivity.this, ChooseWordDBActivity.class);
                       startActivity(intent);
                    } else if (userConfigs.get(0).getCurrentBookId() != -1 && userConfigs.get(0).getWordNeedReciteNum() == 0) {
                        Intent intent = new Intent(WelcomeActivity.this, ChangePlanActivity.class);
                        startActivity(intent);
                    } else {

                        // 后台更新登录时间
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                updateServerData();
                            }
                        }).start();
                         Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                         startActivity(intent);
                    }
                }
                // 未登录，进入登录页面
                else {
                    Toast.makeText(WelcomeActivity.this, "未登录", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void updateServerData() {
        List<User> users = LitePal.where("userId = ?", ConfigData.getQQNumLogged() + "").find(User.class);
        RequestBody formBody = new FormBody.Builder()
                .add(ServerData.LOGIN_QQ_NUM, users.get(0).getUserId()+"")
                .add(ServerData.LOGIN_QQ_NAME, users.get(0).getUserName())
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

}
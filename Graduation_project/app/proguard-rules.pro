-dontshrink
-flattenpackagehierarchy
#指定压缩级别
-optimizationpasses 7
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法 后面的参数是一个过滤器，这个过滤器是谷歌推挤的算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#不混淆输入的类文件
-dontobfuscate

 #混淆时是否记录日志
-verbose

# 混淆时不做预校验 Android 不需要做 preverify，去掉可加快混淆速度
-dontpreverify

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile

#保留行号
-keepattributes SourceFile,LineNumberTable,Deprecated

#保护注解
-keepattributes *Annotation*,InnerClasses,Deprecated


# 泛型与反射
-keepattributes Signature

-keepattributes EnclosingMethod

-keepattributes *Annotation*

-keep class package.classname{*;}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#不提示兼容库的错误警告
-dontwarn android.support.**

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**


#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v7.app.AppCompatActivity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keep public class * extends android.os.IInterface
-keep interface android.support.constraint.** { *; }
-keep class androidx.** {*;}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}


-keep class * extends android.support.v4.app.FragmentManager{ *; }
-keepclasseswithmembernames class android.support.v4.widget.ViewDragHelper{ *; }


#不混淆资源类及其方法
-keep class **.R$* {
 *;
}


# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);

}

 # 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}


#-----------处理实体类---------------
# 在开发的时候我们可以将所有的实体类放在一个包内，这样我们写一次混淆就行了。
#-keep public class com.inm.models.** {
#    public void set*(***);
#    public *** get*();
#    public *** is*();
#
#}

 #保护WebView对HTML页面的API不被混淆
-keep class **.Webview2JsInterface { *; }


#如果项目中用到了WebView的复杂操作
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

# 移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用
# 记得proguard-android.txt中一定不要加-dontoptimize才起作用
# 另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
#-assumenosideeffects class android.util.Log {
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}

#############################################
#
# 项目中特殊处理部分
#
#############################################

#-----------处理反射类---------------



#-----------处理js交互---------------



#-----------处理第三方依赖库---------

-printmapping mapping.txt

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}


# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}



# 极光推送
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

 -keep public class cn.jiguang.analytics.android.api.** {
        *;
    }


-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}


# OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**


# OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class okio.**{*;}
-dontwarn okio.**

-keep class org.litepal.** {
    *;
}

-keep class * extends org.litepal.crud.DataSupport {
    *;
}

-keep class * extends org.litepal.crud.LitePalSupport {
    *;
}

# Retrolambda
-dontwarn java.lang.invoke.*

## ---------Retrofit混淆方法---------------
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

# Gson
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
# 将下面替换成自己的实体类
-keep class com.inm.models.** { *; }

# 微信支付
-dontwarn com.tencent.mm.**
-dontwarn com.tencent.wxop.stat.**
-keep class com.tencent.mm.** {*;}
-keep class com.tencent.wxop.stat.**{*;}

# 微信分享
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}

# QQ
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}


# 支付宝钱包
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }

-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*

#nineoldandroids动画lib包

-dontwarn com.nineoldandroids.*
-keep class com.nineoldandroids.** { *;}

#zxing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** {*;}

#TimePickerDialog
-dontwarn com.jzxiang.pickerview.**
-keep class com.jzxiang.pickerview.** {*;}

#xrefreshview
-dontwarn com.andview.refreshview.**
-keep class com.andview.refreshview.** {*;}



#alertdialog
-dontwarn com.yanzhenjie.alertdialog.**
-keep class com.yanzhenjie.alertdialog.** {*;}





#com.weigan.loopview
-dontwarn com.weigan.loopview.**
-keep class com.weigan.loopview.** {*;}


#com.yanzhenjie.permission
-dontwarn kr.co.namee.permissiongen.**
-keep class kr.co.namee.permissiongen.** {*;}

-keepclassmembers class ** {
    @kr.co.namee.permissiongen.PermissionGen <methods>;
}
-keepclassmembers class ** {
    @kr.co.namee.permissiongen.PermissionFail <methods>;
}
-keepclassmembers class ** {
    @kr.co.namee.permissiongen.PermissionSuccess <methods>;
}




-keep class com.daimajia.easing.** { *; }
-keep interface com.daimajia.easing.** { *; }

-dontwarn com.inm.app.common_ui.views.**
-keep class com.inm.app.common_ui.views.ViewsUtils.**{*;}
-keep class com.inm.app.common_ui.views.** { *; }




-keep class tv.danmaku.ijk.media.player.**{*;}
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep class com.inm.app.video.** { *; }
-dontwarn com.inm.app.video.**


-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
-keep class com.shuyu.gsyvideoplayer.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.**

-keepattributes InnerClasses,Signature
-keepattributes *Annotation*
-keep class cn.addapp.pickers.entity.** { *;}




-keep class com.jkb.fragment.rigger.rigger.** {*;}
-keep interface com.jkb.fragment.rigger.rigger.** {*;}
-keep class com.jkb.fragment.swiper.**{*;}

-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
-keepclassmembers class * extends android.app.Activity {
   public int getContainerViewId();
   public boolean onRiggerBackPressed();
   public void onFragmentResult(int,int,android.os.Bundle);
   public void onLazyLoadViewCreated(android.os.Bundle);
   public int[] getPuppetAnimations();
   public String getFragmentTag();
   public boolean onInterruptBackPressed();
}
-keepclassmembers class * extends android.support.v4.app.Fragment {
   public int getContainerViewId();
   public boolean onRiggerBackPressed();
   public void onFragmentResult(int,int,android.os.Bundle);
   public void onLazyLoadViewCreated(android.os.Bundle);
   public int[] getPuppetAnimations();
   public String getFragmentTag();
   public boolean onInterruptBackPressed();
}

-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}

-dontwarn android.graphics.**

 # androidx 的混淆代码
 -keep class com.google.android.** {*;}
 -keep class androidx.** {*;}
 -keep public class * extends androidx.**
 -keep interface androidx.** {*;}
 -dontwarn com.google.android.material.**
 -dontnote com.google.android.material.**
 -dontwarn androidx.**

 -keepattributes *Annotation*
 -keepattributes *JavascriptInterface*
 -keep class android.webkit.JavascriptInterface {*;}

 -dontwarn com.hwangjr.rxbus.**
 -keep class com.hwangjr.rxbus.** { *; }
 -keep class com.hwangjr.rxbus.finder.** { *; }
 -keep class com.hwangjr.rxbus.thread.EventThread { *; }
 -keepattributes *Annotation
 -keepclassmembers class ** {
      @com.hwangjr.rxbus.annotation.Subscribe public *;
      @com.hwangjr.rxbus.annotation.Produce public *;
      public void onEvent(**);
      public void onEventMainThread(**);
  }
  -keepclassmembers class * implements java.io.Serializable {
      static final long serialVersionUID;
      private static final java.io.ObjectStreamField[] serialPersistentFields;
      !static !transient <fields>;
      !private <fields>;
      !private <methods>;
      private void writeObject(java.io.ObjectOutputStream);
      private void readObject(java.io.ObjectInputStream);
      java.lang.Object writeReplace();
      java.lang.Object readResolve();
  }

  -dontwarn com.xiaomi.push.**
  -keep class com.xiaomi.push.**{*;}

  -keep class com.huawei.hms.** {
     *;
  }
  -dontwarn com.huawei.**
  -keep public class * extends android.app.Activity
  -keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
  -keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}

  -dontwarn com.coloros.mcsdk.**
  -keep class com.coloros.mcsdk.** { *; }

  -dontoptimize
  -dontusemixedcaseclassnames
  -verbose
  -dontskipnonpubliclibraryclasses
  -dontskipnonpubliclibraryclassmembers
  -dontwarn dalvik.**
  -dontwarn com.tencent.smtt.**
  #-overloadaggressively

  # ------------------ Keep LineNumbers and properties ---------------- #
  -keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
  # --------------------------------------------------------------------------

  # Addidional for x5.sdk classes for apps

  -keep class com.tencent.smtt.export.external.**{
      *;
  }

  -keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
  	*;
  }

  -keep class com.tencent.smtt.sdk.CacheManager {
  	public *;
  }

  -keep class com.tencent.smtt.sdk.CookieManager {
  	public *;
  }

  -keep class com.tencent.smtt.sdk.WebHistoryItem {
  	public *;
  }

  -keep class com.tencent.smtt.sdk.WebViewDatabase {
  	public *;
}

-keep class com.tencent.smtt.sdk.WebBackForwardList {
  	public *;
  }

-keep public class com.tencent.smtt.sdk.WebView {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
  	public static final <fields>;
  	public java.lang.String getExtra();
  	public int getType();
}

-keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
  	public <methods>;
  }

-keep public class com.tencent.smtt.sdk.WebView$PictureListener {
  	public <fields>;
  	public <methods>;
}


-keepattributes InnerClasses

-keep public enum com.tencent.smtt.sdk.WebSettings$** {
      *;
}

-keep public enum com.tencent.smtt.sdk.QbSdk$** {
      *;
}

-keep public class com.tencent.smtt.sdk.WebSettings {
      public *;
}


-keepattributes Signature
-keep public class com.tencent.smtt.sdk.ValueCallback {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebViewClient {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.DownloadListener {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebChromeClient {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
  	public <fields>;
  	public <methods>;
}

-keep class com.tencent.smtt.sdk.SystemWebChromeClient{
  	public *;
}
  # 1. extension interfaces should be apparent
-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
  	public protected *;
}

  # 2. interfaces should be apparent
-keep public class com.tencent.smtt.export.external.interfaces.* {
  	public protected *;
}

-keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
  	public protected *;
}

-keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebIconDatabase {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.WebStorage {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.DownloadListener {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.QbSdk {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
  	public <fields>;
  	public <methods>;
}
-keep public class com.tencent.smtt.sdk.CookieSyncManager {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.Tbs* {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.utils.LogFileUtils {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.utils.TbsLog {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.utils.TbsLogClient {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.CookieSyncManager {
  	public <fields>;
  	public <methods>;
}

  # Added for game demos
-keep public class com.tencent.smtt.sdk.TBSGamePlayer {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
  	public <fields>;
  	public <methods>;
}

-keep public class com.tencent.smtt.utils.Apn {
  	public <fields>;
  	public <methods>;
}
-keep class com.tencent.smtt.** {
  	*;
}
  # end


-keep public class com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension {
  	public <fields>;
  	public <methods>;
}

-keep class MTT.ThirdAppInfoNew {
  	*;
}

-keep class com.tencent.mtt.MttTraceEvent {
  	*;
}

  # Game related
-keep public class com.tencent.smtt.gamesdk.* {
  	public protected *;
}

-keep public class com.tencent.smtt.sdk.TBSGameBooter {
          public <fields>;
          public <methods>;
}

-keep public class com.tencent.smtt.sdk.TBSGameBaseActivity {
  	public protected *;
}

-keep public class com.tencent.smtt.sdk.TBSGameBaseActivityProxy {
  	public protected *;
}

-keep public class com.tencent.smtt.gamesdk.internal.TBSGameServiceClient {
  	public *;
}
  #---------------------------------------------------------------------------


  #------------------  下方是android平台自带的排除项，这里不要动         ----------------

-keep public class * extends android.app.Activity{
  	public <fields>;
  	public <methods>;
}
-keep public class * extends android.app.Application{
  	public <fields>;
  	public <methods>;
}
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
  	public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
  	public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepattributes *Annotation*

-keepclasseswithmembernames class *{
  	native <methods>;
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

  #------------------  下方是共性的排除项目         ----------------
  # 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
  # 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除
-keepclasseswithmembers class * {
      ... *JNI*(...);
}

-keepclasseswithmembernames class * {
  	... *JRI*(...);
}

-keep class **JNI* {*;}


-keep class com.gyf.immersionbar.* {*;}
-dontwarn com.gyf.immersionbar.**

-keep public class com.inm.ui.advertisement.AdvertisementActivity { *; }

-keep class com.dueeeke.videoplayer.** { *; }
-dontwarn com.dueeeke.videoplayer.**

-keep class com.huantansheng.easyphotos.models.** { *; }

-keepnames class com.bun.miitmdid.core.IIdentifierListener


-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }
-keep public class com.tencent.tinker.entry.TinkerApplicationInlineFence {
    <init>(...);
    void attachBaseContext(com.tencent.tinker.loader.app.TinkerApplication, android.content.Context);
}

-keep public class com.tencent.bugly.beta.tinker.TinkerPatchReflectApplication {
    <init>(...);
}

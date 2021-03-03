# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#阿里推送
-keepclasseswithmembernames class ** {
    native <methods>;
}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-keep class com.ut.** {*;}
-keep class com.ta.** {*;}
-keep class anet.**{*;}
-keep class anetwork.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-keep class android.os.**{*;}
-keep class org.json.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-dontwarn anetwork.**
-dontwarn com.ut.**
-dontwarn com.ta.**
# 小米通道
-keep class com.xiaomi.** {*;}
-dontwarn com.xiaomi.**
# 华为通道
-keep class com.huawei.** {*;}
-dontwarn com.huawei.**
# GCM/FCM通道
-keep class com.google.firebase.**{*;}
-dontwarn com.google.firebase.**
# OPPO通道
-keep public class * extends android.app.Service
# VIVO通道
-keep class com.vivo.** {*;}
-dontwarn com.vivo.**
# 魅族通道
-keep class com.meizu.cloud.** {*;}
-dontwarn com.meizu.cloud.**
# emas 性能监控
-keep class com.taobao.monitor.APMLauncher{*;}
-keep class com.taobao.monitor.impl.logger.Logger{*;}
-keep class com.taobao.monitor.impl.logger.IDataLogger{*;}
-keep class com.taobao.monitor.impl.data.AbsWebView{*;}
-keep class com.taobao.monitor.impl.data.GlobalStats{*;}
-keep class com.taobao.monitor.impl.common.Global{*;}
-keep class com.taobao.monitor.impl.data.WebViewProxy{*;}
-keep class com.taobao.monitor.impl.logger.Logger{*;}
-keep class com.taobao.monitor.impl.processor.pageload.IProcedureManager{*;}
-keep class com.taobao.monitor.impl.processor.pageload.ProcedureManagerSetter{*;}
-keep class com.taobao.monitor.impl.util.TimeUtils{*;}
-keep class com.taobao.monitor.impl.util.TopicUtils{*;}
-keep class com.taobao.monitor.impl.common.DynamicConstants{*;}
-keep class com.taobao.application.common.data.DeviceHelper{*;}
-keep class com.taobao.application.common.impl.AppPreferencesImpl{*;}
-keep class com.taobao.monitor.impl.processor.launcher.PageList{*;}
-keep class com.taobao.monitor.impl.processor.fragmentload.FragmentInterceptorProxy{*;}
-keep class com.taobao.monitor.impl.processor.fragmentload.IFragmentInterceptor{*;}
-keep class com.taobao.monitor.impl.logger.DataLoggerUtils{*;}
-keep interface com.taobao.monitor.impl.data.IWebView{*;}
-keep interface com.taobao.monitor.impl.processor.IProcessor{*;}
-keep interface com.taobao.monitor.impl.processor.IProcessorFactory{*;}
-keep interface com.taobao.monitor.impl.logger.IDataLogger{*;}
-keep interface com.taobao.monitor.impl.trace.IDispatcher{*;}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# emas 日志
#keep class
-keep interface com.taobao.tao.log.ITLogController{*;}
-keep class com.taobao.tao.log.upload.*{*;}
-keep class com.taobao.tao.log.message.*{*;}
-keep class com.taobao.tao.log.LogLevel{*;}
-keep class com.taobao.tao.log.TLog{*;}
-keep class com.taobao.tao.log.TLogConstant{*;}
-keep class com.taobao.tao.log.TLogController{*;}
-keep class com.taobao.tao.log.TLogInitializer{public *;}
-keep class com.taobao.tao.log.TLogUtils{public *;}
-keep class com.taobao.tao.log.TLogNative{*;}
-keep class com.taobao.tao.log.TLogNative$*{*;}
-keep class com.taobao.tao.log.CommandDataCenter{*;}
-keep class com.taobao.tao.log.task.PullTask{*;}
-keep class com.taobao.tao.log.task.UploadFileTask{*;}
-keep class com.taobao.tao.log.upload.LogFileUploadManager{public *;}
-keep class com.taobao.tao.log.monitor.**{*;}
#兼容godeye
-keep class com.taobao.tao.log.godeye.core.module.*{*;}
-keep class com.taobao.tao.log.godeye.GodeyeInitializer{*;}
-keep class com.taobao.tao.log.godeye.GodeyeConfig{*;}
-keep class com.taobao.tao.log.godeye.core.control.Godeye{*;}
-keep interface com.taobao.tao.log.godeye.core.GodEyeAppListener{*;}
-keep interface com.taobao.tao.log.godeye.core.GodEyeReponse{*;}
-keep interface com.taobao.tao.log.godeye.api.file.FileUploadListener{*;}
-keep public class * extends com.taobao.android.tlog.protocol.model.request.base.FileInfo{*;}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# Vlayout
-keepattributes InnerClasses
-keep class com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutParams { *; }
-keep class android.support.v7.widget.RecyclerView$ViewHolder { *; }
-keep class android.support.v7.widget.ChildHelper { *; }
-keep class android.support.v7.widget.ChildHelper$Bucket { *; }
-keep class android.support.v7.widget.RecyclerView$LayoutManager { *; }

#Gson
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}


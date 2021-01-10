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
-printmapping mapping.txt
-verbose
-dontoptimize
-dontpreverify
-dontshrink
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Annotation
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class * extends android.app.Activity
-assumenosideeffects class android.util.Log {
    public static * d(...);
    public static * v(...);
}

# Suppress warnings if you are NOT using IAP:
-dontwarn com.nnacres.app.**
-dontwarn com.androidquery.**
-dontwarn com.google.**
-dontwarn org.acra.**
-dontwarn org.apache.**
-dontwarn com.mobileapptracker.**
-dontwarn com.nostra13.**
-dontwarn net.simonvt.**
-dontwarn android.support.**
-dontwarn com.facebook.**
-dontwarn twitter4j.**
-dontwarn com.astuetz.**
-dontwarn com.actionbarsherlock.**
-dontwarn com.dg.libs.**
-dontwarn  com.bluetapestudio.templateproject.**

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes Annotation

#
#Keep native
-keepclasseswithmembernames class * {
    native <methods>;
}
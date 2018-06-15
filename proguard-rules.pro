-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-dontusemixedcaseclassnames
-ignorewarnings
-verbose

-keepattributes *Annotation*,EnclosingMethod, InnerClasses, Exceptions, Signature, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
-optimizationpasses 5
-overloadaggressively

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

################################################

-dontnote android.**
-dontwarn android.**

-dontnote com.sun.**
-dontwarn com.sun.**

-dontnote sun.**
-dontwarn sun.**

-dontnote java.**
-dontwarn java.**

-dontnote javax.**
-dontwarn javax.**


# keep all public classes in main package
-keep class at.favre.lib.bytes.** { public *; }

-keep class sun.misc.Unsafe { *; }

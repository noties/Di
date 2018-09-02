# important for generic injection (including Lazy & Provider)
-keepattributes Signature, *Annotation*

-keepclassmembers,allowobfuscation class * {
  @javax.inject.* <fields>;
  @javax.inject.* <init>();
}

-keep class javax.inject.** { *; }
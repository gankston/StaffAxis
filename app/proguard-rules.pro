# OPTIMIZACIÓN PARA REDUCIR TAMAÑO DE APK
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Eliminar logs en release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Compresión agresiva
-keepattributes *Annotation*
-dontwarn **
-ignorewarnings

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit - evitar ClassCastException (Class cannot be cast to ParameterizedType)
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
# Mantener interfaces API con sus firmas genéricas
-keep interface com.registro.empleados.data.remote.api.** { *; }
-keep class com.registro.empleados.data.remote.dto.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations

# Apache POI
-dontwarn org.apache.poi.**
-keep class org.apache.poi.** { *; }

# OPTIMIZACIÓN PARA REDUCIR TAMAÑO DE APK
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Logs: mantener Log.i/Log.e para diagnóstico (StaffAxis)
# -assumenosideeffects removido para ver getSectors OK / resolveSector en release

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

# DTOs de Gson: inmunes a obfuscación (sectors fix R8/ProGuard)
-keep class com.registro.empleados.data.remote.api.SectorsResponseDto { *; }
-keep class com.registro.empleados.data.remote.dto.SectorDto { *; }
-keep class **SectorsResponseDto { *; }
-keep class **SectorDto { *; }

# Gson: mantener campos con @SerializedName
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepattributes Signature
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations

# Apache POI
-dontwarn org.apache.poi.**
-keep class org.apache.poi.** { *; }

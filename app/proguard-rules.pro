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

# --- kotlinx.serialization ---
# Сериализаторы генерируются для @Serializable-классов; их и метаданные нельзя обфусцировать.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
# DTO-классы приложения и их сгенерированные сериализаторы.
-keep,includedescriptorclasses class uz.uzgidro.ugenews.data.net.dto.**$$serializer { *; }
-keepclassmembers class uz.uzgidro.ugenews.data.net.dto.** {
    *** Companion;
}
-keepclasseswithmembers class uz.uzgidro.ugenews.data.net.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Retrofit ---
-keepattributes Signature, Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# --- Room ---
# Room генерирует реализации DAO/DB; сущности сохраняем целиком.
-keep class uz.uzgidro.ugenews.data.db.** { *; }

# --- Coil --- (правила поставляются самой библиотекой; доп. keep не требуется)
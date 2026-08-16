# Compose
-dontwarn androidx.compose.**

# Room
-keep class com.deepspace.hourglass.data.** { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Keep Room generated code
-keep class * extends androidx.room.RoomDatabase { *; }
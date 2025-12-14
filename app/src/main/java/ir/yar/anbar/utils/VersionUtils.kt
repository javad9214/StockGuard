package ir.yar.anbar.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build

/**
 * Utility object for app version information
 */
object VersionUtils {

    /**
     * Get the current app version code
     */
    fun getVersionCode(context: Context): Int {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            e.printStackTrace()
            1 // Default to version 1 if we can't get the version
        }
    }

    /**
     * Get the current app version name
     */
    fun getVersionName(context: Context): String {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            e.printStackTrace()
            "1.0.0" // Default version name
        }
    }

    /**
     * Get both version code and name as a formatted string
     */
    fun getFullVersionInfo(context: Context): String {
        return "v${getVersionName(context)} (${getVersionCode(context)})"
    }
}
package com.example.aks_e_sani002.utils // Or your relevant utils package

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private const val PREFS_NAME = "AppPrefs" // Name for your SharedPreferences file
    private const val TAG = "LocaleHelper"

    // Call this in your Application class's onCreate or early in your BaseActivity's attachBaseContext
    // For this example, we'll call a version of it from MainActivity's onCreate
    fun setLocale(context: Context, languageCode: String?): Context {
        val actualLanguageCode = languageCode ?: getPersistedData(context) ?: Locale.getDefault().language
        persist(context, actualLanguageCode)
        Log.d(TAG, "setLocale: Setting language to: $actualLanguageCode")

        val locale = Locale(actualLanguageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val config: Configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLayoutDirection(locale) // Important for RTL languages
            return context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(locale) // Important for RTL languages
            }
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        }
        return context // For older versions, context is updated directly
    }

    // Call this in your Activity's attachBaseContext to ensure locale is set before anything else
    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context) ?: Locale.getDefault().language
        Log.d(TAG, "onAttach: Attaching with language: $lang")
        return setLocale(context, lang)
    }

    // Overload for when you explicitly change language
    fun onAttach(context: Context, defaultLanguage: String): Context {
        Log.d(TAG, "onAttach: Attaching with default language: $defaultLanguage")
        return setLocale(context, defaultLanguage)
    }


    fun getLanguage(context: Context): String {
        return getPersistedData(context) ?: Locale.getDefault().language
    }

    private fun getPersistedData(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, null)
    }

    private fun persist(context: Context, language: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
        Log.d(TAG, "Persisted language: $language")
    }
}

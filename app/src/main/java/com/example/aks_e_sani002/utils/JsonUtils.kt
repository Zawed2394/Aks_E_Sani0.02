package com.example.aks_e_sani002.utils

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.io.IOException
import java.util.Locale

object JsonUtils {

    private const val TAG = "JsonUtils" // Tag for logging

    fun loadFehristFromAssets(context: Context, language: String): List<Pair<String, String>> {
        val currentLanguageCode = language.lowercase(Locale.getDefault())
        Log.d(TAG, "loadFehristFromAssets called with language: $language (processed as: $currentLanguageCode)")

        val fileName = when (currentLanguageCode) {
            "ur" -> "fehrist.json"       // As confirmed, fehrist.json is your Urdu file.
            "hi" -> "fehrist_hindi.json"
            "en" -> "fehrist_english.json"
            else -> {
                Log.w(TAG, "Unknown language code '$currentLanguageCode', defaulting to fehrist.json (Urdu).")
                "fehrist.json" // Default to your main fehrist.json (Urdu)
            }
        }

        Log.d(TAG, "Attempting to load file: '$fileName' for language code: '$currentLanguageCode'")

        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val poemList = mutableListOf<Pair<String, String>>()

            for (i in 0 until jsonArray.length()) {
                val section = jsonArray.getJSONObject(i)
                val items = section.getJSONArray("items")

                for (j in 0 until items.length()) {
                    val item = items.getJSONObject(j)
                    val id = item.getString("id")
                    val title = item.getString("title")
                    poemList.add(Pair(id, title))
                }
            }

            if (poemList.isEmpty()) {
                Log.w(TAG, "Loaded an EMPTY poem list from '$fileName'. Check if the file exists, is not empty, and is valid JSON.")
            } else {
                Log.d(TAG, "Successfully loaded ${poemList.size} items from '$fileName'. First title (raw): '${poemList[0].second}'")
            }
            poemList
        } catch (e: IOException) {
            Log.e(TAG, "IOException: Error loading or reading '$fileName' for language '$currentLanguageCode'. File might be missing or unreadable.", e)
            emptyList() // Return empty list on error
        } catch (e: org.json.JSONException) {
            Log.e(TAG, "JSONException: Error parsing '$fileName' for language '$currentLanguageCode'. Ensure it's valid JSON.", e)
            emptyList() // Return empty list on error
        } catch (e: Exception) { // Catch-all for any other unexpected errors
            Log.e(TAG, "Generic Exception: Error processing '$fileName' for language '$currentLanguageCode'.", e)
            emptyList() // Return empty list on error
        }
    }
}

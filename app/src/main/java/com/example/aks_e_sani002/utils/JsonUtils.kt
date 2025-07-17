package com.example.aks_e_sani002.utils

import android.content.Context
import org.json.JSONArray
import java.util.Locale

object JsonUtils {

    fun loadFehristFromAssets(context: Context, language: String): List<Pair<String, String>> {
        val fileName = when (language.lowercase(Locale.getDefault())) {
            // Match your actual filenames
            "urdu" -> "fehrist.json" // Or "fehrist_urdu.json" if you rename it
            "hindi" -> "fehrist_hindi.json"
            "en" -> "fehrist_english.json" // Changed "en" to match "english" in filename, or change language string
            else -> "fehrist.json" // Default to your main fehrist.json (assuming it's Urdu)
        }

        // ... rest of the function
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
            poemList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

package com.example.aks_e_sani002

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aks_e_sani002.adapter.SectionAdapter
import com.example.aks_e_sani002.model.Item
import com.example.aks_e_sani002.model.Section
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.Charset

class FehristActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fehrist)

        // ✅ Back button setup
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Go back to previous screen
        }

        // ✅ RecyclerView setup
        recyclerView = findViewById(R.id.sectionRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ Load data from JSON
        val sectionList = loadSectionsFromJson()

        // ✅ Adapter attach
        adapter = SectionAdapter(sectionList)
        recyclerView.adapter = adapter
    }

    private fun loadSectionsFromJson(): List<Section> {
        val sectionList = mutableListOf<Section>()
        try {
            val inputStream = assets.open("fehrist.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, Charset.forName("UTF-8"))

            val gson = Gson()
            val sectionType = object : TypeToken<List<Section>>() {}.type
            val parsedList: List<Section> = gson.fromJson(json, sectionType)

            parsedList.forEach { section ->
                val resId = resources.getIdentifier(section.id, "string", packageName)
                val localizedHeading = if (resId != 0) getString(resId) else section.heading

                val localizedItems = section.items.map { item ->
                    val itemResId = resources.getIdentifier(item.id, "string", packageName)
                    val localizedItemTitle = if (itemResId != 0) getString(itemResId) else item.title
                    Item(item.id, localizedItemTitle, item.content)
                }

                sectionList.add(Section(section.id, localizedHeading, localizedItems))
            }
        } catch (e: IOException) {
            Log.e("FehristActivity", "Error reading JSON: ${e.message}")
        } catch (e: Exception) {
            Log.e("FehristActivity", "Parsing error: ${e.message}")
        }

        return sectionList
    }
}

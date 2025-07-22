package com.example.aks_e_sani002

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.aks_e_sani002.Tabsira
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aks_e_sani002.utils.LocaleHelper
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class TabsiraActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val tabsiraList = mutableListOf<Tabsira>()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabsira)

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.tabsira_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadTabsiraData()
        recyclerView.adapter = TabsiraAdapter(tabsiraList)
    }

    private fun loadTabsiraData() {
        try {
            val inputStream = assets.open("tabsira.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonText = reader.readText()
            val jsonArray = JSONArray(jsonText)

            val lang = LocaleHelper.getLanguage(this)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val title = obj.getJSONObject("title").optString(lang, obj.getJSONObject("title").optString("ur"))
                val author = obj.getJSONObject("author").optString(lang, obj.getJSONObject("author").optString("ur"))
                val content = obj.getJSONObject("content").optString(lang, obj.getJSONObject("content").optString("ur"))

                tabsiraList.add(Tabsira(title, author, content))
            }

        } catch (e: Exception) {
            Log.e("TabsiraActivity", "Error loading tabsira.json", e)
        }
    }
}

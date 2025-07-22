package com.example.aks_e_sani002

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aks_e_sani002.utils.LocaleHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class TabsiraDetailActivity : AppCompatActivity() {

    private lateinit var titleView: TextView
    private lateinit var contentView: TextView
    private lateinit var authorView: TextView
    private lateinit var serialView: TextView
    private lateinit var appNameText: TextView
    private lateinit var noorNameText: TextView

    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var fontIncreaseButton: ImageButton
    private lateinit var fontDecreaseButton: ImageButton

    private var currentIndex: Int = 0
    private lateinit var tabsiraList: List<Tabsira>

    private var currentFontSize = 18f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabsira_detail)

        // Bind views
        titleView = findViewById(R.id.detail_title)
        contentView = findViewById(R.id.detail_content)
        authorView = findViewById(R.id.detail_author)
        serialView = findViewById(R.id.toolbar_serial)
        appNameText = findViewById(R.id.toolbar_app_name)
        noorNameText = findViewById(R.id.toolbar_noor_name)

        previousButton = findViewById(R.id.button_previous)
        nextButton = findViewById(R.id.button_next)
        fontIncreaseButton = findViewById(R.id.button_font_increase)
        fontDecreaseButton = findViewById(R.id.button_font_decrease)

        appNameText.text = getString(R.string.app_name)
        noorNameText.text = getString(R.string.nav_header_nooruddin)

        // Get current index from adapter
        currentIndex = intent.getIntExtra("index", 0)

        // Load tabsira data
        val language = LocaleHelper.getLanguage(this)
        tabsiraList = loadTabsiraFromAssets(language)

        // Show current item
        showTabsira(currentIndex)

        // Navigation buttons
        previousButton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                showTabsira(currentIndex)
            }
        }

        nextButton.setOnClickListener {
            if (currentIndex < tabsiraList.size - 1) {
                currentIndex++
                showTabsira(currentIndex)
            }
        }

        // Font size buttons
        fontIncreaseButton.setOnClickListener {
            if (currentFontSize < 30f) {
                currentFontSize += 2f
                applyFontSizes()
            }
        }

        fontDecreaseButton.setOnClickListener {
            if (currentFontSize > 12f) {
                currentFontSize -= 2f
                applyFontSizes()
            }
        }
    }

    private fun applyFontSizes() {
        contentView.textSize = currentFontSize
        titleView.textSize = currentFontSize + 2f // Slightly larger
        authorView.textSize = currentFontSize - 2f // Slightly smaller
    }

    private fun showTabsira(index: Int) {
        val item = tabsiraList[index]
        titleView.text = item.title
        contentView.text = item.content
        authorView.text = item.author
        serialView.text = (index + 1).toString()

        applyFontSizes()

        previousButton.alpha = if (index == 0) 0.4f else 1.0f
        previousButton.isEnabled = index != 0

        nextButton.alpha = if (index == tabsiraList.size - 1) 0.4f else 1.0f
        nextButton.isEnabled = index != tabsiraList.size - 1
    }

    private fun loadTabsiraFromAssets(lang: String): List<Tabsira> {
        val filename = "tabsira.json"
        val jsonString = assets.open(filename).use {
            BufferedReader(InputStreamReader(it)).readText()
        }
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<Tabsira>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val titleObj = obj.optJSONObject("title") ?: JSONObject()
            val contentObj = obj.optJSONObject("content") ?: JSONObject()
            val authorObj = obj.optJSONObject("author") ?: JSONObject()

            val title = titleObj.optString(lang, titleObj.optString("en", ""))
            val content = contentObj.optString(lang, contentObj.optString("en", ""))
            val author = authorObj.optString(lang, authorObj.optString("en", ""))

            list.add(Tabsira(title, author, content))
        }

        return list
    }
}

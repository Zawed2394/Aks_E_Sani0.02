package com.example.aks_e_sani002

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.util.Locale

class PoemDetailActivity : AppCompatActivity() {

    private lateinit var titleView: TextView
    private lateinit var contentView: TextView
    private lateinit var toolbarSerial: TextView
    private lateinit var buttonNext: ImageButton
    private lateinit var buttonBack: ImageButton
    private lateinit var buttonShare: ImageButton
    private lateinit var buttonFontIncrease: ImageButton
    private lateinit var buttonFontDecrease: ImageButton

    private var currentIndex = 0
    private lateinit var poemList: List<Triple<String, String, String>> // id, title, content
    private var currentFontSize = 18f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poem_detail)

        titleView = findViewById(R.id.chapter_title)
        contentView = findViewById(R.id.chapter_content)
        toolbarSerial = findViewById(R.id.toolbarSerial)
        buttonNext = findViewById(R.id.button_forward)
        buttonBack = findViewById(R.id.button_backward)
        buttonShare = findViewById(R.id.button_share)
        buttonFontIncrease = findViewById(R.id.button_font_increase)
        buttonFontDecrease = findViewById(R.id.button_font_decrease)

        setSupportActionBar(findViewById(R.id.toolbar))

        val passedId = intent.getStringExtra("id") ?: ""
        poemList = loadPoemsFromAssets()
        currentIndex = poemList.indexOfFirst { it.first == passedId }.coerceAtLeast(0)

        showPoem(currentIndex)

        buttonFontIncrease.setOnClickListener {
            if (currentFontSize < 40f) {
                currentFontSize += 2f
                contentView.textSize = currentFontSize
            }
        }

        buttonFontDecrease.setOnClickListener {
            if (currentFontSize > 10f) {
                currentFontSize -= 2f
                contentView.textSize = currentFontSize
            }
        }

        buttonNext.setOnClickListener {
            if (currentIndex < poemList.size - 1) {
                currentIndex++
                val animIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                val animOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                contentView.startAnimation(animOut)
                showPoem(currentIndex)
                contentView.startAnimation(animIn)
            }
        }

        buttonBack.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                val animIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
                val animOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
                contentView.startAnimation(animOut)
                showPoem(currentIndex)
                contentView.startAnimation(animIn)
            } else {
                finish()
            }
        }

        buttonShare.setOnClickListener {
            val (_, title, content) = poemList[currentIndex]
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    private fun showPoem(index: Int) {
        val (_, title, content) = poemList[index]
        titleView.text = title
        contentView.text = content
        contentView.textSize = currentFontSize
        toolbarSerial.text = toLocalizedNumber(index + 1)
    }

    private fun loadPoemsFromAssets(): List<Triple<String, String, String>> {
        val locale = resources.configuration.locales[0].language
        val fileName = when (locale) {
            "hi" -> "fehrist_hindi.json"
            "en" -> "fehrist_english.json"
            else -> "fehrist.json" // Urdu default
        }

        val inputStream = assets.open(fileName)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val list = mutableListOf<Triple<String, String, String>>() // id, title, content
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val section = jsonArray.getJSONObject(i)
            val items = section.getJSONArray("items")
            for (j in 0 until items.length()) {
                val poem = items.getJSONObject(j)
                val id = poem.getString("id")
                val title = poem.getString("title")
                val content = poem.getString("content")
                list.add(Triple(id, title, content))
            }
        }
        return list
    }

    private fun toLocalizedNumber(number: Int): String {
        val lang = Locale.getDefault().language
        return if (lang == "ur" || lang == "fa" || lang == "ar") {
            val urduDigits = listOf('۰','۱','۲','۳','۴','۵','۶','۷','۸','۹')
            number.toString().map { urduDigits[it.digitToInt()] }.joinToString("")
        } else {
            number.toString()
        }
    }
}

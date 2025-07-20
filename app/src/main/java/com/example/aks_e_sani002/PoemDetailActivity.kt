package com.example.aks_e_sani002

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
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
    private lateinit var wordMap: JSONObject

    // Common connectors to skip unless in word_map.json
    private val commonConnectors = setOf(
        "کا", "کے", "کی", "ہ ہے", "میں", "سے", "پر", "تک", // Urdu
        "है", "का", "के", "की", "में", "से", "पर", "तक", // Hindi
        "hai", "ka", "ke", "ki", "mein", "se", "par", "tak" // English
    )

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
        val (id, title, content) = poemList[index]
        titleView.text = title
        toolbarSerial.text = toLocalizedNumber(index + 1)
        contentView.textSize = currentFontSize
        contentView.keyListener = null // Disable text selection to prevent handles

        // load word meanings
        wordMap = loadWordMap(id)
        setupWordMeaningSpans(content)
    }

    private fun loadPoemsFromAssets(): List<Triple<String, String, String>> {
        val locale = resources.configuration.locales[0].language
        val fileName = when (locale) {
            "hi" -> "fehrist_hindi.json"
            "en" -> "fehrist_english.json"
            else -> "fehrist.json" // Urdu default
        }
        Log.d("PoemDetailActivity", "Loading poems from: $fileName")

        val inputStream = assets.open(fileName)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val list = mutableListOf<Triple<String, String, String>>()
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

    private fun loadWordMap(poemId: String): JSONObject {
        return try {
            val json = assets.open("word_map.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)
            val sections = jsonObject.getJSONArray("sections")
            for (i in 0 until sections.length()) {
                val section = sections.getJSONObject(i)
                val items = section.getJSONArray("items")
                for (j in 0 until items.length()) {
                    val item = items.getJSONObject(j)
                    if (item.getString("id") == poemId) {
                        return JSONObject().put("words", item.get("word_meanings"))
                    }
                }
            }
            JSONObject()
        } catch (e: Exception) {
            Log.e("PoemDetailActivity", "Error loading word_map.json: ${e.message}")
            JSONObject()
        }
    }

    private fun setupWordMeaningSpans(text: String) {
        val spannable = SpannableString(text)
        val words = text.split("\\s+".toRegex())
        var start = 0
        val locale = resources.configuration.locales[0].language
        Log.d("PoemDetailActivity", "Locale: $locale, Poem text: $text")

        if (!wordMap.has("words") || wordMap.get("words") !is JSONArray) {
            Log.d("PoemDetailActivity", "No valid word meanings found")
            contentView.text = text
            return
        }

        val wordArray = wordMap.getJSONArray("words")

        // Create a set of mapped words for quick lookup
        val mappedWords = mutableSetOf<String>()
        for (i in 0 until wordArray.length()) {
            val wordObj = wordArray.getJSONObject(i)
            val mappings = wordObj.optJSONObject("mapping")
            val urduMapping = mappings?.optString("urdu") ?: wordObj.getString("word")
            val hindiMapping = mappings?.optString("hindi") ?: wordObj.getString("word")
            val englishMapping = mappings?.optString("english") ?: wordObj.getString("word")
            mappedWords.add(urduMapping.lowercase())
            mappedWords.add(hindiMapping.lowercase())
            mappedWords.add(englishMapping.lowercase())
        }

        for (word in words) {
            // Clean word by removing punctuation, preserving Unicode characters
            val cleanWord = word.trim().replace("[^\\p{L}\\p{N}\\p{M}]".toRegex(), "")
            if (cleanWord.isEmpty()) {
                Log.d("PoemDetailActivity", "Skipping empty word after cleaning: $word")
                continue
            }

            val wordStart = text.indexOf(word, start)
            if (wordStart == -1) {
                Log.d("PoemDetailActivity", "Word not found in text: $word")
                continue // Skip if word not found
            }
            val wordEnd = wordStart + word.length

            // Skip common connectors unless they are in word_map.json
            if (cleanWord.lowercase() in commonConnectors && cleanWord.lowercase() !in mappedWords) {
                Log.d("PoemDetailActivity", "Skipping common connector: $cleanWord")
                start = wordEnd + 1
                continue
            }

            for (i in 0 until wordArray.length()) {
                val wordObj = wordArray.getJSONObject(i)
                val jsonWord = wordObj.getString("word")
                val mappings = wordObj.optJSONObject("mapping")
                val urduMapping = mappings?.optString("urdu") ?: jsonWord
                val hindiMapping = mappings?.optString("hindi") ?: jsonWord
                val englishMapping = mappings?.optString("english") ?: jsonWord

                Log.d("PoemDetailActivity", "Checking word: $cleanWord, Urdu: $urduMapping, Hindi: $hindiMapping, English: $englishMapping")

                // Case-insensitive matching
                val matches = when (locale) {
                    "hi" -> cleanWord.equals(hindiMapping, ignoreCase = true) ||
                            cleanWord.equals(urduMapping, ignoreCase = true) ||
                            cleanWord.equals(englishMapping, ignoreCase = true)
                    "en" -> cleanWord.equals(englishMapping, ignoreCase = true) ||
                            cleanWord.equals(urduMapping, ignoreCase = true) ||
                            cleanWord.equals(hindiMapping, ignoreCase = true)
                    else -> cleanWord.equals(urduMapping, ignoreCase = true) ||
                            cleanWord.equals(hindiMapping, ignoreCase = true) ||
                            cleanWord.equals(englishMapping, ignoreCase = true)
                }

                if (matches) {
                    Log.d("PoemDetailActivity", "Match found for $cleanWord at $wordStart-$wordEnd")
                    val clickableSpan = object : LongClickableSpan() {
                        override fun onClick(widget: View) {
                            showMeaningBottomSheet(wordObj)
                        }

                        override fun onLongClick(view: View, wordObj: JSONObject) {
                            showMeaningBottomSheet(wordObj)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false // Remove underline
                            ds.color = Color.BLACK // Set text color to black
                        }
                    }
                    clickableSpan.wordObj = wordObj
                    spannable.setSpan(clickableSpan, wordStart, wordEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    break
                }
            }
            start = wordEnd + 1
        }

        contentView.text = spannable
        contentView.movementMethod = LongClickLinkMovementMethod.getInstance()
    }

    private fun showMeaningBottomSheet(wordObj: JSONObject) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.bottom_sheet_meaning)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)

        val word = wordObj.getString("word")
        val meanings = wordObj.getJSONObject("meanings")

        dialog.findViewById<TextView>(R.id.wordTextView).text = word
        dialog.findViewById<TextView>(R.id.meaningUrdu).text = "اردو: ${meanings.optString("urdu", "-")}"
        dialog.findViewById<TextView>(R.id.meaningHindi).text = "हिंदी: ${meanings.optString("hindi", "-")}"
        dialog.findViewById<TextView>(R.id.meaningEnglish).text = "English: ${meanings.optString("english", "-")}"

        dialog.show()
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

abstract class LongClickableSpan : ClickableSpan() {
    lateinit var wordObj: JSONObject

    override fun onClick(view: View) {
        // Default implementation
    }

    abstract fun onLongClick(view: View, wordObj: JSONObject)
}
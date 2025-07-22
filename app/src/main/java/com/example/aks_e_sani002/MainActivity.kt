package com.example.aks_e_sani002

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aks_e_sani002.utils.JsonUtils
import com.example.aks_e_sani002.utils.LocaleHelper
import com.google.android.material.navigation.NavigationView
import java.text.Normalizer
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val TAG = "MainActivity"
    private var allTitlesForSearch: List<Pair<String, String>> = emptyList()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentAppLanguage = LocaleHelper.getLanguage(this)
        setContentView(R.layout.activity_main)

        val appNameTextView: TextView = findViewById(R.id.left_text)
        appNameTextView.text = getString(R.string.app_name)

        drawer = findViewById(R.id.drawer_layout)
        val drawerToggle: ImageButton = findViewById(R.id.drawer_toggle)
        drawerToggle.setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        updateNavHeaderTexts()

        val fehristButton: Button = findViewById(R.id.button1)
        fehristButton.text = getString(R.string.button_fehrist)
        fehristButton.setOnClickListener {
            startActivity(Intent(this, FehristActivity::class.java))
        }
        val tabsiraButton: Button = findViewById(R.id.button2)
        tabsiraButton.text = getString(R.string.button_tabsira)
        tabsiraButton.setOnClickListener {
            startActivity(Intent(this, TabsiraActivity::class.java))
        }

        val searchButton: Button = findViewById(R.id.button5)
        searchButton.text = getString(R.string.button_search)
        searchButton.setOnClickListener {
            loadSearchTitles(LocaleHelper.getLanguage(this))
            showSearchDialog()
        }

        val intesabButton: Button = findViewById(R.id.button4)
        intesabButton.text = getString(R.string.button_intesab)
        intesabButton.setOnClickListener {
            startActivity(Intent(this, IntisabActivity::class.java))
        }
        val introductionButton: Button = findViewById(R.id.button3)
        introductionButton.text = getString(R.string.button_introduction)
        introductionButton.setOnClickListener {
            startActivity(Intent(this, IntroductionActivity::class.java))
        }

        loadSearchTitles(currentAppLanguage)
    }

    private fun updateNavHeaderTexts() {
        val headerView = navigationView.getHeaderView(0)
        val navHeaderText = headerView.findViewById<TextView>(R.id.nav_header_title)
        navHeaderText?.text = getString(R.string.nav_header_nooruddin)
    }

    private fun loadSearchTitles(language: String) {
        allTitlesForSearch = try {
            JsonUtils.loadFehristFromAssets(this, language)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading titles for language $language:", e)
            emptyList()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val appNameTextView: TextView = findViewById(R.id.left_text)
        appNameTextView.text = getString(R.string.app_name)

        val fehristButton: Button = findViewById(R.id.button1)
        fehristButton.text = getString(R.string.button_fehrist)

        val tabsiraButton: Button = findViewById(R.id.button2)
        tabsiraButton.text = getString(R.string.button_tabsira)

        val searchButton: Button = findViewById(R.id.button5)
        searchButton.text = getString(R.string.button_search)

        val intesabButton: Button = findViewById(R.id.button4)
        intesabButton.text = getString(R.string.button_intesab)
        val introductionButton: Button = findViewById(R.id.button3)
        introductionButton.text = getString(R.string.button_introduction)

        updateNavHeaderTexts()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val currentAppLanguage = LocaleHelper.getLanguage(this)
        when (item.itemId) {
            R.id.nav_language -> showLanguageDialog()
            R.id.nav_facebook -> openUrl("https://www.facebook.com/nooruddin.saani")
            R.id.nav_instagram -> openUrl("https://www.instagram.com/291_noor")
            R.id.nav_youtube -> openUrl("https://www.youtube.com/yourchannel")
            R.id.nav_share -> shareAppLink()
            R.id.nav_fehrist -> startActivity(Intent(this, FehristActivity::class.java))
            R.id.nav_tabsira -> startActivity(Intent(this, TabsiraActivity::class.java))
            R.id.nav_search -> {
                loadSearchTitles(currentAppLanguage)
                showSearchDialog()
            }
            R.id.nav_intesab -> {
                startActivity(Intent(this, IntisabActivity::class.java))
            }
            R.id.nav_introduction -> {
                startActivity(Intent(this, IntroductionActivity::class.java))
            }
            else -> return false
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showSearchDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null)
        dialog.setContentView(view)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val recyclerView = view.findViewById<RecyclerView>(R.id.search_recycler_view)
        val closeBtn = view.findViewById<ImageView>(R.id.close_button)

        val adapter = object : RecyclerView.Adapter<TitleViewHolder>() {
            var filteredList: List<Pair<String, String>> = emptyList()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_search_result, parent, false)
                return TitleViewHolder(v)
            }

            override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
                if (position >= filteredList.size) return
                val (id, title) = filteredList[position]
                holder.bind(title)
                holder.itemView.setOnClickListener {
                    dialog.dismiss()
                    val intent = Intent(this@MainActivity, PoemDetailActivity::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("CURRENT_LANGUAGE", LocaleHelper.getLanguage(this@MainActivity))
                    startActivity(intent)
                }
            }

            override fun getItemCount(): Int = filteredList.size

            fun filter(query: String) {
                val normalizedQuery = normalizeText(query)
                filteredList = if (normalizedQuery.isBlank()) {
                    emptyList()
                } else {
                    allTitlesForSearch.filter { (_, titleText) ->
                        normalizeText(titleText).contains(normalizedQuery, ignoreCase = true)
                    }
                }
                notifyDataSetChanged()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView.adapter = adapter
        adapter.filter("")

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        closeBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.itemTitle)
        fun bind(title: String) {
            titleView.text = title
        }
    }

    private fun normalizeText(text: String): String {
        val nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD)
        val pattern = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        return pattern.replace(nfdNormalizedString, "").lowercase(Locale.getDefault())
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_urdu),
            getString(R.string.language_hindi)
        )
        val languageCodes = arrayOf("en", "ur", "hi")
        val currentAppLanguage = LocaleHelper.getLanguage(this)
        val currentLangIndex = languageCodes.indexOf(currentAppLanguage).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.language_change))
            .setSingleChoiceItems(languages, currentLangIndex) { _, _ -> }
            .setPositiveButton(android.R.string.ok) { d, _ ->
                val selectedPosition = (d as AlertDialog).listView.checkedItemPosition
                if (selectedPosition != ListView.INVALID_POSITION) {
                    val selectedLanguageCode = languageCodes[selectedPosition]
                    if (selectedLanguageCode != LocaleHelper.getLanguage(this)) {
                        changeLanguage(selectedLanguageCode)
                    }
                }
                d.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create().apply {
                setOnDismissListener { drawer.closeDrawer(GravityCompat.START) }
                show()
                window?.let {
                    val params = it.attributes
                    val displayMetrics = resources.displayMetrics
                    params.width = (displayMetrics.widthPixels * 0.85).toInt()
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT
                    it.attributes = params
                }
            }
    }

    private fun changeLanguage(languageCode: String) {
        val currentActiveLanguage = LocaleHelper.getLanguage(this)
        if (currentActiveLanguage == languageCode) {
            return
        }
        Toast.makeText(this, getString(R.string.switching_language_toast, languageCode.uppercase(Locale.ROOT)), Toast.LENGTH_SHORT).show()
        LocaleHelper.setLocale(this, languageCode)
        recreate()
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.facebook_page), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.instagram_pages), Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error opening URL $url", e)
        }
    }

    private fun shareAppLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            val playStoreLink = "https://play.google.com/store/apps/details?id=$packageName"
            val messageBody = getString(R.string.share_message_body, getString(R.string.app_name), playStoreLink)
            putExtra(Intent.EXTRA_TEXT, messageBody)
        }
        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_sharing_app), Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error creating share chooser", e)
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

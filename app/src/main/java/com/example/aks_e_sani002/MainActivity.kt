package com.example.aks_e_sani002
import com.example.aks_e_sani002.utils.JsonUtils
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentLocale = resources.configuration.locales[0]
        language = currentLocale.language.lowercase(Locale.getDefault())

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

        val fehristButton: Button = findViewById(R.id.button1)
        fehristButton.setOnClickListener {
            startActivity(Intent(this, FehristActivity::class.java))
        }

        val searchButton: Button = findViewById(R.id.button5)
        searchButton.setOnClickListener {
            showSearchDialog()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_language -> {
                showLanguageDialog()
                return true
            }
            R.id.nav_facebook -> openUrl("https://www.facebook.com/nooruddin.saani")
            R.id.nav_instagram -> openUrl("https://www.instagram.com/291_noor")
            R.id.nav_youtube -> openUrl("https://www.youtube.com/yourchannel")
            R.id.nav_share -> shareAppLink()
            R.id.nav_fehrist -> startActivity(Intent(this, FehristActivity::class.java))
            R.id.nav_search -> {
                showSearchDialog()
                return true
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showSearchDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null)
        dialog.setContentView(view)

        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val recyclerView = view.findViewById<RecyclerView>(R.id.search_recycler_view)
        val closeBtn = view.findViewById<ImageView>(R.id.close_button)

        val allTitles = JsonUtils.loadFehristFromAssets(this, language)

        val adapter = object : RecyclerView.Adapter<TitleViewHolder>() {
            var filteredList = emptyList<Pair<String, String>>()

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
                    startActivity(intent)
                }
            }

            override fun getItemCount(): Int = filteredList.size

            fun filter(query: String) {
                filteredList = if (query.isNotBlank()) {
                    allTitles.filter { it.second.contains(query, ignoreCase = true) }
                } else {
                    emptyList()
                }
                notifyDataSetChanged()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.itemTitle)
        fun bind(title: String) {
            titleView.text = title
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.language_english),
            getString(R.string.language_urdu),
            getString(R.string.language_hindi)
        )
        val languageCodes = arrayOf("en", "ur", "hi")
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.language_change))
            .setItems(languages) { _, which ->
                changeLanguage(languageCodes[which])
                drawer.closeDrawer(GravityCompat.START)
            }
            .setNegativeButton(getString(android.R.string.cancel), null)
            .create()

        dialog.show()
        dialog.window?.let { window ->
            val params = window.attributes
            val displayMetrics = resources.displayMetrics
            params.width = (displayMetrics.widthPixels * 0.8).toInt()
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = params
        }
    }

    private fun changeLanguage(languageCode: String) {
        Toast.makeText(this, "Switching to ${languageCode.uppercase(Locale.ROOT)}", Toast.LENGTH_SHORT).show()

        this.language = languageCode.lowercase(Locale.getDefault())

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        recreate()
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open URL", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun shareAppLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, "Check out ${getString(R.string.app_name)} app: https://play.google.com/store/apps/details?id=$packageName")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

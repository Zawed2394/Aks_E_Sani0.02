package com.example.aks_e_sani002

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the header layout's TextView with app name
        val appNameTextView: TextView = findViewById(R.id.left_text)
        appNameTextView.text = getString(R.string.app_name)

        // Set up DrawerLayout and hamburger icon
        drawer = findViewById(R.id.drawer_layout)
        val drawerToggle: ImageButton = findViewById(R.id.drawer_toggle)
        drawerToggle.setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }

        // Set up NavigationView for language change and other items
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val fehristButton: Button = findViewById(R.id.button1)
        fehristButton.setOnClickListener {
            startActivity(Intent(this, FehristActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_language -> {
                showLanguageDialog()
                return true
            }
            R.id.nav_facebook -> {
                openUrl("https://www.facebook.com/nooruddin.saani")
            }
            R.id.nav_instagram -> {
                openUrl("https://www.instagram.com/291_noor")
            }
            R.id.nav_youtube -> {
                openUrl("https://www.youtube.com/yourchannel")
            }
            R.id.nav_share -> {
                shareAppLink()
            }
            R.id.nav_fehrist -> { // ✅ Fehrist nav item
                startActivity(Intent(this, FehristActivity::class.java))
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
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
        Toast.makeText(this, "Switching to ${languageCode.uppercase()}", Toast.LENGTH_SHORT).show()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun shareAppLink() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Aks-e-Sani app: https://play.google.com/store/apps/details?id=com.example.aks_e_sani002")
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

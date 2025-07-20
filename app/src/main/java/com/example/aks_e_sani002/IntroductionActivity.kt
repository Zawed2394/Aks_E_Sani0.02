package com.example.aks_e_sani002

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aks_e_sani002.utils.LocaleHelper

class IntroductionActivity : AppCompatActivity() {

    private val TAG = "IntroductionActivity"

    override fun attachBaseContext(newBase: Context) {
        val updatedContext = LocaleHelper.onAttach(newBase)
        Log.d(TAG, "Locale set in attachBaseContext: ${updatedContext.resources.configuration.locale}")
        super.attachBaseContext(updatedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        val language = LocaleHelper.getLanguage(this)
        Log.d(TAG, "Current language: $language")

        // Toolbar setup
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            Log.d(TAG, "Back button clicked")
            finish()
        }

        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.introduction_title)

        // Main text view setup
        val tarufTextView = findViewById<TextView>(R.id.taruf_text_view)

        // Set direction and alignment based on language
        if (language.lowercase() == "ur") {
            Log.d(TAG, "Setting Urdu RTL alignment")
            tarufTextView.textDirection = View.TEXT_DIRECTION_RTL
            tarufTextView.layoutDirection = View.LAYOUT_DIRECTION_RTL
            (tarufTextView.parent as View).layoutDirection = View.LAYOUT_DIRECTION_RTL

            tarufTextView.text = """
                
                
                اصل نام: محمد نور الدین
                قلمی نام: نور الدین ثانی
                والد محترم: محمد یوسف عرف بہادر (مرحوم)
                والدہ محترمہ: بی بی تجم النساء (مرحومہ)
                تعلیم: مڈل اسکول
                پیشہ: معمار
                تاریخ پیدائش: 1 جنوری 1979
                جائے پیدائش: صالح پور
                شریک حیات: ثمینہ خاتون
                اثمارِ حیات: آذان الٰہی، انا الٰہی، مہربان الٰہی، ثریا الٰہی
                استاد محترم: نوشاد ثمرؔ بگراسوی (بلند شہر)
                ساکن و پوسٹ: صالح پور، تھانہ پھلکا، ضلع کٹیہار (بہار)
                موبائل: 9717311363
            """.trimIndent()
        } else if (language.lowercase() == "hi") {
            Log.d(TAG, "Setting Hindi LTR alignment")
            tarufTextView.textDirection = View.TEXT_DIRECTION_LTR
            tarufTextView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            (tarufTextView.parent as View).layoutDirection = View.LAYOUT_DIRECTION_LTR

            tarufTextView.text = """
               
                
                वास्तविक नाम: मोहम्मद नूरुद्दीन
                उपनाम: नूरुद्दीन सानी
                सम्मानित पिता: मोहम्मद यूसुफ उर्फ बहादुर (स्वर्गीय)
                सम्मानित माता: बीबी तजमुल निसा (स्वर्गीय)
                शिक्षा: मिडिल स्कूल
                पेशा: वास्तुकार
                जन्म तिथि: 1 जनवरी 1979
                जन्म स्थान: सालेहपुर
                जीवनसाथी: समीना खातून
                संतान: आज़ान इलाही, अना इलाही, मेहरबान इलाही, सूर्या इलाही
                सम्मानित शिक्षक: नौशाद समर बग्रासवी (बुलंदशहर)
                निवास: सालेहपुर, थाना फुलका, जिला कटिहार (बिहार)
                मोबाइल: 9717311363
            """.trimIndent()
        } else {
            Log.d(TAG, "Setting English LTR alignment")
            tarufTextView.textDirection = View.TEXT_DIRECTION_LTR
            tarufTextView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            (tarufTextView.parent as View).layoutDirection = View.LAYOUT_DIRECTION_LTR

            tarufTextView.text = """
                
                
                Real Name: Mohammad Nooruddin
                Pen Name: Nooruddin Sani
                Father: Mohammad Yusuf alias Bahadur (Late)
                Mother: Bibi Tajmul Nisa (Late)
                Education: Middle School
                Occupation: Architect
                Date of Birth: January 1, 1979
                Place of Birth: Salehpur
                Spouse: Samina Khatoon
                Children: Azan Ilahi, Ana Ilahi, Mehrban Ilahi, Suraiya Ilahi
                Teacher: Naushad Samar Bagrassvi (Bulandshahr)
                Address: Salehpur, Thana Phulka, District Katihar (Bihar)
                Mobile: 9717311363
            """.trimIndent()
        }
    }
}

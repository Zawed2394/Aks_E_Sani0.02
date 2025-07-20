package com.example.aks_e_sani002

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aks_e_sani002.utils.LocaleHelper

class IntisabActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intisab)

        // Set toolbar title
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.intesab_title)

        // Back button
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        // Dedication content based on current language
        val dedicationView = findViewById<TextView>(R.id.dedication_text)
        val language = LocaleHelper.getLanguage(this) // Fix: use LocaleHelper

        val dedicationText = when (language) {
            "hi" -> """
                स्वर्गीय माता-पिता के नाम, जिनकी विशेष दुआओं ने ज़िंदगी की उलझनों को सुलझाया, 
                और उनकी ममता भरी छाया ने हिम्मत और हौसले को प्रेरणा दी और इस योग्य बनाया।

                ✦

                और उन शिक्षकों के नाम, जिनकी करुणा और प्रोत्साहन से लिखने और बोलने की समझ विकसित हुई।

                - नूरुद्दीन सानी
            """.trimIndent()

            "en" -> """
                Dedicated to my late father and mother, whose special prayers untangled the knots of life,
                and whose attention and shadow of care inspired courage and determination.

                ✦

                And to those teachers, whose kindness and encouragement awakened in me the consciousness to write and speak.

                - Nooruddin Sani
            """.trimIndent()

            else -> """
                مرحوم والد اور والدہ کے نام جن کی مخصوص دعاؤں نے زندگی کی گرہیں کھولیں، 
                اور ان کی توجہ وسایہئ ہما نے ہمت وحوصلہ کو مہمیز کیا اور اس لائق بنایا۔

                ٭

                اور اُن اساتذہ کرام کے نام جن کی توجہاتِ کرم فرمائی اور حوصلہ افزائی کے باعث کچھ لکھنے اور بولنے کا شعور پیدا ہوا۔

                - نورالدین ثانیؔ
            """.trimIndent()
        }

        dedicationView.text = dedicationText
    }
}

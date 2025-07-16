package com.example.aks_e_sani002.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aks_e_sani002.PoemDetailActivity
import com.example.aks_e_sani002.R
import com.example.aks_e_sani002.model.Item

class ItemAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberTextView: TextView = itemView.findViewById(R.id.itemNumber)
        val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        val layout: LinearLayout = itemView.findViewById(R.id.itemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        val resId = context.resources.getIdentifier(item.id, "string", context.packageName)
        val localizedTitle = if (resId != 0) context.getString(resId) else item.title

        val currentLang = context.resources.configuration.locales[0].language

        val numberText = if (currentLang == "ur") {
            toUrduDigits(position + 1)
        } else {
            (position + 1).toString()
        }

        if (currentLang == "ur") {
            holder.layout.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            holder.layout.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

        holder.numberTextView.text = "$numberText "
        holder.titleTextView.text = localizedTitle

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PoemDetailActivity::class.java)
            intent.putExtra("id", item.id) // 🔥 pass unique ID
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun toUrduDigits(number: Int): String {
        val urduDigits = listOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        return number.toString().map { urduDigits[it.digitToInt()] }.joinToString("")
    }
}

package com.example.aks_e_sani002.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aks_e_sani002.R
import com.example.aks_e_sani002.model.Section

class SectionAdapter(private val sections: List<Section>) :
    RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headingTextView: TextView = itemView.findViewById(R.id.sectionHeading)
        val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        val context = holder.itemView.context

        // ✅ Get localized section heading using ID
        val resId = context.resources.getIdentifier(section.id, "string", context.packageName)
        val localizedHeading = if (resId != 0) context.getString(resId) else section.heading

        holder.headingTextView.text = localizedHeading

        holder.itemsRecyclerView.layoutManager = LinearLayoutManager(context)
        holder.itemsRecyclerView.adapter = ItemAdapter(section.items)
    }

    override fun getItemCount(): Int = sections.size
} 
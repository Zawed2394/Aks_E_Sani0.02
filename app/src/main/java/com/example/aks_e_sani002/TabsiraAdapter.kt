package com.example.aks_e_sani002

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TabsiraAdapter(private val items: List<Tabsira>) :
    RecyclerView.Adapter<TabsiraAdapter.TabsiraViewHolder>() {

    class TabsiraViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberView: TextView = view.findViewById(R.id.tabsira_number)
        val titleView: TextView = view.findViewById(R.id.tabsira_title)
        val authorView: TextView = view.findViewById(R.id.tabsira_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabsiraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tabsira, parent, false)
        return TabsiraViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabsiraViewHolder, position: Int) {
        val item = items[position]
        holder.numberView.text = "${position + 1}."
        holder.titleView.text = item.title
        holder.authorView.text = item.author

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TabsiraDetailActivity::class.java)
            intent.putExtra("index", position)  // ✅ درست: صرف index بھیجنا کافی ہے
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}

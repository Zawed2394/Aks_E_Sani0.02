package com.example.aks_e_sani002

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(
    private var list: List<Pair<String, String>>,
    // Modify the lambda type to accept two String arguments
    private val onItemClickCallback: (id: String, title: String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    fun updateList(newList: List<Pair<String, String>>) {
        list = newList
        notifyDataSetChanged() // For better performance with lists, consider using DiffUtil
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.itemTitle) // Make sure R.id.itemTitle is in your item_search_result.xml

        // The bind function itself doesn't need to change much,
        // but the click listener within it (or defined in init) needs to pass both values.
        // Option 1: Set listener in init (common practice)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // Check for valid position
                    val item = list[position]
                    onItemClickCallback(item.first, item.second) // Pass both id and title
                }
            }
        }

        fun bind(item: Pair<String, String>) {
            titleText.text = item.second
            // Option 2: Set listener in bind (less common if ViewHolder is reused extensively without rebinding listener logic)
            // itemView.setOnClickListener {
            //     onItemClickCallback(item.first, item.second) // Pass both id and title
            // }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false) // Ensure this layout file exists and is correct
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

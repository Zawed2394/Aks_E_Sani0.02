package com.example.aks_e_sani002

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aks_e_sani002.utils.JsonUtils

class SearchDialogFragment(
    private val context: Context,
    private val language: String,
    private val onItemClick: (String, String) -> Unit
) : DialogFragment() {

    private lateinit var searchInput: EditText
    private lateinit var closeButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var poemList: List<Pair<String, String>>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_search)

        searchInput = dialog.findViewById(R.id.search_input)
        closeButton = dialog.findViewById(R.id.close_button)
        recyclerView = dialog.findViewById(R.id.search_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        poemList = JsonUtils.loadFehristFromAssets(context, language)
        adapter = SearchAdapter(poemList) { id, title ->
            onItemClick(id, title)
            dismiss()
        }
        recyclerView.adapter = adapter

        // ✅ یہی ہے وہ TextWatcher جو غلطی دے رہا تھا
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = poemList.filter {
                    it.second.contains(s.toString(), ignoreCase = true)
                }
                adapter.updateList(filteredList)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        closeButton.setOnClickListener {
            dismiss()
        }

        searchInput.requestFocus()
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}

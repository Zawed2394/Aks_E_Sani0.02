package com.example.aks_e_sani002.model

data class Section(
    val id: String,
    val heading: String,
    val items: List<Item>
)

data class Item(
    val id: String,
    val title: String,
    val content: String
)

package com.mychefassistant.framework.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Groceries")
data class GroceryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val kitchen: Int,

    val title: String,

    val value: String = "",

    val status: Boolean = false
)
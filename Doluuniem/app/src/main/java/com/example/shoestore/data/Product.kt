package com.example.shoestore.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val image: String = "",
    val description: String = "",
    var quantity: Double = 0.0,
    val category: Double = 0.0
) : Parcelable

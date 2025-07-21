package com.example.shoestore.data
data class Cart(
    var id: String? = null,
    var userId: String? = null,
    var name: String? = null,
    var price: Int = 0,
    var quantity: Int = 0,
    var image: String? = null,
    var productId: String = ""
) {
    constructor() : this(null, null, "", 0, 0, "" ,"")
}

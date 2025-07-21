package com.example.shoestore.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestore.R
import com.example.shoestore.data.Cart
import com.example.shoestore.data.Product
import com.example.shoestore.databinding.ItemProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductAdapter(private var list: List<Product>,
                     private val onItemClick: (Product) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)
    private var filteredList = list.toMutableList()

    init {
        filteredList = list.toMutableList() // đảm bảo khởi tạo đúng
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount() = filteredList.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredList[position]
        holder.binding.tvName.text = product.name
        holder.binding.tvPrice.text = String.format("%.0f VND", product.price)
        holder.binding.tvQuantity.text = String.format("số lượng : %.0f", product.quantity)

        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(R.drawable.loader)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.ivImage)

        holder.itemView.setOnClickListener {
            onItemClick(product)
        }
        holder.binding.btnBuy.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_quantity, null)
            val edtQuantity = dialogView.findViewById<EditText>(R.id.edtQuantity)
            val btnAdd = dialogView.findViewById<Button>(R.id.btnAddToCart)

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            btnAdd.setOnClickListener {
                val quantityText = edtQuantity.text.toString()
                if (quantityText.isNotEmpty()) {
                    val quantity = quantityText.toInt()

                    if (quantity > product.quantity) {
                        Toast.makeText(context, "Không đủ hàng", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Lấy userId từ Firebase Authentication
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId == null) {
                        Toast.makeText(context, "Vui lòng đăng nhập trước khi thêm vào giỏ", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Tính tổng giá (price * quantity)
                    val totalPrice = product.price.toInt() * quantity

                    // Tạo đối tượng Cart với tổng giá
                    val cart = Cart(
                        userId = userId,
                        name = product.name,
                        price = totalPrice, // Sử dụng tổng giá
                        quantity = quantity,
                        image = product.image,
                        productId = product.id
                    )

                    val db = FirebaseFirestore.getInstance()

                    // Thêm trực tiếp vào collection "cart"
                    db.collection("cart")
                        .add(cart)
                        .addOnSuccessListener { documentReference ->
                            cart.id = documentReference.id // Gán ID từ Firestore
                            // Trừ số lượng sản phẩm
                            val newQuantity = product.quantity - quantity
                            db.collection("products").document(product.id)
                                .update("quantity", newQuantity)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Đã thêm vào giỏ và cập nhật tồn kho", Toast.LENGTH_SHORT).show()
                                    product.quantity = newQuantity
                                    notifyItemChanged(position)
                                    dialog.dismiss()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Lỗi khi thêm giỏ hàng", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            dialog.show()
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Product>) {
        list = newList
        filteredList = newList.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            list.toMutableList()
        } else {
            list.filter {
                it.name.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }


}

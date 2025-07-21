import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoestore.data.Cart
import com.example.shoestore.databinding.ItemCartBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class CartAdapter(
    private val cartList: MutableList<Cart>,
    private val context: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount() = cartList.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvPrice.text = "${item.price} VND"
        holder.binding.tvQuantity.text = "Số lượng: ${item.quantity}"
        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.binding.ivImage)

        holder.binding.btnCancel.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val cartDocId = item.id ?: return@setOnClickListener

            db.collection("cart").document(cartDocId)
                .delete()
                .addOnSuccessListener {
                    // Cộng lại số lượng vào sản phẩm gốc
                    val productRef = db.collection("products").document(item.productId)

                    try {
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(productRef)
                            val currentQty = snapshot.getLong("quantity") ?: 0L
                            transaction.update(productRef, "quantity", FieldValue.increment(item.quantity.toLong()))
                        }.addOnSuccessListener {
                            Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show()
                            cartList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, cartList.size)
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, "Lỗi khôi phục số lượng: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Giao dịch thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Lỗi xóa đơn hàng", Toast.LENGTH_SHORT).show()
                }
        }

    }
}

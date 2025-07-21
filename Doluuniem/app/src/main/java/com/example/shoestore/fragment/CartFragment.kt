package com.example.shoestore.fragment

import CartAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoestore.activity.Payment
import com.example.shoestore.data.Cart
import com.example.shoestore.data.PayOSResponse
import com.example.shoestore.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartList = mutableListOf<Cart>()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var call: Call<PayOSResponse>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CartAdapter(cartList, requireContext())

        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter
        loadCartItems()

        binding.btnCheckout.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(requireContext(), "Add more product", Toast.LENGTH_SHORT).show()
            } else {
                var total = 0
                val detailBuilder = StringBuilder()
                cartList.forEach {
                    total += it.price * it.quantity
                    detailBuilder.append("${it.name} x ${it.quantity} = ${it.price * it.quantity} VND\n")
                }

                val intent = Intent(requireContext(), Payment::class.java)
                intent.putExtra("totalAmount", total)
                intent.putExtra("orderDetail", detailBuilder.toString())
                startActivity(intent)
            }
        }


    }
    private fun openCustomTab(url: String) {
        try {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
        } catch (e: Exception) {
            Log.e("CustomTab", "Failed to open: ${e.message}")
            // Nếu lỗi thì mở bằng trình duyệt mặc định
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

private fun loadCartItems() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid // Lấy userId hiện tại
    if (userId == null) {
        Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
        return
    }

    FirebaseFirestore.getInstance().collection("cart")
        .whereEqualTo("userId", userId) // Lọc theo userId
        .get()
        .addOnSuccessListener { result ->
            cartList.clear()
            for (doc in result) {
                val item = doc.toObject(Cart::class.java)
                item.id = doc.id  // gán id cho item để dùng xóa
                cartList.add(item)
            }
            adapter.notifyDataSetChanged()
            updateEmptyView()
        }
        .addOnFailureListener {
            Toast.makeText(requireContext(), "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show()
        }
}
    private fun updateEmptyView() {
        if (cartList.isEmpty()) {
            binding.recyclerViewCart.visibility = View.GONE
            binding.tvEmptyCart.visibility = View.VISIBLE
        } else {
            binding.recyclerViewCart.visibility = View.VISIBLE
            binding.tvEmptyCart.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        call?.cancel()
    }
}



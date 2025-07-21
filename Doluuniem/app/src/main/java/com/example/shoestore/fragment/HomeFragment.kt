package com.example.shoestore.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoestore.R
import com.example.shoestore.activity.ProductAdapter
import com.example.shoestore.data.Product
import com.example.shoestore.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val productList = ArrayList<Product>()
    private lateinit var adapter: ProductAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductAdapter(productList) { product ->
            val fragment = ProductDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("product", product)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHome.adapter = adapter
        loadProducts()
        filterProduct()
        setupCategoryButtons()
    }

    @SuppressLint("NotifyDataSetChanged")


    private fun loadProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (doc in result) {
                    val p = doc.toObject(Product::class.java)
                    p.id = doc.id
                    productList.add(p)
                }
                adapter.updateList(productList)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
    }


    private fun filterProduct() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Không làm gì khi submit, vì lọc realtime rồi
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })


    }

    private fun setupCategoryButtons() {
        // tất cả
        binding.btnTatCa.setOnClickListener {
            adapter.updateList(productList)
            checkEmptyList()
        }

        // mùa xuân = 1.0
        binding.btnMuaXuan.setOnClickListener {
            filterByCategory(1.0)
        }

        // mùa hạ = 2.0
        binding.btnMuaHa.setOnClickListener {
            filterByCategory(2.0)
        }

        // mùa thu = 3.0
        binding.btnMuaThu.setOnClickListener {
            filterByCategory(3.0)
        }

        // mùa đông = 4.0
        binding.btnMuaDong.setOnClickListener {
            filterByCategory(4.0)
        }
    }

    //loc product
    private fun filterByCategory(category: Double) {
        val filtered = productList.filter { it.category == category }
        adapter.updateList(filtered)
        checkEmptyList()
    }

    //hien thi text neu product rong
    private fun checkEmptyList() {
        if (adapter.itemCount == 0) {
            Toast.makeText(requireContext(), "Không có sản phẩm", Toast.LENGTH_SHORT).show()
            binding.tvEmpty.visibility = View.VISIBLE
        }else {
            binding.tvEmpty.visibility = View.GONE
        }
    }
}

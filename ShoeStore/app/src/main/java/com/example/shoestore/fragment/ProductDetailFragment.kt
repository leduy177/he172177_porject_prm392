package com.example.shoestore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.shoestore.data.Product
import com.example.shoestore.databinding.FragmentProductDetailBinding


class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getParcelable<Product>("product")
        product?.let {
            binding.tvName.text = it.name
            binding.tvPrice.text = "${it.price} VND"
            binding.tvQuantity.text = "Số lượng: ${it.quantity}"
            //binding.tvCategory.text = "Category: ${it.category}"
            binding.tvDescription.text = it.description

            Glide.with(this)
                .load(it.image)
                .into(binding.ivImage)


            binding.btnBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }}


}
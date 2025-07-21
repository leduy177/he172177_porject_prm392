//package com.example.shoestore.activity
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.example.shoestore.R
//import com.example.shoestore.databinding.MainActivityBinding
//import com.example.shoestore.fragment.CartFragment
//import com.example.shoestore.fragment.HomeFragment
//import com.example.shoestore.fragment.ProfileFragment
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: MainActivityBinding
//    private lateinit var authen: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = MainActivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        authen = FirebaseAuth.getInstance()
//
//        // Mặc định hiển thị Home
//        loadFragment(HomeFragment())
//
//        // Bắt sự kiện click BottomNavigation
//        binding.bottomNav.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.home -> loadFragment(HomeFragment())
//                R.id.profile -> loadFragment(ProfileFragment())
//                R.id.cart -> loadFragment(CartFragment())
//                else -> false
//            }
//            true
//        }
//
//    }
//
//    private fun loadFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//    }
//
//}
package com.example.shoestore.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.shoestore.R
import com.example.shoestore.databinding.MainActivityBinding
import com.example.shoestore.fragment.CartFragment
import com.example.shoestore.fragment.HomeFragment
import com.example.shoestore.fragment.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var authen: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authen = FirebaseAuth.getInstance()

        // Mặc định hiển thị Home
        loadFragment(HomeFragment())

        // Bắt sự kiện click BottomNavigation
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.profile -> loadFragment(ProfileFragment())
                R.id.cart -> loadFragment(CartFragment())
                else -> false
            }
            true
        }


    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


}
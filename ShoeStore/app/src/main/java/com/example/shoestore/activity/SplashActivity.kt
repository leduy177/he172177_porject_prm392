package com.example.shoestore.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoestore.R
import com.example.shoestore.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding;
    private lateinit var authen : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authen = FirebaseAuth.getInstance()

        Glide.with(this)
            .asGif()
            .load(R.drawable.hello)
            .into(binding.loading)


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 5000)


    }

}
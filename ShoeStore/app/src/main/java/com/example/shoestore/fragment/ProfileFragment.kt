package com.example.shoestore.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shoestore.activity.LoginActivity
import com.example.shoestore.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Lấy thông tin người dùng hiện tại
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvEmail.text = currentUser.email ?: "No email"
            binding.tvUid.text = currentUser.uid ?: "No UID"
        } else {
            binding.tvEmail.text = "Not logged in"
            binding.tvUid.text = "Not logged in"
            // Nếu chưa đăng nhập, chuyển về màn hình đăng nhập
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        // Xử lý nút Đăng xuất
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        // Xử lý nút Thay đổi mật khẩu
        binding.btnChangePassword.setOnClickListener {
            if (currentUser != null && currentUser.email != null) {
                // Gửi email đặt lại mật khẩu
                auth.sendPasswordResetEmail(currentUser.email!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Password reset email has been sent to ${currentUser.email}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error sending email: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Password reset email could not be sent. Please log in again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
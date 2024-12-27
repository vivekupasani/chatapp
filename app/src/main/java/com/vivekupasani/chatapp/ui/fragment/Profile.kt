package com.vivekupasani.chatapp.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.FragmentProfileBinding
import com.vivekupasani.chatapp.ui.Activity.EditProfile
import com.vivekupasani.chatapp.ui.Activity.OnBoard
import com.vivekupasani.chatapp.viewModels.ProfileViewModel

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var signOutDialog: Dialog

    companion object {
        const val Name: String = "name/vivekupasani/single"
        const val About: String = "about/vivekupasani/single"
        const val Email: String = "email/vivekupasani/single"
        const val password: String = "password/vivekupasani/single"
        const val userId: String = "userId/vivekupasani/single"
        const val profilePic: String = "profilePic/vivekupasani/single"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        signOutDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialouge_box)
            window?.setBackgroundDrawableResource(R.drawable.dialouge_box_background)
        }

        isLoading(true)
        setUpObservers()
        viewModel.fetchUserData()

        binding.btnSignOut.setOnClickListener { onSignOutBtnClick() }
        binding.btnEditProfile.setOnClickListener {
            onEditBtnClick()
        }
    }

    private fun onSignOutBtnClick() {
        val btnLogout = signOutDialog.findViewById<Button>(R.id.button_logout)
        val btnCancel = signOutDialog.findViewById<Button>(R.id.button_cancel)
        signOutDialog.show()
        signOutDialog.setCancelable(false)

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), OnBoard::class.java))
            activity?.finish()
        }

        btnCancel.setOnClickListener {
            signOutDialog.dismiss()
        }
    }

    private fun setUpObservers() {
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                isLoading(false)
                Glide.with(this).load(user.profilePicURL)
                    .placeholder(R.drawable.profile_placeholder).into(binding.profile)

                binding.emailAddress.setText(user.email)
                binding.fullName.setText(user.userName)
                binding.about.setText(user.about)
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            isLoading(false)
            errorMessage?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.dataLoding.visibility = View.VISIBLE
            binding.dataLoding.startShimmerAnimation()
            binding.dataLoaded.visibility = View.INVISIBLE
        } else {
            binding.dataLoding.visibility = View.INVISIBLE
            binding.dataLoding.stopShimmerAnimation()
            binding.dataLoaded.visibility = View.VISIBLE
        }
    }

    private fun onEditBtnClick() {
        val intent = Intent(requireContext(), EditProfile::class.java).apply {
            putExtra("title", "Edit Profile")
            putExtra(Profile.Name, binding.fullName.text.toString())
            putExtra(Profile.About, binding.about.text.toString())
            putExtra(Profile.Email, binding.emailAddress.text.toString())
            putExtra(Profile.password, viewModel.user.value?.password)
            putExtra(Profile.profilePic, viewModel.user.value?.profilePicURL)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the view binding to prevent memory leaks
    }
}

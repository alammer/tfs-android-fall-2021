package com.example.tfs.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tfs.R
import com.example.tfs.databinding.FragmentProfileBinding
import com.example.tfs.util.drawUserInitials
import com.example.tfs.util.toPx
import com.example.tfs.util.toast
import com.example.tfs.util.viewbinding.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val profileViewModel: ProfileViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        profileViewModel.profileScreenState.observe(viewLifecycleOwner) {
            processProfileScreenState(it)
        }

        profileViewModel.fetchUser(requireArguments().getInt(CONTACT_ID, -1))
    }

    private fun processProfileScreenState(response: ProfileScreenState) {
        when (response) {
            is ProfileScreenState.Result -> {
                with(viewBinding) {
                    response.user?.apply {
                        tvProfileName.text = userName
                        avatarUrl?.let { userImage ->
                            imgProfileUser.setImageURI(userImage.toUri())
                        } ?: imgProfileUser.drawUserInitials(
                            userName,
                            PROFILE_USER_IMAGE_WIDTH.toPx
                        )  //быдловатый вариант с размером
                        when (userState) {
                            "active" -> tvProfileState.setTextColor(Color.GREEN)
                            "idle" -> tvProfileState.setTextColor(Color.YELLOW)
                        }
                        tvProfileState.text = userState
                    } ?: { tvProfileName.text = "User info not available" }
                }
                //viewBinding.loadingProgress.isVisible = false
            }
            ProfileScreenState.Loading -> {
                // viewBinding.loadingProgress.isVisible = true
            }
            is ProfileScreenState.Error -> {
                context.toast(response.error.message)
                //streamViewModel.retrySubscribe()
                //viewBinding.loadingProgress.isVisible = false
            }
        }
    }

    private fun initViews() {
        viewBinding.btnProfileNavBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {

        private const val CONTACT_ID = "contact_id"
        private const val PROFILE_USER_IMAGE_WIDTH = 185

        fun newInstance(contactId: Int = -1): ProfileFragment {
            return ProfileFragment().apply {
                arguments = bundleOf(
                    CONTACT_ID to contactId,
                )
            }
        }
    }
}
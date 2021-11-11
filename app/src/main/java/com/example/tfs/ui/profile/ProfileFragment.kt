package com.example.tfs.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tfs.R
import com.example.tfs.databinding.FragmentProfileBinding
import com.example.tfs.util.drawUserInitials
import com.example.tfs.util.setUserState
import com.example.tfs.util.toPx
import com.example.tfs.util.viewbinding.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val profileViewModel: ProfileViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        profileViewModel.profile.observe(viewLifecycleOwner, { contact ->
            contact?.apply {
                with(viewBinding) {
                    tvProfileName.text = name
                    profileData?.status?.let { userStatus ->
                        tvProfileStatus.text = userStatus
                    } ?: tvProfileStatus.apply { visibility = View.GONE }
                    tvProfileState.setUserState(1)
                    avatarUrl?.let { userImage ->
                        imgProfileUser.setImageURI(userImage.toUri())
                    } ?: imgProfileUser.drawUserInitials(
                        name,
                        PROFILE_USER_IMAGE_WIDTH.toPx
                    )  //быдловатый вариант с размером
                }
            }
        })

        profileViewModel.fetchProfile(requireArguments().getInt(CONTACT_ID, -1))
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
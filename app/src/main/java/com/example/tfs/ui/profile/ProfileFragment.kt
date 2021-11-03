package com.example.tfs.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.example.tfs.databinding.FragmentProfileBinding
import com.example.tfs.domain.contacts.Contact
import com.example.tfs.util.*
import com.example.tfs.util.viewbinding.viewBinding


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

        val requestContact = TestMockDataGenerator.mockContactList
            .firstOrNull { it.userId == requireArguments().getInt(CONTACT_ID, -1) }

        requestContact?.apply {
            with(viewBinding) {
                tvProfileName.text = userName
                tvProfileStatus.text = userStatus
                tvProfileState.setUserState(userState)
                userImage?.let {
                    imgProfileUser.setImageResource(it)
                } ?: imgProfileUser.drawUserInitials(userName, PROFILE_USER_IMAGE_WIDTH.dpToPixels() )
                btnProfileNavBack.setOnClickListener {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    companion object {
        private const val CONTACT_ID = "contact_id"
        private const val PROFILE_USER_IMAGE_WIDTH = 37
        fun newInstance(contactId: Int = -1): ProfileFragment {
            val fragment = ProfileFragment()
            val arguments = Bundle()
            arguments.putInt(CONTACT_ID, contactId)
            fragment.arguments = arguments
            return fragment
        }
    }
}
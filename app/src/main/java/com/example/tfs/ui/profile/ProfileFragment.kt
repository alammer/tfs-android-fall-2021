package com.example.tfs.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.example.tfs.R
import com.example.tfs.databinding.FragmentProfileBinding
import com.example.tfs.di.AppDI
import com.example.tfs.ui.contacts.elm.ContactEffect
import com.example.tfs.ui.profile.elm.ProfileEffect
import com.example.tfs.ui.profile.elm.ProfileEvent
import com.example.tfs.ui.profile.elm.ProfileState
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.ui.topic.elm.TopicEffect
import com.example.tfs.ui.topic.elm.TopicEvent
import com.example.tfs.ui.topic.elm.TopicState
import com.example.tfs.util.drawUserInitials
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.toPx
import com.example.tfs.util.viewbinding.viewBinding
import com.squareup.picasso.Picasso
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

class ProfileFragment :
    ElmFragment<ProfileEvent, ProfileEffect, ProfileState>(R.layout.fragment_profile) {

    override val initEvent: ProfileEvent = ProfileEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    private val userId by lazy {
        requireArguments().getInt(USER_ID, -1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        store.accept(ProfileEvent.Ui.InitialLoad(userId))
    }

    override fun createStore(): Store<ProfileEvent, ProfileEffect, ProfileState> =
        AppDI.INSTANCE.elmProfileStoreFactory.provide()

    override fun render(state: ProfileState) {
        with(viewBinding) {
            loading.root.isVisible = state.isFetching

            state.user?.apply {
                tvProfileName.text = userName
                avatarUrl?.let { userImage ->
                    Picasso.get().load(userImage)
                        .resize(PROFILE_AVATAR_WIDTH.toPx, PROFILE_AVATAR_WIDTH.toPx)
                        .centerCrop().into(imgProfileUser)
                } ?: imgProfileUser.drawUserInitials(
                    userName,
                    PROFILE_USER_IMAGE_WIDTH.toPx
                )  //быдловатый вариант с размером
                when (userState) {
                    "active" -> tvProfileState.setTextColor(Color.GREEN)
                    "idle" -> tvProfileState.setTextColor(Color.YELLOW)
                    "offline" -> tvProfileState.setTextColor(Color.RED)
                }
                tvProfileState.text = userState
            } ?: { tvProfileName.text = "User info not available" }
        }
    }

    override fun handleEffect(effect: ProfileEffect) {
        when (effect) {
            is ProfileEffect.FetchError -> {
                with(requireView()) {
                    showSnackbarError("User info unavailable")
                }
            }
            is ProfileEffect.BackNavigation -> {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun initViews() {
        viewBinding.btnProfileNavBack.setOnClickListener {
            store.accept(ProfileEvent.Ui.BackToContacts)
        }
    }

    companion object {

        private const val USER_ID = "user_id"
        private const val PROFILE_USER_IMAGE_WIDTH = 185

        fun newInstance(userId: Int = -1): ProfileFragment {
            return ProfileFragment().apply {
                arguments = bundleOf(
                    USER_ID to userId,
                )
            }
        }
    }
}

private const val PROFILE_AVATAR_WIDTH = 185
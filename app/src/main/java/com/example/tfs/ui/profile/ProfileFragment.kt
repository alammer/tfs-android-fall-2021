package com.example.tfs.ui.profile

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.tfs.R
import com.example.tfs.appComponent
import com.example.tfs.databinding.FragmentProfileBinding
import com.example.tfs.di.DaggerProfileComponent
import com.example.tfs.ui.profile.elm.*
import com.example.tfs.util.drawUserInitials
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.viewbinding.viewBinding
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import vivid.money.elmslie.storepersisting.retainStoreHolder
import javax.inject.Inject

class ProfileFragment :
    ElmFragment<ProfileEvent, ProfileEffect, ProfileState>(R.layout.fragment_profile) {

    override val initEvent: ProfileEvent = ProfileEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    @Inject
    lateinit var profileActor: ProfileActor

    private val userId by lazy {
        requireArguments().getInt(USER_ID, -1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun createStore(): Store<ProfileEvent, ProfileEffect, ProfileState> =
        ProfileStore.provide(ProfileState(userId = userId), profileActor)

    override val storeHolder by retainStoreHolder(storeProvider = ::createStore)

    override fun render(state: ProfileState) {
        with(viewBinding) {
            loading.root.isVisible = state.isFetching

            state.user?.apply {
                tvProfileName.text = userName
                avatarUrl?.let { userImage ->
                    Glide.with(viewBinding.root)
                        .load(userImage)
                        .centerCrop()
                        .placeholder(R.drawable.loading_img_animation)
                        .error(R.drawable.broken_img)
                        .into(imgProfileUser)
                        .waitForLayout()
                } ?: imgProfileUser.drawUserInitials(userName)

                tvProfileState.setUserState(userState)
            } ?: { tvProfileName.text = getString(R.string.user_unvailable_message) }
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

    override fun onAttach(context: Context) {
        DaggerProfileComponent.builder().appComponent(context.appComponent).build()
            .inject(this)
        super.onAttach(context)
    }

    private fun initViews() {
        viewBinding.btnProfileNavBack.setOnClickListener {
            store.accept(ProfileEvent.Ui.BackToContacts)
        }
    }

    companion object {

        private const val USER_ID = "user_id"

        fun newInstance(userId: Int = -1): ProfileFragment {
            return ProfileFragment().apply {
                arguments = bundleOf(
                    USER_ID to userId,
                )
            }
        }
    }
}

fun TextView.setUserState(userState: String) {

    when (userState) {
        "active" -> setTextColor(Color.GREEN)
        "idle" -> setTextColor(Color.YELLOW)
        "offline" -> setTextColor(Color.RED)
    }
    text = userState
}
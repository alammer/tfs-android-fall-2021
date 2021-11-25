package com.example.tfs.ui.streams.viewpager

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamContainerBinding
import com.example.tfs.di.AppDI
import com.example.tfs.ui.streams.elm.Effect
import com.example.tfs.ui.streams.elm.Event
import com.example.tfs.ui.streams.elm.State
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store

class StreamContainerFragment :
    ElmFragment<Event, Effect, State>(R.layout.fragment_stream_container) {

    override val initEvent: Event = Event.Ui.Init

    private val viewBinding by viewBinding(FragmentStreamContainerBinding::bind)

    /*private val streamViewModel: StreamViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
*/
    private val viewPagerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            when (position) {
                0 -> store.accept(Event.Ui.FetchSubscribedStreams)
                1 -> store.accept(Event.Ui.FetchRawStreams)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.FetchError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on load stream list")
                }
            }
        }
    }

    override fun createStore(): Store<Event, Effect, State> =
        AppDI.INSTANCE.elmStoreFactory.provide()

    override fun render(state: State) {
        viewBinding.loading.root.isVisible = state.isFetching
        //state.error?.let { throwable ->  errorText.text = throwable.userMessage(requireContext()) }  //are we gonna need last error body?
    }


    override fun onDestroy() {
        super.onDestroy()
        viewBinding.viewPager.unregisterOnPageChangeCallback(viewPagerPageChangeCallback)
    }

    private fun initViews() {
        val pagerAdapter = FragmentPagerAdapter(requireActivity())

        with(viewBinding) {
            viewPager.adapter = pagerAdapter
            TabLayoutMediator(
                appbar.tabLayout,
                viewPager,
            ) { tab, position ->
                val tabNames = listOf("Subscribed", "All streams")
                tab.text = tabNames[position]
            }.attach()

            viewPager.registerOnPageChangeCallback(viewPagerPageChangeCallback)

            appbar.etSearchInput.doAfterTextChanged {
                store.accept(Event.Ui.ChangeSearchQuery(it.toString()))
                if (it.isNullOrBlank()) {
                    appbar.etSearchInput.clearFocus()
                }
            }
        }
    }
}
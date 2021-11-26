package com.example.tfs.ui.streams.viewpager

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.example.tfs.R
import com.example.tfs.databinding.FragmentStreamContainerBinding
import com.example.tfs.di.AppDI
import com.example.tfs.ui.streams.StreamViewModel
import com.example.tfs.ui.streams.viewpager.elm.ViewPagerEffect
import com.example.tfs.ui.streams.viewpager.elm.ViewPagerEvent
import com.example.tfs.ui.streams.viewpager.elm.ViewPagerState
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import java.util.concurrent.TimeUnit

class StreamContainerFragment :
    ElmFragment<ViewPagerEvent, ViewPagerEffect, ViewPagerState>(R.layout.fragment_stream_container) {

    override val initEvent: ViewPagerEvent = ViewPagerEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentStreamContainerBinding::bind)

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchStream: PublishSubject<String> = PublishSubject.create()

    private val viewPagerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            when (position) {
                0 -> store.accept(ViewPagerEvent.Ui.FetchSubscribedStreams)
                1 -> store.accept(ViewPagerEvent.Ui.FetchRawStreams)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun handleEffect(effect: ViewPagerEffect) {
        when (effect) {
            is ViewPagerEffect.FetchError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on load stream list")
                }
            }
        }
    }

    override fun createStore(): Store<ViewPagerEvent, ViewPagerEffect, ViewPagerState> =
        AppDI.INSTANCE.elmStoreFactory.provide()

    override fun render(state: ViewPagerState) {
        viewBinding.loading.root.isVisible = state.isFetching
        //state.error?.let { throwable ->  errorText.text = throwable.userMessage(requireContext()) }  //are we gonna need last error body?
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        viewBinding.viewPager.unregisterOnPageChangeCallback(viewPagerPageChangeCallback)
    }

    private fun subscribeToSearchStreams() {
        searchStream
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .doOnNext { Log.i("StreamContainerFragment", "Function called: subscribeToSearchStreams() $it") }
            .subscribeBy(
                onNext = { store.accept(ViewPagerEvent.Ui.ChangeSearchQuery(it)) }
            )
            .addTo(compositeDisposable)
    }

    private fun initViews() {
        val pagerAdapter = FragmentPagerAdapter(requireActivity())

        subscribeToSearchStreams()

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
                searchStream.onNext(it.toString())
                if (it.isNullOrBlank()) {
                    appbar.etSearchInput.clearFocus()
                }
            }
        }
    }
}
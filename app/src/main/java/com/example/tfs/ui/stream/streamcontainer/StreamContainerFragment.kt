package com.example.tfs.ui.stream.streamcontainer

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.example.tfs.R
import com.example.tfs.appComponent
import com.example.tfs.databinding.FragmentStreamContainerBinding
import com.example.tfs.di.DaggerStreamContainerComponent
import com.example.tfs.ui.stream.streamcontainer.elm.*
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
import javax.inject.Inject

class StreamContainerFragment :
    ElmFragment<StreamContainerEvent, StreamContainerEffect, StreamContainerState>(R.layout.fragment_stream_container) {

    override val initEvent: StreamContainerEvent = StreamContainerEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentStreamContainerBinding::bind)

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchStream: PublishSubject<String> = PublishSubject.create()

    @Inject
    lateinit var streamContainerActor: StreamContainerActor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun handleEffect(effect: StreamContainerEffect) {
        when (effect) {
            is StreamContainerEffect.QueryError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on load stream list")
                }
            }
        }
    }

    override fun createStore(): Store<StreamContainerEvent, StreamContainerEffect, StreamContainerState> =
        StreamContainerStore.provide(actor = streamContainerActor)

    override fun render(state: StreamContainerState) {

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onAttach(context: Context) {
        DaggerStreamContainerComponent.builder().appComponent(context.appComponent).build()
            .inject(this)
        super.onAttach(context)
    }

    private fun subscribeToSearchStreams(adapter: FragmentPagerAdapter) {
        searchStream
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = {
                    adapter.setCurrentQuery(it)
                    store.accept(StreamContainerEvent.Ui.ChangeSearchQuery(it))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun initViews() {

        val pagerAdapter = FragmentPagerAdapter(this)

        subscribeToSearchStreams(pagerAdapter)

        with(viewBinding) {
            viewPager.adapter = pagerAdapter
            TabLayoutMediator(
                appbar.tabLayout,
                viewPager,
            ) { tab, position ->
                val tabNames = listOf("Subscribed", "All Streams")
                tab.text = tabNames[position]
            }.attach()

            appbar.etSearchInput.doAfterTextChanged {
                searchStream.onNext(it.toString())
                if (it.isNullOrBlank()) {
                    appbar.etSearchInput.clearFocus()
                }
            }
        }
    }
}
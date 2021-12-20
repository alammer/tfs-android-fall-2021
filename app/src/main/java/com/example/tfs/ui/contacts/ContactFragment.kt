package com.example.tfs.ui.contacts

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.appComponent
import com.example.tfs.common.baseitems.BaseLoader
import com.example.tfs.common.baseitems.LoaderItem
import com.example.tfs.databinding.FragmentContactBinding
import com.example.tfs.di.DaggerContactComponent
import com.example.tfs.ui.contacts.adapter.ContactAdapter
import com.example.tfs.ui.contacts.adapter.items.ContactItem
import com.example.tfs.ui.contacts.elm.*
import com.example.tfs.ui.profile.ProfileFragment
import com.example.tfs.util.showSnackbarError
import com.example.tfs.util.viewbinding.viewBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.core.store.Store
import vivid.money.elmslie.storepersisting.retainStoreHolder
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ContactFragment :
    ElmFragment<ContactEvent, ContactEffect, ContactState>(R.layout.fragment_contact) {

    override val initEvent: ContactEvent = ContactEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentContactBinding::bind)

    @Inject
    lateinit var contactActor: ContactActor

    private val contactAdapter = ContactAdapter(getItemTypes())

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchContact: PublishSubject<String> = PublishSubject.create()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun createStore(): Store<ContactEvent, ContactEffect, ContactState> =
        ContactStore.provide(contactActor)

    override val storeHolder by retainStoreHolder(storeProvider = ::createStore)

    override fun render(state: ContactState) {
        viewBinding.loading.root.isVisible = state.isLoading
        viewBinding.empty.root.isVisible = state.isEmpty
        contactAdapter.updateData(state.contactList)
    }

    override fun handleEffect(effect: ContactEffect) {
        when (effect) {
            is ContactEffect.LoadingError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on update contact list")
                }
            }
            is ContactEffect.ShowUser -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment.newInstance(effect.userId))
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    }

    override fun onAttach(context: Context) {
        DaggerContactComponent.builder().appComponent(context.appComponent).build()
            .inject(this)
        super.onAttach(context)
    }

    override fun onDestroyView() {
        viewBinding.rvContacts.clearOnScrollListeners()  //TODO
        super.onDestroyView()
    }

    private fun initViews() {

        subscribeToSearchContacts()

        with(viewBinding) {

            etSearchInput.doAfterTextChanged {
                searchContact.onNext(it.toString())
                if (it.isNullOrBlank()) {
                    etSearchInput.clearFocus()
                }
            }

            with(rvContacts) {
                setHasFixedSize(true)

                contactAdapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

                adapter = contactAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun getItemTypes() = listOf(
        ContactItem(::clickOnContact),
        LoaderItem(),
    )

    private fun clickOnContact(contactId: Int) {
        store.accept(ContactEvent.Ui.ContactClicked(contactId))
    }

    private fun subscribeToSearchContacts() {
        searchContact
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = { store.accept(ContactEvent.Ui.SearchQueryChange(it)) }
            )
            .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}
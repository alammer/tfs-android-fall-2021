package com.example.tfs.ui.contacts

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfs.R
import com.example.tfs.databinding.FragmentContactsBinding
import com.example.tfs.di.AppDI
import com.example.tfs.ui.contacts.adapter.ContactViewAdapter
import com.example.tfs.ui.contacts.elm.ContactEffect
import com.example.tfs.ui.contacts.elm.ContactEvent
import com.example.tfs.ui.contacts.elm.ContactState
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
import java.util.concurrent.TimeUnit

class ContactsFragment :
    ElmFragment<ContactEvent, ContactEffect, ContactState>(R.layout.fragment_contacts) {

    override val initEvent: ContactEvent = ContactEvent.Ui.Init

    private val viewBinding by viewBinding(FragmentContactsBinding::bind)

    private lateinit var contactListAdapter: ContactViewAdapter

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchContact: PublishSubject<String> = PublishSubject.create()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun createStore(): Store<ContactEvent, ContactEffect, ContactState> =
        AppDI.INSTANCE.elmContactStoreFactory.provide()

    override fun render(state: ContactState) {
        viewBinding.loading.root.isVisible = state.isFetching
        contactListAdapter.submitList(state.contactList)
    }

    override fun handleEffect(effect: ContactEffect) {
        when (effect) {
            is ContactEffect.FetchError -> {
                with(requireView()) {
                    effect.error.message?.let { showSnackbarError(it) }
                        ?: showSnackbarError("Error on fetch list of users")
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

    private fun initViews() {

        subscribeToSearchContacts()

        contactListAdapter = ContactViewAdapter { contactId: Int ->
                store.accept(ContactEvent.Ui.ContactClicked(contactId))
        }

        with(viewBinding) {
            rvContacts.adapter = contactListAdapter
            rvContacts.layoutManager = LinearLayoutManager(context)

            etSearchInput.doAfterTextChanged {
                searchContact.onNext(it.toString())
                if (it.isNullOrBlank()) {
                    etSearchInput.clearFocus()
                }
            }
        }
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
package com.example.tfs.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.StreamRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

internal class ContactsViewModel : ViewModel() {

    private val repository = StreamRepositoryImpl
    private var currentSearchQuery = ""

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchContact: PublishSubject<String> = PublishSubject.create()

    val contactScreenState: LiveData<ContactScreenState> get() = _contactScreenState
    private var _contactScreenState: MutableLiveData<ContactScreenState> = MutableLiveData()

    init {
        subscribeToSearchContacts()
    }

    fun fetchContacts(searchQuery: String) {
        currentSearchQuery = searchQuery
        searchContact.onNext(currentSearchQuery)
    }

    private fun subscribeToSearchContacts() {
        searchContact
            .subscribeOn(Schedulers.io())
            .doOnNext { _contactScreenState.postValue(ContactScreenState.Loading) }
            .debounce(500L, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { query -> repository.getContacts(query) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _contactScreenState.value = ContactScreenState.Result(it) },
                onError = { _contactScreenState.value = ContactScreenState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {

        const val INITIAL_QUERY: String = ""
    }

}
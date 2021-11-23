package com.example.tfs.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.domain.contacts.ContactRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

internal class ProfileViewModel : ViewModel() {

    private val repository = ContactRepositoryImpl()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val profileScreenState: LiveData<ProfileScreenState> get() = _profileScreenState
    private var _profileScreenState: MutableLiveData<ProfileScreenState> = MutableLiveData()

    fun fetchUser(contactId: Int) {
        repository.getUser(contactId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _profileScreenState.value = ProfileScreenState.Error(it) },
                onComplete = { _profileScreenState.value = ProfileScreenState.Result(null) },
                onSuccess = { _profileScreenState.value = ProfileScreenState.Result(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
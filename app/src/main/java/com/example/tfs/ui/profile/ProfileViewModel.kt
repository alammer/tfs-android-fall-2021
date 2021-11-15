package com.example.tfs.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.RepositoryImpl
import com.example.tfs.domain.contacts.DomainUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class ProfileViewModel : ViewModel() {

    private val repository = RepositoryImpl()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val profile: LiveData<DomainUser?> get() = _profile
    private val _profile = MutableLiveData<DomainUser?>()

    fun fetchUser(contactId: Int) {
        repository.fetchUser(contactId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { _profile.value = null },
                onSuccess = { _profile.value = it }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
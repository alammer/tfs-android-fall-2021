package com.example.tfs.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.StreamRepositoryImpl
import com.example.tfs.domain.contacts.Contact

class ProfileViewModel : ViewModel() {

    private val repository = StreamRepositoryImpl

    val profile: LiveData<Contact?> get() = _profile
    private val _profile = MutableLiveData<Contact?>()

    fun fetchProfile(contactId: Int) {
        _profile.postValue(repository.contactList.firstOrNull { it.id == contactId })
    }
}
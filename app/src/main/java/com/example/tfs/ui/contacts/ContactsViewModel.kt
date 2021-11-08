package com.example.tfs.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfs.data.StreamRepositoryImpl
import com.example.tfs.domain.contacts.Contact

class ContactsViewModel : ViewModel() {

    private val repository = StreamRepositoryImpl

    val contactList: LiveData<List<Contact>?> get() = _contactList
    private val _contactList = MutableLiveData<List<Contact>?>()

    init {
        fetchContacts()
    }

    private fun fetchContacts() {
        _contactList.postValue(repository.contactList)
    }
}
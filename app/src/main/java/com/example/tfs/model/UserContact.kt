package com.example.tfs.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserContact(val name: String?, val phoneNumber: String?) : Parcelable

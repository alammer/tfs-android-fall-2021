package com.example.tfs.util

import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tfs.ContactService

fun Context.hasPermission() =
    ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED


fun AppCompatActivity.requestPermission() = ActivityCompat.requestPermissions(this,
    arrayOf(READ_CONTACTS), CONTACT_PERMISSION_CODE)


const val CONTACT_PERMISSION_CODE = 1
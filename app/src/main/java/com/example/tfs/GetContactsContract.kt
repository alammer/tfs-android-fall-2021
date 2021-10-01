package com.example.tfs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract

class GetContactsContract : ActivityResultContract<CharSequence?, ArrayList<String>?>() {
    override fun createIntent(context: Context, input: CharSequence?): Intent {
        return Intent(context, SecondActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<String>? {
        if(resultCode != Activity.RESULT_OK) return null
        intent ?: return null
        return intent.getStringArrayListExtra("contacts")
    }
}
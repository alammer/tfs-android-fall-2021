package com.example.tfs

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.example.tfs.model.UserContact

class GetContactsContract : ActivityResultContract<Unit?, ArrayList<UserContact>?>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(context, SecondActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<UserContact>? {
        if (resultCode != Activity.RESULT_OK) return null
        intent ?: return null
        return intent.getParcelableArrayListExtra("contacts")
    }
}
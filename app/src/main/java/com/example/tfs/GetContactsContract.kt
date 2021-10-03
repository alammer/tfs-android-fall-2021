package com.example.tfs

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class GetContactsContract : ActivityResultContract<Unit?, Array<String>?>() {
    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(context, SecondActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Array<String>? {
        if (resultCode != Activity.RESULT_OK) return null
        intent ?: return null
        return intent.getStringArrayExtra("contacts")
    }
}
package com.example.tfs.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

fun View.hideSoftKeyboard() {
    try {
        val im: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    //clearFocus()
    /*if (this is EditText) { //move to uiTopic elm
        text.clear()
    }*/
}

fun View.showSnackbarError(
    error: String,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    Snackbar.make(this, error, duration).show()
}
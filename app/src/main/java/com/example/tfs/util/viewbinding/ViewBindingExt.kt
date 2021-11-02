package com.example.tfs.util.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.properties.ReadOnlyProperty


fun <F : Fragment, VB : ViewBinding> Fragment.viewBinding(
    viewBinder: (F) -> VB,
): ReadOnlyProperty<F, VB> {
    return ViewBindingProperty(viewBinder)
}

inline fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(
    crossinline vbFactory: (View) -> T,
    crossinline viewProvider: (F) -> View = Fragment::requireView,
): ReadOnlyProperty<F, T> {
    return viewBinding { fragment: F -> vbFactory(viewProvider(fragment)) }
}

inline fun <F : BottomSheetDialogFragment, VB : ViewBinding> BottomSheetDialogFragment.viewBinding(
    crossinline vbFactory: (View) -> VB,
    crossinline viewProvider: (F) -> View = Fragment::requireView,
): ReadOnlyProperty<F, VB> {
    return viewBinding { fragment: F -> vbFactory(viewProvider(fragment)) }
}
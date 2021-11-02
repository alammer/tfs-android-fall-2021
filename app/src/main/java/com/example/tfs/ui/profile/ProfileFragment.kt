package com.example.tfs.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.example.tfs.databinding.FragmentProfileBinding
import com.example.tfs.databinding.FragmentStreamsBinding
import com.example.tfs.util.viewbinding.viewBinding
import com.google.android.material.imageview.ShapeableImageView

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

        //получить из листа контактов
        //val requestContact = requireArguments().getInt(ARG_MESSAGE, -1)
        with(viewBinding) {
            btnProfileNavBack.setOnClickListener {
                //TODO("по фигме неявно куда возвращаться")
            }
        }
    }

    companion object {
        private const val ARG_MESSAGE = "contact_id"
        fun newInstance(contactId: Int = -1): ProfileFragment {
            val fragment = ProfileFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_MESSAGE, contactId)
            fragment.arguments = arguments
            return fragment
        }
    }
}
package com.example.tfs.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.google.android.material.imageview.ShapeableImageView

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var userAvatarView: ShapeableImageView
    private lateinit var userName: TextView
    private lateinit var userStatus: TextView
    private lateinit var userState: TextView
    private lateinit var btnNavBack: ImageView
    private var requestContact = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestContact = requireArguments().getInt(ARG_MESSAGE, -1)
        initViews(view)
    }

    private fun initViews(view: View) {
        userAvatarView = view.findViewById(R.id.imgProfileUser)
        userName = view.findViewById(R.id.tvProfileName)
        userStatus = view.findViewById(R.id.tvProfileStatus)
        userState = view.findViewById(R.id.tvProfileState)

        btnNavBack = view.findViewById(R.id.btnProfileNavBack)

        btnNavBack.setOnClickListener {
            //TODO("по фигме неявно куда возвращаться")
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
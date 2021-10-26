package com.example.tfs.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.R
import com.example.tfs.data.Reaction
import com.example.tfs.data.TopicCell
import com.example.tfs.presentation.streams.StreamsFragment
import com.example.tfs.presentation.topic.emoji.EmojiDialogFragment
import com.example.tfs.presentation.topic.TopicAdapterCallback
import com.example.tfs.presentation.topic.TopicFragment
import com.example.tfs.ui.topic.TopicViewAdapter
import com.example.tfs.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //bottomNavigationView = findViewById(R.id.bottomNavView)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, TopicFragment())
                .commit()
        }

//        supportFragmentManager.addOnBackStackChangedListener {
//            when (supportFragmentManager.findFragmentById(R.id.main_container)) {
//                is DetailFragment -> bottomNavigationView.menu.setGroupCheckable(0, false, true)
//                is MainScreenFragment -> {
//                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
//                    bottomNavigationView.menu.findItem(R.id.home)?.run { isChecked = true }
//                }
//                is ProfileFragment -> {
//                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
//                    bottomNavigationView.menu.findItem(R.id.profile)?.run { isChecked = true }
//                }
//            }
//        }

//        bottomNavigationView.setOnItemSelectedListener {
//            val fromDetailFragment =
//                supportFragmentManager.findFragmentById(R.id.main_container) is DetailFragment
//            when (it.itemId) {
//                R.id.home -> {
//                    if (fromDetailFragment) {
//                        supportFragmentManager.popBackStack()
//                        loadFragment(MainScreenFragment())
//                    } else {
//                        if (!it.isChecked) {
//                            loadFragment(MainScreenFragment())
//                        }
//                    }
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.profile -> {
//                    if (!it.isChecked) {
//                        loadFragment(ProfileFragment(), fromDetailFragment)
//                    }
//                    return@setOnItemSelectedListener true
//                }
//            }
//            return@setOnItemSelectedListener false
//        }
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
}




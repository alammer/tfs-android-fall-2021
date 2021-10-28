package com.example.tfs.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.example.tfs.presentation.contacts.ContactsFragment
import com.example.tfs.presentation.profile.ProfileFragment
import com.example.tfs.presentation.streams.StreamsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        //bottomNavigationView = findViewById(R.id.bottomNavView)

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
        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
}




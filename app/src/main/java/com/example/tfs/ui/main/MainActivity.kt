package com.example.tfs.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.example.tfs.ui.contacts.ContactsFragment
import com.example.tfs.ui.profile.ProfileFragment
import com.example.tfs.ui.streams.viewpager.StreamContainer
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, StreamContainer())
                .commitAllowingStateLoss()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_to_streams -> {
                    loadFragment(StreamContainer())
                    return@setOnItemSelectedListener true
                }

                R.id.nav_to_contacts -> {
                    loadFragment(ContactsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_to_profile -> {
                    loadFragment(ProfileFragment.newInstance())
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when (supportFragmentManager.findFragmentById(R.id.fragment_container)) {
            is StreamContainer -> bottomNavigationView.menu.findItem(R.id.nav_to_streams).isChecked =
                true
            is ContactsFragment -> bottomNavigationView.menu.findItem(R.id.nav_to_contacts).isChecked =
                true
            is ProfileFragment -> bottomNavigationView.menu.findItem(R.id.nav_to_profile).isChecked =
                true
        }
    }

    fun hideBottomNav() {
        bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNav() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
        .add(R.id.fragment_container, fragment)
        .addToBackStack(null)
        .commitAllowingStateLoss()
    }
}




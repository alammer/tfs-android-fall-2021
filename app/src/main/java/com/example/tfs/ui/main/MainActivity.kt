package com.example.tfs.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tfs.R
import com.example.tfs.ui.contacts.ContactsFragment
import com.example.tfs.ui.profile.ProfileFragment
import com.example.tfs.ui.stream.streamcontainer.StreamContainerFragment
import com.example.tfs.ui.topic.TopicFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, StreamContainerFragment())
                //.add(R.id.fragment_container, TopicFragment.newInstance("swimming turtles", "general") )
                .commitAllowingStateLoss()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener {
            onNavItemSelected(it.itemId)
            true
        }

        bottomNavigationView.setOnItemReselectedListener {

        }

        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.findFragmentById(R.id.fragment_container)) {
                is TopicFragment -> bottomNavigationView.visibility = View.GONE
                is StreamContainerFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.findItem(R.id.nav_to_streams).isChecked = true
                }
                is ContactsFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.findItem(R.id.nav_to_contacts).isChecked = true
                }
                is ProfileFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.findItem(R.id.nav_to_profile).isChecked = true
                }
            }
        }
    }

/*    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }*/


    private fun onNavItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_to_streams -> loadFragment(StreamContainerFragment())
            R.id.nav_to_contacts -> loadFragment(ContactsFragment())
            R.id.nav_to_profile -> loadFragment(ProfileFragment.newInstance())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}




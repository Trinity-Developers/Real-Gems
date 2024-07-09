package com.trinitydevelopers.realgemsadmin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.trinitydevelopers.realgemsadmin.bottomfragments.AddFragment
import com.trinitydevelopers.realgemsadmin.bottomfragments.ExploreFragment
import com.trinitydevelopers.realgemsadmin.bottomfragments.ProfileFragment
import com.trinitydevelopers.realgemsadmin.databinding.ActivityMainBinding
import com.trinitydevelopers.realgemsadmin.fragments.AllGemsFragment
import com.trinitydevelopers.realgemsadmin.fragments.GemsCategoryFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentFragmentTag: String? = null

    companion object {
        private const val HOME_FRAGMENT_TAG = "HOME_FRAGMENT"
        private const val ADD_FRAGMENT_TAG = "ADD_FRAGMENT"
        private const val PROFILE_FRAGMENT_TAG = "PROFILE_FRAGMENT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        if (savedInstanceState == null) {
            // Load the default fragment on initial creation
            replaceFragment(ExploreFragment(), HOME_FRAGMENT_TAG,false)
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val currentFragment = supportFragmentManager.primaryNavigationFragment
        if (currentFragment != null && currentFragment.tag == tag) {
            return
        }
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.replace(R.id.frame_container, fragment, tag)
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNow()
        currentFragmentTag = tag
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    replaceFragment(ExploreFragment(), HOME_FRAGMENT_TAG,false)
                    true
                }
                R.id.bottom_add -> {
                    replaceFragment(AddFragment(), ADD_FRAGMENT_TAG,false)
                    true
                }

                R.id.bottom_profile -> {
                    replaceFragment(ProfileFragment(), PROFILE_FRAGMENT_TAG,false)
                    true
                }
                R.id.bottom_allProduct -> {
                    replaceFragment(GemsCategoryFragment(), PROFILE_FRAGMENT_TAG,false)
                    true
                }
                else -> false
            }
        }
    }
    override fun onBackPressed() {
        if (currentFragmentTag != HOME_FRAGMENT_TAG) {
            replaceFragment(ExploreFragment(), HOME_FRAGMENT_TAG, false)
            binding.bottomNavigation.selectedItemId = R.id.bottom_home
        } else {
            super.onBackPressed()
        }
    }
}
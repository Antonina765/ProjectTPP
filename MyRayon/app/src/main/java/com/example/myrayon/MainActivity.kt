package com.example.myrayon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myrayon.databinding.ActivityMainBinding
import com.example.myrayon.ui.auth.LoginActivity
import com.example.myrayon.ui.chat.ChatFragment
import com.example.myrayon.ui.news.NewsFragment
import com.example.myrayon.ui.profile.ProfileFragment
import com.example.myrayon.ui.requests.RequestsFragment
import com.example.myrayon.ui.votes.VotesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("MyRayonPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        replaceFragment(RequestsFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_requests -> replaceFragment(RequestsFragment())
                R.id.nav_news -> replaceFragment(NewsFragment())
                R.id.nav_votes -> replaceFragment(VotesFragment())
                R.id.nav_chat -> replaceFragment(ChatFragment())
                R.id.nav_profile -> {
                    if (getSharedPreferences("MyRayonPrefs", MODE_PRIVATE).getInt("userId", -1) == -1) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        replaceFragment(ProfileFragment())
                    }
                    true
                }
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
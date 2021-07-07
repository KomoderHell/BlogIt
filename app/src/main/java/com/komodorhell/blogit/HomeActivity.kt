package com.komodorhell.blogit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.replace
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        drawerToggle = ActionBarDrawerToggle(this, navigationDrawer, R.string.open, R.string.close)
        navigationDrawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationBar.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feed -> {
                    Toast.makeText(applicationContext, "feed", Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FeedFragment()).commit()
                    navigationDrawer.closeDrawer(GravityCompat.START)
                }

                R.id.search -> {
                    Toast.makeText(applicationContext, "search", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) return true


        return super.onOptionsItemSelected(item)

    }
}
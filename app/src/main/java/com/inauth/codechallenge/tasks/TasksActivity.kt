package com.inauth.codechallenge.tasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inauth.codechallenge.R
import com.inauth.codechallenge.util.obtainViewModel
import com.inauth.codechallenge.util.replaceFragmentInActivity
import com.inauth.codechallenge.util.setupActionBar


class TasksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(false)
        }

        setupViewFragment()
    }

    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(TasksFragment.newInstance(), R.id.contentFrame)
    }

    fun obtainViewModel(): TasksViewModel = obtainViewModel(TasksViewModel::class.java)

}

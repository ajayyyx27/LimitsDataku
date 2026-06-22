package com.applimiter.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class BlockActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)
        val appName = intent.getStringExtra("appName") ?: "This app"
        val minutes = intent.getIntExtra("minutes", 10)
        findViewById<TextView>(R.id.blockMessage).text =
            "You have used $appName for $minutes minutes this hour.\nCome back next hour!"
        findViewById<Button>(R.id.goHomeButton).setOnClickListener {
            val home = Intent(Intent.ACTION_MAIN)
            home.addCategory(Intent.CATEGORY_HOME)
            home.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(home)
            finish()
        }
    }
    override fun onBackPressed() {
        val home = Intent(Intent.ACTION_MAIN)
        home.addCategory(Intent.CATEGORY_HOME)
        home.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(home)
        finish()
    }
}

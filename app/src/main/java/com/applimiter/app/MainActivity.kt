package com.applimiter.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private lateinit var listView: ListView
    private lateinit var pm: PackageManager
    private val REQUEST_PICK_APP = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pm = packageManager
        listView = findViewById(R.id.limitsListView)
        findViewById<Button>(R.id.addAppButton).setOnClickListener {
            startActivityForResult(Intent(this, AppPickerActivity::class.java), REQUEST_PICK_APP)
        }
        findViewById<Button>(R.id.accessibilityButton).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        findViewById<Button>(R.id.usageAccessButton).setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_APP && resultCode == Activity.RESULT_OK) {
            val packageName = data?.getStringExtra("packageName") ?: return
            val label = data.getStringExtra("label") ?: packageName
            showMinutesDialog(packageName, label)
        }
    }

    private fun showMinutesDialog(packageName: String, label: String) {
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.hint = "e.g. 10"
        LimitsStore.getLimits(this)[packageName]?.let { input.setText(it.toString()) }
        AlertDialog.Builder(this)
            .setTitle("Minutes per hour for $label")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val minutes = input.text.toString().toIntOrNull() ?: 10
                LimitsStore.setLimit(this, packageName, minutes)
                refreshList()
                Toast.makeText(this, "$label limited to $minutes min/hour", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshList() {
        val limits = LimitsStore.getLimits(this)
        val packages = limits.keys.toList()
        listView.adapter = object : ArrayAdapter<String>(this, 0, packages) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_item_limit, parent, false)
                val pkg = packages[position]
                val minutes = limits[pkg] ?: 0
                val icon = view.findViewById<ImageView>(R.id.appIcon)
                val name = view.findViewById<TextView>(R.id.appName)
                val mins = view.findViewById<TextView>(R.id.appMinutes)
                val removeBtn = view.findViewById<Button>(R.id.removeButton)
                try {
                    icon.setImageDrawable(pm.getApplicationIcon(pkg))
                    name.text = pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0))
                } catch (e: Exception) { name.text = pkg }
                mins.text = "$minutes min / hour"
                view.setOnClickListener { showMinutesDialog(pkg, name.text.toString()) }
                removeBtn.setOnClickListener {
                    LimitsStore.removeLimit(this@MainActivity, pkg)
                    refreshList()
                }
                return view
            }
        }
    }
}

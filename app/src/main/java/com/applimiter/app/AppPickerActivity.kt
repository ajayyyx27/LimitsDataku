package com.applimiter.app

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class AppPickerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_picker)
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { pm.getApplicationLabel(it).toString() }
        val listView = findViewById<ListView>(R.id.appListView)
        listView.adapter = object : ArrayAdapter<Any>(this, 0, apps) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_item_app, parent, false)
                val app = apps[position]
                view.findViewById<ImageView>(R.id.appIcon).setImageDrawable(pm.getApplicationIcon(app.packageName))
                view.findViewById<TextView>(R.id.appName).text = pm.getApplicationLabel(app).toString()
                return view
            }
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            val app = apps[position]
            val result = Intent()
            result.putExtra("packageName", app.packageName)
            result.putExtra("label", pm.getApplicationLabel(app).toString())
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}

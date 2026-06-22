package com.applimiter.app

import android.content.Context
import org.json.JSONObject

object LimitsStore {
    private const val PREFS = "applimiter_prefs"
    private const val KEY_LIMITS = "limits_json"

    fun getLimits(context: Context): MutableMap<String, Int> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LIMITS, "{}") ?: "{}"
        val obj = JSONObject(json)
        val map = mutableMapOf<String, Int>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            map[k] = obj.getInt(k)
        }
        return map
    }

    fun saveLimits(context: Context, map: Map<String, Int>) {
        val obj = JSONObject()
        for ((k, v) in map) obj.put(k, v)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_LIMITS, obj.toString()).apply()
    }

    fun setLimit(context: Context, packageName: String, minutes: Int) {
        val map = getLimits(context)
        map[packageName] = minutes
        saveLimits(context, map)
    }

    fun removeLimit(context: Context, packageName: String) {
        val map = getLimits(context)
        map.remove(packageName)
        saveLimits(context, map)
    }
}

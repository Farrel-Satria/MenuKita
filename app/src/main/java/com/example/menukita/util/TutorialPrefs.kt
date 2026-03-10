package com.example.menukita.util

import android.content.Context

object TutorialPrefs {
    private const val PREFS = "menukita_prefs"
    private const val KEY_TUTORIAL = "tutorial_done"

    fun isDone(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_TUTORIAL, false)
    }

    fun setDone(context: Context, done: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_TUTORIAL, done)
            .apply()
    }
}

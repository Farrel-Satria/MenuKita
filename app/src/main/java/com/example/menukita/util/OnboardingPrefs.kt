package com.example.menukita.util

import android.content.Context

object OnboardingPrefs {
    private const val PREFS = "menukita_prefs"
    private const val KEY_DONE = "onboarding_done"

    fun isDone(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_DONE, false)
    }

    fun setDone(context: Context, done: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DONE, done)
            .apply()
    }
}

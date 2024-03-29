package com.ngapak.dev.javacell.utils

import android.content.Context
import android.widget.Toast

object Utils {
    fun showToast(ctx: Context, msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
    }
}
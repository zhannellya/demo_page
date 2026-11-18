package com.adoptme.app.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.dialPhone(phone: String) {
    val digits = phone.filter { it.isDigit() || it == '+' }
    if (digits.isBlank()) return
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits"))
    startActivity(intent)
}

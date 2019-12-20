package me.digitalby.lr3

import java.text.SimpleDateFormat
import java.util.*

class DateTimeHelper {
    companion object {
        fun getFormattedDate(): String {
            val pattern = "yyyy-MM-dd HH:mm:ss"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return simpleDateFormat.format(Date())
        }
    }
}
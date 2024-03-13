package util

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

object Formatting {
    val formatterISO8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val formatterSimpleDate = SimpleDateFormat("yyyy-MM-dd")
}
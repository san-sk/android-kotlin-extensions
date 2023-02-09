/**
List of extension functions used for android view system.
**/


/**
used to strike the text
*/
fun TextView.strikeThrough() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

/**
set visibility
*/
fun View.setVisibility(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}

/**
set color
*/
fun TextView.setColor(color: String?) {
    this.setTextColor(Color.parseColor(color ?: "#0b1219"))
}

/**
set HTML content
*/
fun TextView.applyHtml() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT);
    } else {
        this.text = Html.fromHtml(text.toString())
    }
}

/**
showToast
*/
fun Context.showToast(message: String?) {
    Toast.makeText(
            this,
            message ?: "",
            Toast.LENGTH_SHORT
    ).show()
}


/**
change color to substring
*/
fun String.colorized(word: String, argb: Int): Spannable {
    val spannable: Spannable = SpannableString(this)
    var substringStart = 0
    var start: Int
    while (this.indexOf(word, substringStart).also { start = it } >= 0) {
        spannable.setSpan(ForegroundColorSpan(argb), start, start + word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(StyleSpan(Typeface.BOLD), start, start + word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        substringStart = start + word.length
    }
    return spannable
}
  

/**
input: List<String>
output: Bulleted list.
*/
fun List<String>.toBulletedList(): CharSequence {
    return SpannableString(this.joinToString("\n")).apply {
        this@toBulletedList.foldIndexed(0) { index, acc, span ->
            val end = acc + span.length + if (index != this@toBulletedList.size - 1) 1 else 0
            this.setSpan(BulletSpan(16), acc, end, 0)
            end
        }
    }
}


/**
input: List<String>
output: returns the bulleted list with foregorund color span.
*/
fun List<String>.toFormattedBulletList(separator: String, color: Int): CharSequence {
    return SpannableString(this.joinToString("\n")).apply {
        this@toFormattedBulletList.foldIndexed(0) { index, acc, span ->
            val end = acc + span.length + if (index != this@toFormattedBulletList.size - 1) 1 else 0
            this.setSpan(BulletSpan(16), acc, end, 0)
            if (span.indexOf(separator) != -1) {
                this.setSpan(ForegroundColorSpan(color), acc + span.indexOf(separator), end, 0)
            }
            end
        }
    }
}

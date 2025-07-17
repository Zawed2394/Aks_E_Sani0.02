package com.example.aks_e_sani002

import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.view.MotionEvent
import android.widget.TextView

class LongClickLinkMovementMethod : LinkMovementMethod() {
    private val longClickHandler = Handler(Looper.getMainLooper())
    private var isLongPressed = false
    private val LONG_CLICK_TIME = 500L // Long press duration in milliseconds

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.actionMasked

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
                val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY
                val layout = widget.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())

                val link = buffer.getSpans(off, off, LongClickableSpan::class.java)
                if (link.isNotEmpty()) {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]))
                    longClickHandler.postDelayed({
                        link[0].onLongClick(widget, link[0].wordObj)
                        isLongPressed = true
                    }, LONG_CLICK_TIME)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                longClickHandler.removeCallbacksAndMessages(null)
                if (!isLongPressed) {
                    val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
                    val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY
                    val layout = widget.layout
                    val line = layout.getLineForVertical(y)
                    val off = layout.getOffsetForHorizontal(line, x.toFloat())

                    val link = buffer.getSpans(off, off, LongClickableSpan::class.java)
                    if (link.isNotEmpty()) {
                        link[0].onClick(widget)
                        return true
                    }
                }
                isLongPressed = false
            }
            MotionEvent.ACTION_CANCEL -> {
                longClickHandler.removeCallbacksAndMessages(null)
                isLongPressed = false
                Selection.removeSelection(buffer)
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    companion object {
        private var sInstance: LongClickLinkMovementMethod? = null

        fun getInstance(): MovementMethod {
            if (sInstance == null) {
                sInstance = LongClickLinkMovementMethod()
            }
            return sInstance!!
        }
    }
}
package de.mchllngr.quickero.util.swipe

import android.graphics.Canvas
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class SwipeItemTouchCallback(
    private val swipeBackground: View,
    @Px private val deviceScreenWidth: Int,
    private val delegate: Delegate
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.START or ItemTouchHelper.END
) {

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        delegate.move(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        swipeDir: Int
    ) {
        delegate.remove(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val halfDeviceScreenWidth: Float = deviceScreenWidth / 2f
        val absDX = abs(dX)
        val calculatedDX = if (absDX <= halfDeviceScreenWidth) absDX else halfDeviceScreenWidth - (absDX - halfDeviceScreenWidth)

        swipeBackground.y = viewHolder.itemView.top.toFloat()
        swipeBackground.alpha = calculatedDX / halfDeviceScreenWidth

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface Delegate {

        fun move(
            fromPosition: Int,
            toPosition: Int
        )

        fun remove(position: Int)
    }
}

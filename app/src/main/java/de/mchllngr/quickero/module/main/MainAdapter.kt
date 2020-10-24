package de.mchllngr.quickero.module.main

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import butterknife.BindView
import butterknife.ButterKnife
import de.mchllngr.quickero.R
import de.mchllngr.quickero.model.ApplicationModel
import de.mchllngr.quickero.module.main.MainAdapter.MainViewHolder
import de.mchllngr.quickero.util.CustomAnimatorListener
import java.util.*

/**
 * [Adapter] for handling the shown items.
 */
class MainAdapter
/**
 * Constructor for initialising the [MainAdapter].
 */(
    /**
     * Current [Context].
     */
    private val context: Context,
    /**
     * [List] of shown items.
     */
    var items: MutableList<ApplicationModel>,
    /**
     * Listener for notifying a drag-start.
     */
    private val startDragListener: StartDragListener
) : RecyclerView.Adapter<MainViewHolder>() {
    /**
     * Returns the `items`.
     */
    /**
     * Indicates whether the Reorder-Mode is enabled or disabled.
     */
    private var reorderMode = false
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return MainViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(
        holder: MainViewHolder,
        position: Int
    ) {
        val applicationModel = items[position]
        holder.icon?.setImageDrawable(applicationModel.iconDrawable)
        holder.name?.text = applicationModel.name
        if (reorderMode) {
            var drawable: Drawable? = VectorDrawableCompat.create(context.resources, R.drawable.ic_reorder_black_24dp, null)
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.reorder_icon_color))
                holder.handle!!.setImageDrawable(drawable)
            }
            holder.handle?.setOnTouchListener { v: View?, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_DOWN) startDragListener.onStartDrag(holder)
                false
            }
            if (holder.handle?.visibility != View.VISIBLE) {
                holder.handle?.apply {
                    animate()
                        .alpha(1f)
                        .setDuration(100)
                        .setListener(object : CustomAnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                alpha = 0f
                                visibility = View.VISIBLE
                            }
                        })
                        .start()
                }
            } else holder.handle?.alpha = 1f
        } else {
            if (holder.handle?.visibility == View.VISIBLE) {
                holder.handle?.apply {
                    animate()
                        .alpha(0f)
                        .setDuration(100)
                        .setListener(object : CustomAnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                alpha = 1f
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                visibility = View.GONE
                            }
                        })
                        .start()
                }
            } else holder.handle?.alpha = 0f
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Updates the `items`.
     */
    fun updateItems(items: MutableList<ApplicationModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    /**
     * Adds an `item` at `position`.
     */
    fun add(
        position: Int,
        item: ApplicationModel
    ) {
        var position = position
        if (position < 0) position = 0
        if (position > itemCount) position = itemCount
        this.items.add(position, item)
        notifyItemInserted(position)
    }

    /**
     * Removes the `item`.
     */
    fun remove(item: ApplicationModel) {
        val position = items.indexOf(item)
        if (position < 0) return
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Moves an item from `fromPosition` to `toPosition`.
     */
    fun move(
        fromPosition: Int,
        toPosition: Int
    ) {
        if (fromPosition < toPosition) for (i in fromPosition until toPosition) Collections.swap(items, i, i + 1) else for (i in fromPosition downTo toPosition + 1) Collections.swap(
            items, i, i - 1
        )
        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Returns the [ApplicationModel] at `position`.
     */
    operator fun get(position: Int): ApplicationModel? {
        return if (position < 0 || position >= itemCount) null else items[position]
    }

    /**
     * Sets the Reorder-Mode.
     */
    fun setReorderMode(enable: Boolean) {
        reorderMode = enable
        notifyDataSetChanged()
    }

    /**
     * [RecyclerView.ViewHolder] for holding all the [View]s.
     */
    class MainViewHolder constructor(view: View?) : RecyclerView.ViewHolder(view!!) {
        @BindView(R.id.icon)
        var icon: ImageView? = null

        @BindView(R.id.name)
        var name: TextView? = null

        @BindView(R.id.handle)
        var handle: ImageView? = null

        init {
            ButterKnife.bind(this, view!!)
        }
    }

    /**
     * Interface for notifying a drag-start.
     */
    interface StartDragListener {
        /**
         * Gets called when a drag starts.
         */
        fun onStartDrag(viewHolder: MainViewHolder)
    }
}

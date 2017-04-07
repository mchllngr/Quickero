package de.mchllngr.quickopen.module.main;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.model.ApplicationModel;

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter} for handling the shown items.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    /**
     * Current {@link Context}.
     */
    private final Context context;
    /**
     * {@link List} of shown items.
     */
    private List<ApplicationModel> items;
    /**
     * Listener for notifying a drag-start.
     */
    private StartDragListener startDragListener;
    /**
     * Indicates whether the Reorder-Mode is enabled or disabled.
     */
    private boolean reorderMode;

    /**
     * Constructor for initialising the {@link MainAdapter}.
     */
    MainAdapter(Context context,
                List<ApplicationModel> items,
                StartDragListener startDragListener) {
        this.context = context;
        this.items = items;
        this.startDragListener = startDragListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ApplicationModel applicationModel = items.get(position);

        holder.icon.setImageDrawable(applicationModel.iconDrawable);
        holder.name.setText(applicationModel.name);

        if (reorderMode) {
            Drawable drawable = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_reorder_black_24px, null);
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.reorder_icon_color));
                holder.handle.setImageDrawable(drawable);
            }

            holder.handle.setOnTouchListener((v, event) -> {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN)
                    if (startDragListener != null)
                        startDragListener.onStartDrag(holder);

                return false;
            });

            if (holder.handle.getVisibility() != View.VISIBLE) {
                holder.handle.animate()
                        .alpha(1f)
                        .setDuration(100)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                holder.handle.setAlpha(0f);
                                holder.handle.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) { /* empty */ }

                            @Override
                            public void onAnimationCancel(Animator animation) { /* empty */ }

                            @Override
                            public void onAnimationRepeat(Animator animation) { /* empty */ }
                        })
                        .start();
            } else
                holder.handle.setAlpha(1f);

        } else {
            if (holder.handle.getVisibility() == View.VISIBLE) {
                holder.handle.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                holder.handle.setAlpha(1f);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.handle.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) { /* empty */ }

                            @Override
                            public void onAnimationRepeat(Animator animation) { /* empty */ }
                        })
                        .start();
            } else
                holder.handle.setAlpha(0f);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Returns the {@code items}.
     */
    List<ApplicationModel> getItems() {
        return items;
    }

    /**
     * Updates the {@code items}.
     */
    void updateItems(List<ApplicationModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * Adds an {@code item} at {@code position}.
     */
    void add(int position, ApplicationModel item) {
        if (position < 0) position = 0;
        if (position > getItemCount()) position = getItemCount();

        items.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * Removes the {@code item}.
     */
    void remove(ApplicationModel item) {
        int position = items.indexOf(item);

        if (position < 0) return;

        items.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Moves an item from {@code fromPosition} to {@code toPosition}.
     */
    void move(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Returns the {@link ApplicationModel} at {@code position}.
     */
    ApplicationModel get(int position) {
        if (position < 0 || position >= getItemCount()) return null;

        return items.get(position);
    }

    /**
     * Sets the Reorder-Mode.
     */
    void setReorderMode(boolean enable) {
        reorderMode = enable;
        notifyDataSetChanged();
    }

    /**
     * {@link android.support.v7.widget.RecyclerView.ViewHolder} for holding all the {@link View}s.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) ImageView icon;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.handle) ImageView handle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Interface for notifying a drag-start.
     */
    interface StartDragListener {
        /**
         * Gets called when a drag starts.
         */
        void onStartDrag(ViewHolder viewHolder);
    }
}

package de.mchllngr.quickero.module.main;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import de.mchllngr.quickero.R;
import de.mchllngr.quickero.databinding.RecyclerViewItemBinding;
import de.mchllngr.quickero.model.ApplicationModel;
import de.mchllngr.quickero.util.CustomAnimatorListener;

/**
 * {@link Adapter} for handling the shown items.
 */
class MainAdapter extends Adapter<MainAdapter.MainViewHolder> {

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
    MainAdapter(Context context, List<ApplicationModel> items, StartDragListener startDragListener) {
        this.context = context;
        this.items = items;
        this.startDragListener = startDragListener;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.recycler_view_item, parent, false);
        return new MainViewHolder(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        ApplicationModel applicationModel = items.get(position);

        holder.getBinding().icon.setImageDrawable(applicationModel.iconDrawable);
        holder.getBinding().name.setText(applicationModel.name);

        if (reorderMode) {
            Drawable drawable = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_reorder_black_24dp, null);
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.reorder_icon_color));
                holder.getBinding().handle.setImageDrawable(drawable);
            }

            holder.getBinding().handle.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && startDragListener != null)
                    startDragListener.onStartDrag(holder);

                return false;
            });

            if (holder.getBinding().handle.getVisibility() != View.VISIBLE) {
                holder.getBinding().handle.animate()
                        .alpha(1f)
                        .setDuration(100)
                        .setListener(new CustomAnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                holder.getBinding().handle.setAlpha(0f);
                                holder.getBinding().handle.setVisibility(View.VISIBLE);
                            }
                        })
                        .start();
            } else
                holder.getBinding().handle.setAlpha(1f);

        } else {
            if (holder.getBinding().handle.getVisibility() == View.VISIBLE) {
                holder.getBinding().handle.animate()
                        .alpha(0f)
                        .setDuration(100)
                        .setListener(new CustomAnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                holder.getBinding().handle.setAlpha(1f);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.getBinding().handle.setVisibility(View.GONE);
                            }
                        })
                        .start();
            } else
                holder.getBinding().handle.setAlpha(0f);
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
        if (fromPosition < toPosition)
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(items, i, i + 1);
        else
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(items, i, i - 1);

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
     * {@link RecyclerView.ViewHolder} for holding all the {@link View}s.
     */
    class MainViewHolder extends RecyclerView.ViewHolder {
        private RecyclerViewItemBinding binding;

        MainViewHolder(RecyclerViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public RecyclerViewItemBinding getBinding() {
            return binding;
        }
    }

    /**
     * Interface for notifying a drag-start.
     */
    interface StartDragListener {
        /**
         * Gets called when a drag starts.
         */
        void onStartDrag(MainViewHolder viewHolder);
    }
}

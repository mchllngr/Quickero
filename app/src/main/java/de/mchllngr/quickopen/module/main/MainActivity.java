package de.mchllngr.quickopen.module.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BaseActivity;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.module.about.AboutActivity;
import de.mchllngr.quickopen.service.NotificationService;
import de.mchllngr.quickopen.util.CustomNotificationHelper;

/**
 * {@link Activity} for handling the selection of applications.
 */
public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView, MaterialSimpleListAdapter.Callback, MainAdapter.StartDragListener {

    /**
     * {@link CoordinatorLayout} from the layout for showing the {@link Snackbar}.
     */
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    /**
     * {@link Toolbar} for this {@link Activity}.
     */
    @BindView(R.id.toolbar) Toolbar toolbar;
    /**
     * {@link RecyclerView} for showing list of items.
     */
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    /**
     * {@link FloatingActionButton} for adding items.
     */
    @BindView(R.id.fab) FloatingActionButton fab;
    /**
     * Represents the red background behind a swipeable item.
     */
    @BindView(R.id.swipe_background) FrameLayout swipeBackground;

    /**
     * {@link MainAdapter} for updating shown items in {@code recyclerView}.
     */
    private MainAdapter adapter;
    /**
     * {@link MaterialDialog} for showing the installed application-list.
     */
    private MaterialDialog applicationDialog;
    /**
     * {@link MaterialDialog} for showing the loading-process for the list of installed applications.
     */
    private MaterialDialog progressDialog;

    /**
     * {@link Snackbar} for showing the undo-remove-button.
     */
    private Snackbar snackbar;
    /**
     * {@link ItemTouchHelper} for moving and swiping in {@link RecyclerView}.
     */
    private ItemTouchHelper itemTouchHelper;
    /**
     * Indicates whether the Reorder-Mode is enabled or disabled.
     */
    private boolean reorderMode;

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initRecyclerView();

        ApplicationModel.removeNotLaunchableAppsFromList(this);

        startNotificationService();

        fab.setOnClickListener(view -> getPresenter().openApplicationList());
    }

    /**
     * Initialises the {@code recyclerView}.
     */
    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MainAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(
                ContextCompat.getDrawable(this, R.drawable.recycler_view_item_divider)
        ));

        itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.START | ItemTouchHelper.END) {
                    @Override
                    public boolean isLongPressDragEnabled() {
                        return false;
                    }

                    @Override
                    public boolean isItemViewSwipeEnabled() {
                        return !reorderMode;
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        getPresenter().removeItem(viewHolder.getAdapterPosition());
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (!reorderMode) {
                            // set the red background one swiped item
                            swipeBackground.setY(viewHolder.itemView.getTop());
                            if (isCurrentlyActive) {
                                swipeBackground.setVisibility(View.VISIBLE);
                            } else {
                                swipeBackground.setVisibility(View.GONE);
                            }
                        } else
                            swipeBackground.setVisibility(View.GONE);

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().loadItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(!reorderMode); // reorder
        menu.getItem(1).setVisible(!reorderMode); // about
        menu.getItem(2).setVisible(!reorderMode); // settings
        menu.getItem(3).setVisible(reorderMode); // reorder_cancel
        menu.getItem(4).setVisible(reorderMode); // reorder_accept

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reorder:
                if (adapter != null)
                    getPresenter().onReorderIconClick(adapter.getItems());
                return true;
            case R.id.about:
                AboutActivity.start(this);
                return true;
            case R.id.settings:
                goToNotificationSettings(CustomNotificationHelper.CHANNEL_ID);
                return true;
            case R.id.reorder_cancel:
                getPresenter().onReorderCancelIconClick();
                return true;
            case R.id.reorder_accept:
                if (adapter != null)
                    getPresenter().onReorderAcceptIconClick(adapter.getItems());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToNotificationSettings(@Nullable String channel) {
        Intent intent = new Intent();
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            if (channel != null) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
            } else {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
        startActivity(intent);
    }

    /**
     * Starts the {@link NotificationService}.
     */
    private void startNotificationService() {
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
        getPresenter().onApplicationSelected(index);

        if (applicationDialog != null)
            applicationDialog.dismiss();
    }

    @Override
    public void showApplicationListDialog(MaterialSimpleListAdapter adapter) {
        applicationDialog = new MaterialDialog.Builder(this)
                .title(R.string.application_list_dialog_title)
                .adapter(adapter, null)
                .show();
    }

    @Override
    public MaterialSimpleListAdapter.Callback getApplicationChooserCallback() {
        return this;
    }

    @Override
    public void showProgressDialog() {
        hideProgressDialog();

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.progress_dialog_please_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void setReorderMode(boolean enable) {
        reorderMode = enable;
        invalidateOptionsMenu();

        if (adapter != null)
            adapter.setReorderMode(enable);
    }

    @Override
    public void updateItems(List<ApplicationModel> items) {
        if (adapter != null)
            adapter.updateItems(items);
    }

    @Override
    public void addItem(int position, ApplicationModel applicationModel) {
        if (adapter != null)
            adapter.add(position, applicationModel);
    }

    @Override
    public void removeItem(int position) {
        if (adapter != null)
            adapter.remove(adapter.get(position));
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (adapter != null)
            adapter.move(fromPosition, toPosition);
    }

    @Override
    public void showAddItemsButton() {
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAddItemsButton() {
        fab.setVisibility(View.GONE);
    }

    @Override
    public void showUndoButton() {
        hideUndoButton();

        snackbar = Snackbar
                .make(coordinatorLayout, R.string.snackbar_undo_remove, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo_remove_action, view -> {
                    getPresenter().undoRemove();
                    snackbar.dismiss();
                });
        snackbar.getView().setBackgroundResource(R.color.snackbar_background_color);

        snackbar.show();
    }

    @Override
    public void hideUndoButton() {
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onOpenApplicationListError() {
        Log.d("DEBUG_TAG", "MainActivity#onOpenApplicationListError()"); // FIXME delete
        // TODO show error msg
    }

    @Override
    public void showMaxItemsError() {
        Log.d("DEBUG_TAG", "MainActivity#showMaxItemsError()"); // FIXME delete
        // TODO show error msg
    }

    @Override
    public void onStartDrag(MainAdapter.ViewHolder viewHolder) {
        if (itemTouchHelper != null)
            itemTouchHelper.startDrag(viewHolder);
    }
}

package de.mchllngr.quickero.module.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.mchllngr.quickero.R;
import de.mchllngr.quickero.base.BaseActivity;
import de.mchllngr.quickero.databinding.ActivityMainBinding;
import de.mchllngr.quickero.model.ApplicationModel;
import de.mchllngr.quickero.module.about.AboutActivity;
import de.mchllngr.quickero.service.NotificationService;
import de.mchllngr.quickero.util.CustomNotificationHelper;
import de.mchllngr.quickero.util.dialog.ApplicationItem;
import de.mchllngr.quickero.util.dialog.DialogHelper;

/**
 * {@link Activity} for handling the selection of applications.
 */
public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView,
        MainAdapter.StartDragListener, DialogHelper.GoToNotificationSettingsListener {

    private ActivityMainBinding binding;

    /**
     * {@link MainAdapter} for updating shown items in {@code recyclerView}.
     */
    private MainAdapter adapter;

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
    /**
     * Current device screen width in pixels.
     */
    private int deviceScreenWidthPixels;
    /**
     * Helper-Class for showing dialogs.
     */
    private final DialogHelper dialogHelper = new DialogHelper(this, this);

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        getDeviceScreenWidthPixels();

        initRecyclerView();

        ApplicationModel.removeNotLaunchableAppsFromList(this);

        binding.fab.setOnClickListener(view -> getPresenter().openApplicationList());

        // consume move actions to only allow clicking
        binding.enable.setOnTouchListener((v, event) -> event.getActionMasked() == MotionEvent.ACTION_MOVE);
        binding.enable.setOnClickListener(this::onEnableClick);

        getPresenter().checkIfVersionIsSupportedOnCreate();
    }

    private void getDeviceScreenWidthPixels() {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            WindowManager windowManager = (WindowManager) createDisplayContext(getDisplay()).getSystemService(Context.WINDOW_SERVICE);
            deviceScreenWidthPixels = windowManager.getCurrentWindowMetrics().getBounds().width();
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            //noinspection deprecation
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            deviceScreenWidthPixels = metrics.widthPixels;
        }
    }

    /**
     * Initialises the {@code recyclerView}.
     */
    private void initRecyclerView() {
        binding.mainContent.recyclerView.setHasFixedSize(true);
        binding.mainContent.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MainAdapter(this, new ArrayList<>(), this);
        binding.mainContent.recyclerView.setAdapter(adapter);

        binding.mainContent.recyclerView.addItemDecoration(new DividerItemDecoration(
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
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        getPresenter().removeItem(viewHolder.getAdapterPosition());
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (!reorderMode) {
                            binding.mainContent.swipeBackground.setY(viewHolder.itemView.getTop());

                            float halfDeviceScreenWidthPixels = deviceScreenWidthPixels / 2f;
                            float absDX = Math.abs(dX);
                            float calculatedDX;

                            if (absDX <= halfDeviceScreenWidthPixels)
                                calculatedDX = absDX;
                            else
                                calculatedDX = halfDeviceScreenWidthPixels - (absDX - halfDeviceScreenWidthPixels);

                            binding.mainContent.swipeBackground.setAlpha(calculatedDX / halfDeviceScreenWidthPixels);
                        } else {
                            binding.mainContent.swipeBackground.setAlpha(0f);
                        }

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        itemTouchHelper.attachToRecyclerView(binding.mainContent.recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().checkIfNotificationEnabledInPrefs();
        getPresenter().checkIfVersionIsSupported();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().loadItems();
        getPresenter().checkIfNotificationEnabledInAndroidSettings();
    }

    @Override
    protected void onStop() {
        dialogHelper.hideDialog();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(!reorderMode && (adapter == null || adapter.getItems().size() > 1)); // reorder
        menu.getItem(1).setVisible(!reorderMode); // about
        menu.getItem(2).setVisible(!reorderMode); // settings
        menu.getItem(3).setVisible(reorderMode); // reorder_cancel
        menu.getItem(4).setVisible(reorderMode); // reorder_accept

        return super.onPrepareOptionsMenu(menu);
    }

    public void onEnableClick(View v) {
        SwitchMaterial view = (SwitchMaterial) v;
        getPresenter().onEnableClick(view.isChecked());
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
                goToNotificationSettings(CustomNotificationHelper.CHANNEL_DEFAULT_ID);
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

    private void goToNotificationSettings(@Nullable String channelId) {
        Intent intent = new Intent();
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            if (channelId != null) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
            } else {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        }
        startActivity(intent);
    }

    @Override
    public boolean isNotificationEnabled(@Nullable String channelId) {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            if (channelId != null) {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    if (manager.getNotificationChannel(channelId) == null)
                        new CustomNotificationHelper(this).createNotificationChannels(manager);

                    NotificationChannel channel = manager.getNotificationChannel(channelId);
                    return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
                }
            }
            return false;
        } else {
            return NotificationManagerCompat.from(this).areNotificationsEnabled();
        }
    }

    @Override
    public void showVersionNotSupportedDialog() {
        dialogHelper.showVersionNotSupportedDialog();
    }

    @Override
    public void showNotificationDisabledDialog() {
        dialogHelper.showNotificationDisabledDialog();
    }

    /**
     * Starts the {@link NotificationService}.
     */
    private void startNotificationService() {
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void setEnableState(boolean stateEnabled) {
        if (binding != null) binding.enable.setChecked(stateEnabled);
        if (stateEnabled) startNotificationService();
    }

    @Override
    public void showApplicationListDialog(@NonNull List<ApplicationItem> items) {
        dialogHelper.showApplicationListDialog(items);
    }

    @Override
    public void showProgressDialog() {
        dialogHelper.showProgressDialog();
    }

    @Override
    public void hideDialog() {
        dialogHelper.hideDialog();
    }

    @Override
    public void setEmptyListViewVisibility(boolean visible) {
        binding.mainContent.emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
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
        invalidateOptionsMenu();
    }

    @Override
    public void addItem(int position, ApplicationModel applicationModel) {
        if (adapter != null)
            adapter.add(position, applicationModel);
        invalidateOptionsMenu();
    }

    @Override
    public void removeItem(int position) {
        if (adapter != null)
            adapter.remove(adapter.get(position));
        invalidateOptionsMenu();
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (adapter != null)
            adapter.move(fromPosition, toPosition);
    }

    @Override
    public void showAddItemsButton() {
        binding.fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAddItemsButton() {
        binding.fab.setVisibility(View.GONE);
    }

    @Override
    public void showUndoButton() {
        showSnackbar(R.string.snackbar_undo_remove, R.string.snackbar_undo_remove_action, view -> {
            getPresenter().undoRemove();
            snackbar.dismiss();
        });
    }

    @Override
    public void onOpenApplicationListError() {
        showSnackbar(R.string.snackbar_open_application_list_error);
    }

    @Override
    public void onEmptyApplicationListError() {
        showSnackbar(R.string.snackbar_empty_application_list_error);
    }

    @Override
    public void showMaxItemsError() {
        showSnackbar(R.string.snackbar_max_items_error);
    }

    private void showSnackbar(@StringRes int textId) {
        showSnackbar(textId, 0, null);
    }

    private void showSnackbar(@StringRes int textId, @StringRes int actionTextId, @Nullable View.OnClickListener listener) {
        dismissSnackbar();

        snackbar = Snackbar.make(binding.coordinatorLayout, textId, Snackbar.LENGTH_LONG);

        if (actionTextId != 0 && listener != null)
            snackbar.setAction(actionTextId, listener);

        snackbar.getView().setBackgroundResource(R.color.snackbar_background_color);

        snackbar.show();
    }

    @Override
    public void dismissSnackbar() {
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onStartDrag(MainAdapter.MainViewHolder viewHolder) {
        if (itemTouchHelper != null)
            itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onGoToNotificationSettings() {
        goToNotificationSettings(CustomNotificationHelper.CHANNEL_DEFAULT_ID);
    }
}

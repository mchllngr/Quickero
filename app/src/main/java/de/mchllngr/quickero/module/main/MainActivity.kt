package de.mchllngr.quickero.module.main

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Canvas
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import de.mchllngr.quickero.R
import de.mchllngr.quickero.base.BaseActivity
import de.mchllngr.quickero.model.ApplicationModel
import de.mchllngr.quickero.model.ApplicationModel.Companion.removeNotLaunchableAppsFromList
import de.mchllngr.quickero.module.about.AboutActivity.Companion.start
import de.mchllngr.quickero.module.main.MainAdapter.MainViewHolder
import de.mchllngr.quickero.module.main.MainAdapter.StartDragListener
import de.mchllngr.quickero.service.NotificationService
import de.mchllngr.quickero.util.CustomNotificationHelper
import de.mchllngr.quickero.util.dialog.ApplicationItem
import de.mchllngr.quickero.util.dialog.DialogHelper
import de.mchllngr.quickero.util.dialog.DialogHelper.GoToNotificationSettingsListener
import java.util.*
import kotlin.math.abs

/**
 * [Activity] for handling the selection of applications.
 */
class MainActivity : BaseActivity<MainView, MainPresenter>(), MainView, StartDragListener, GoToNotificationSettingsListener {
    /**
     * [CoordinatorLayout] from the layout for showing the [Snackbar].
     */
    @BindView(R.id.coordinator_layout)
    lateinit var coordinatorLayout: CoordinatorLayout

    /**
     * [Toolbar] for this [Activity].
     */
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    /**
     * [RecyclerView] for showing list of items.
     */
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    /**
     * [FloatingActionButton] for adding items.
     */
    @BindView(R.id.fab)
    lateinit var fab: FloatingActionButton

    /**
     * Represents the red background behind a swipeable item.
     */
    @BindView(R.id.swipe_background)
    lateinit var swipeBackground: FrameLayout

    /**
     * Represents the empty view that is shown when the list is empty.
     */
    @BindView(R.id.empty_view)
    lateinit var emptyView: TextView

    /**
     * Represents the view for enabling/disabling the notification.
     */
    @BindView(R.id.enable)
    lateinit var enableNotificationSwitch: SwitchMaterial

    /**
     * [MainAdapter] for updating shown items in `recyclerView`.
     */
    private var adapter: MainAdapter? = null

    /**
     * [Snackbar] for showing the undo-remove-button.
     */
    private var snackbar: Snackbar? = null

    /**
     * [ItemTouchHelper] for moving and swiping in [RecyclerView].
     */
    private var itemTouchHelper: ItemTouchHelper? = null

    /**
     * Indicates whether the Reorder-Mode is enabled or disabled.
     */
    private var reorderMode = false

    /**
     * Current device screen width in pixels.
     */
    private var deviceScreenWidthPixels = 0

    /**
     * Helper-Class for showing dialogs.
     */
    private val dialogHelper = DialogHelper(this, this)
    override fun createPresenter(): MainPresenter {
        return MainPresenter(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        getDeviceScreenWidthPixels()
        initRecyclerView()
        removeNotLaunchableAppsFromList(this)
        fab?.setOnClickListener { presenter.openApplicationList() }

        // consume move actions to only allow clicking
        enableNotificationSwitch?.setOnTouchListener { _, event: MotionEvent -> event.actionMasked == MotionEvent.ACTION_MOVE }
        presenter.checkIfVersionIsSupportedOnCreate()
    }

    private fun getDeviceScreenWidthPixels() {
        deviceScreenWidthPixels = if (VERSION.SDK_INT >= VERSION_CODES.R) {
            display?.let {
                val windowManager = createDisplayContext(it).getSystemService(WINDOW_SERVICE) as WindowManager
                windowManager.currentWindowMetrics.bounds.width()
            } ?: 0
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(metrics)
            metrics.widthPixels
        }
    }

    /**
     * Initialises the `recyclerView`.
     */
    private fun initRecyclerView() {
        recyclerView?.also {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
            val adapter = MainAdapter(this, ArrayList(), this)
            it.adapter = adapter
            it.addItemDecoration(
                DividerItemDecoration(
                    ContextCompat.getDrawable(this, R.drawable.recycler_view_item_divider)
                )
            )
        }
        itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.START or ItemTouchHelper.END
            ) {
                override fun isLongPressDragEnabled(): Boolean {
                    return false
                }

                override fun isItemViewSwipeEnabled(): Boolean {
                    return !reorderMode
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    moveItem(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    swipeDir: Int
                ) {
                    getPresenter().removeItem(viewHolder.adapterPosition)
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
                    if (!reorderMode) {
                        swipeBackground?.apply {
                            y = viewHolder.itemView.top.toFloat()
                            val halfDeviceScreenWidthPixels = deviceScreenWidthPixels / 2f
                            val absDX = abs(dX)
                            val calculatedDX: Float
                            calculatedDX = if (absDX <= halfDeviceScreenWidthPixels) absDX else halfDeviceScreenWidthPixels - (absDX - halfDeviceScreenWidthPixels)
                            alpha = calculatedDX / halfDeviceScreenWidthPixels
                        }
                    } else {
                        swipeBackground?.alpha = 0f
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            })
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        getPresenter().checkIfNotificationEnabledInPrefs()
        getPresenter().checkIfVersionIsSupported()
    }

    override fun onStart() {
        super.onStart()
        getPresenter().loadItems()
        getPresenter().checkIfNotificationEnabledInAndroidSettings()
    }

    override fun onStop() {
        dialogHelper.hideDialog()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @OnClick(R.id.enable)
    fun onEnableClick(view: SwitchMaterial) {
        getPresenter().onEnableClick(view.isChecked)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mainAdapter = adapter
        return when (item.itemId) {
            R.id.reorder -> {
                if (mainAdapter != null) getPresenter().onReorderIconClick(mainAdapter.items)
                true
            }
            R.id.about -> {
                start(this)
                true
            }
            R.id.settings -> {
                goToNotificationSettings(CustomNotificationHelper.CHANNEL_DEFAULT_ID)
                true
            }
            R.id.reorder_cancel -> {
                getPresenter().onReorderCancelIconClick()
                true
            }
            R.id.reorder_accept -> {
                if (mainAdapter != null) getPresenter().onReorderAcceptIconClick(mainAdapter.items)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToNotificationSettings(channelId: String?) {
        val intent = Intent()
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            if (channelId != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
        }
        startActivity(intent)
    }

    override fun isNotificationEnabled(channelId: String?): Boolean {
        return if (VERSION.SDK_INT >= VERSION_CODES.O) {
            if (channelId != null) {
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (manager != null) {
                    if (manager.getNotificationChannel(channelId) == null) CustomNotificationHelper(this).createNotificationChannels(manager)
                    val channel = manager.getNotificationChannel(channelId)
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
            }
            false
        } else {
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    override fun showVersionNotSupportedDialog() {
        dialogHelper.showVersionNotSupportedDialog()
    }

    override fun showNotificationDisabledDialog() {
        dialogHelper.showNotificationDisabledDialog()
    }

    /**
     * Starts the [NotificationService].
     */
    private fun startNotificationService() {
        startService(Intent(this, NotificationService::class.java))
    }

    override fun setEnableState(stateEnabled: Boolean) {
        if (enableNotificationSwitch != null) enableNotificationSwitch?.isChecked = stateEnabled
        if (stateEnabled) startNotificationService()
    }

    override fun showApplicationListDialog(items: List<ApplicationItem>) {
        dialogHelper.showApplicationListDialog(items)
    }

    override fun showProgressDialog() {
        dialogHelper.showProgressDialog()
    }

    override fun hideDialog() {
        dialogHelper.hideDialog()
    }

    override fun setEmptyListViewVisibility(visible: Boolean) {
        emptyView?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setReorderMode(enable: Boolean) {
        reorderMode = enable
        invalidateOptionsMenu()
        adapter?.setReorderMode(enable)
    }

    override fun updateItems(items: MutableList<ApplicationModel>) {
        adapter?.updateItems(items)
        invalidateOptionsMenu()
    }

    override fun addItem(
        position: Int,
        applicationModel: ApplicationModel
    ) {
        adapter?.add(position, applicationModel)
        invalidateOptionsMenu()
    }

    override fun removeItem(position: Int) {
        adapter?.apply {
            val appModel = this[position]
            if (appModel != null) remove(appModel)
        }
        invalidateOptionsMenu()
    }

    override fun moveItem(
        fromPosition: Int,
        toPosition: Int
    ) {
        adapter?.move(fromPosition, toPosition)
    }

    override fun showAddItemsButton() {
        fab?.visibility = View.VISIBLE
    }

    override fun hideAddItemsButton() {
        fab?.visibility = View.GONE
    }

    override fun showUndoButton() {
        showSnackbar(R.string.snackbar_undo_remove, R.string.snackbar_undo_remove_action) { view: View? ->
            getPresenter().undoRemove()
            snackbar?.dismiss()
        }
    }

    override fun onOpenApplicationListError() {
        showSnackbar(R.string.snackbar_open_application_list_error)
    }

    override fun onEmptyApplicationListError() {
        showSnackbar(R.string.snackbar_empty_application_list_error)
    }

    override fun showMaxItemsError() {
        showSnackbar(R.string.snackbar_max_items_error)
    }

    private fun showSnackbar(
        @StringRes textId: Int,
        @StringRes actionTextId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        dismissSnackbar()
        coordinatorLayout?.apply { snackbar = Snackbar.make(this, textId, Snackbar.LENGTH_LONG) }
        if (actionTextId != 0 && listener != null) snackbar?.setAction(actionTextId, listener)
        snackbar?.view?.setBackgroundResource(R.color.snackbar_background_color)
        snackbar?.show()
    }

    override fun dismissSnackbar() {
        snackbar?.dismiss()
    }

    override fun onStartDrag(viewHolder: MainViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    override fun onGoToNotificationSettings() {
        goToNotificationSettings(CustomNotificationHelper.CHANNEL_DEFAULT_ID)
    }
}

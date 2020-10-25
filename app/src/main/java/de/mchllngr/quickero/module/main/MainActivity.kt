package de.mchllngr.quickero.module.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.customListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.mchllngr.quickero.R
import de.mchllngr.quickero.databinding.ActivityMainBinding
import de.mchllngr.quickero.extension.deviceScreenWidth
import de.mchllngr.quickero.module.about.AboutActivity
import de.mchllngr.quickero.repository.application.PackageName
import de.mchllngr.quickero.util.applicationlist.ApplicationListAdapter
import de.mchllngr.quickero.util.notification.NotificationHelper
import de.mchllngr.quickero.util.swipe.SwipeItemTouchCallback
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var adapter: MainAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.initRecyclerView()

        binding.enable.setOnCheckedChangeListener { _, checked -> viewModel.setNotificationEnabled(checked) }
        binding.fab.setOnClickListener { binding.openApplicationSelection() }

        viewModel.applications.asLiveData().observe(this) { adapter.submitList(it) }
        viewModel.applicationsMaxReached.asLiveData().observe(this) { binding.fab.visibility = if (it) View.VISIBLE else View.GONE }
        viewModel.notificationEnabled.asLiveData().observe(this) { binding.enable.isChecked = it }
    }

    private fun ActivityMainBinding.initRecyclerView() {
        content.recyclerView.let {
            it.setHasFixedSize(true)
            it.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
            it.adapter = adapter
            ItemTouchHelper(
                SwipeItemTouchCallback(
                    content.swipeBackground,
                    deviceScreenWidth,
                    object : SwipeItemTouchCallback.Delegate {

                        override fun move(
                            fromPosition: Int,
                            toPosition: Int
                        ) {
                            viewModel.moveApplication(fromPosition, toPosition)
                        }

                        override fun remove(position: Int) {
                            lifecycleScope.launch {
                                viewModel.removeApplication(position)?.let { packageName -> showRemovedSnackbar(packageName, position) }
                            }
                        }
                    }
                )
            ).attachToRecyclerView(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.about -> {
            AboutActivity.start(this)
            true
        }
        R.id.settings -> {
            notificationHelper.openSettings(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        checkNotificationEnabled()
    }

    private fun checkNotificationEnabled() {
        if (!notificationHelper.isNotificationEnabled()) {
            MaterialDialog(this)
                .title(R.string.dialog_notification_disabled_title, null)
                .message(null, getString(R.string.dialog_notification_disabled_text, getString(R.string.app_name)), null)
                .cancelable(false)
                .cancelOnTouchOutside(false)
                .positiveButton(R.string.dialog_notification_disabled_button_ok, null, null)
                .negativeButton(R.string.dialog_notification_disabled_button_settings, null) { notificationHelper.openSettings(this) }
                .show()
        }
    }

    private fun ActivityMainBinding.openApplicationSelection() {
        val loadingDialog = MaterialDialog(this@MainActivity)
            .message(R.string.dialog_progress_please_wait, null, null)
            .cancelable(false)
            .cancelOnTouchOutside(false)
            .apply { show() }

        lifecycleScope.launch {
            viewModel.installedApplications.collect { list ->
                loadingDialog.dismiss()

                if (list.isEmpty()) {
                    Snackbar.make(coordinatorLayout, R.string.snackbar_empty_application_list_error, Snackbar.LENGTH_SHORT).show()
                    return@collect
                }

                MaterialDialog(this@MainActivity, BottomSheet())
                    .title(R.string.dialog_application_list_title, null)
                    .apply {
                        customListAdapter(
                            ApplicationListAdapter(list) {
                                viewModel.addApplication(it)
                                dismiss()
                            }
                        )
                    }
                    .show()
            }
        }
    }

    private fun ActivityMainBinding.showRemovedSnackbar(
        packageName: PackageName,
        position: Int
    ) {
        Snackbar.make(coordinatorLayout, R.string.snackbar_undo_remove, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_undo_remove_action) { viewModel.addApplication(packageName, position) }
            .show()
    }
}

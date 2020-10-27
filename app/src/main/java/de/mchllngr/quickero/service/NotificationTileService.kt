package de.mchllngr.quickero.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.Service
import android.graphics.Color
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dagger.hilt.android.AndroidEntryPoint
import de.mchllngr.quickero.repository.notification.NotificationRepository
import de.mchllngr.quickero.util.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/** [Service] for handling the [Tile] for enabling and disabling the [Notification] in Android Nougat and above. */
@TargetApi(Build.VERSION_CODES.N)
@AndroidEntryPoint
class NotificationTileService : TileService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var listeningJob: Job? = null

    @Inject lateinit var notificationRepository: NotificationRepository
    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()

        listeningJob?.cancel()

        val job = SupervisorJob()
        listeningJob = job

        serviceScope.launch(job) {
            notificationRepository.enabled.collect {
                val state = if (it) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                updateTileState(state)
            }
        }
    }

    override fun onStopListening() {
        listeningJob?.cancel()
        listeningJob = null
        super.onStopListening()
    }

    private fun updateTileState(state: Int) {
        val tile = qsTile ?: return

        tile.state = state
        tile.icon?.apply {
            val tint = when (state) {
                Tile.STATE_ACTIVE -> Color.WHITE
                else -> Color.GRAY
            }
            setTint(tint)
        }

        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        val job = listeningJob ?: return
        serviceScope.launch(job) {
            val currentState = qsTile.state == Tile.STATE_ACTIVE
            val nextState = !currentState
            notificationRepository.setEnabled(nextState)
            if (nextState) notificationHelper.startNotificationService()
        }
    }
}

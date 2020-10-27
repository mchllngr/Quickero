package de.mchllngr.quickero.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import de.mchllngr.quickero.repository.application.ApplicationsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/** [BroadcastReceiver] for removing apps from the list when uninstalled.*/
@AndroidEntryPoint
class PackageFullyRemovedReceiver : BroadcastReceiver() {

    @Inject lateinit var applicationsRepository: ApplicationsRepository

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        if (context == null || intent == null) return

        val action = intent.action
        if (Intent.ACTION_PACKAGE_FULLY_REMOVED == action) {
            Timber.d("Action '$action' received")
            GlobalScope.launch {
                applicationsRepository.removeInvalidApplications()
            }
        } else {
            Timber.w("Unknown action '$action' received")
        }
    }
}

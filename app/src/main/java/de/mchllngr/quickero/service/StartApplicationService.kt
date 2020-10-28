package de.mchllngr.quickero.service

import android.app.IntentService
import android.content.Intent

/** [IntentService] for starting the clicked application from the notification. */
class StartApplicationService : IntentService(StartApplicationService::class.java.name) {

    override fun onHandleIntent(intent: Intent?) {
        // get packageName and start selected application
        val extras = intent?.extras
        if (extras != null && extras.containsKey(KEY_PACKAGE_NAME)) {
            val packageName = extras.getString(KEY_PACKAGE_NAME)
            startApplication(packageName)
        }

        // close navigation bar
        val i = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        sendBroadcast(i)
    }

    private fun startApplication(packageName: String?) {
        if (packageName.isNullOrBlank()) return
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
    }

    companion object {

        const val KEY_PACKAGE_NAME = "package_name"
    }
}

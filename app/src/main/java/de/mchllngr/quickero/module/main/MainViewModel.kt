package de.mchllngr.quickero.module.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.mchllngr.quickero.repository.application.ApplicationsRepository
import de.mchllngr.quickero.repository.application.PackageName
import de.mchllngr.quickero.repository.notification.NotificationRepository
import de.mchllngr.quickero.util.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel @ViewModelInject constructor(
    private val applicationsRepository: ApplicationsRepository,
    private val notificationRepository: NotificationRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    val applications = applicationsRepository.applications
        .map { list ->
            if (list.isNotEmpty()) list.map { it.toItem() }
            else listOf(MainItem.Empty)
        }
        .flowOn(Dispatchers.Default)

    val applicationsMaxReached = applicationsRepository.applicationsMaxReached
        .flowOn(Dispatchers.Default)

    val notificationEnabled = notificationRepository.enabled
        .flowOn(Dispatchers.Default)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                applicationsRepository.removeInvalidApplications()

                notificationRepository.enabled
                    .combine(applicationsRepository.applications) { enabled, applications -> enabled to applications }
                    .map { (enabled, applications) -> enabled && applications.isNotEmpty() }
                    .collect { startService -> if (startService) notificationHelper.startNotificationService() }
            }
        }
    }

    fun getInstalledApplications() = applicationsRepository.getInstalledApplications()
        .flowOn(Dispatchers.Default)

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                notificationRepository.setEnabled(enabled)
            }
        }
    }

    fun addApplication(
        packageName: PackageName,
        position: Int = Int.MAX_VALUE
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                applicationsRepository.addApplication(packageName, position)
            }
        }
    }

    fun moveApplication(
        fromPosition: Int,
        toPosition: Int
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                applicationsRepository.moveApplication(fromPosition, toPosition)
            }
        }
    }

    suspend fun removeApplication(position: Int): PackageName? = withContext(Dispatchers.Default) {
        applicationsRepository.removeApplication(position)
    }
}

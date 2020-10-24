package de.mchllngr.quickero.model

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.google.gson.Gson
import de.mchllngr.quickero.R
import de.mchllngr.quickero.util.DrawableToBitmapConverter
import de.mchllngr.quickero.util.GsonPreferenceAdapter
import timber.log.Timber
import java.util.*

/**
 * Model for needed information about an application.
 */
class ApplicationModel private constructor(
    val packageName: String,
    val iconDrawable: Drawable,
    val name: String
) {

    val iconBitmap: Bitmap? = DrawableToBitmapConverter.convertDrawableToBitmap(iconDrawable)

    companion object {
        /**
         * Factory-method to create an [ApplicationModel] from a package-name.
         *
         * @param context [Context]
         * @param packageName package-name
         * @return [ApplicationModel] if an application with the `packageName` is found, otherwise null.
         */
        @JvmStatic
        fun getApplicationModelForPackageName(
            context: Context,
            packageName: String
        ): ApplicationModel? {
            return try {
                // will throw NameNotFoundException if no application with packageName is found
                val packageManager = context.packageManager
                val info = packageManager.getApplicationInfo(packageName, 0)
                val name = packageManager.getApplicationLabel(info) as String
                val icon = getApplicationIconForPackageName(context, packageName)
                if (icon != null) ApplicationModel(packageName, icon, name) else null
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }

        /**
         * Gets the iconBitmap for an Application.
         *
         * @param context [Context]
         * @param packageName package-name
         * @return [Drawable] if an application with the `packageName` is found, otherwise null.
         */
        private fun getApplicationIconForPackageName(
            context: Context,
            packageName: String
        ): Drawable? {
            return try {
                context.packageManager.getApplicationIcon(packageName)
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Generates a [List] of [ApplicationModel]s from a [List] of packageNames.
         *
         *
         * If the given [List] is null or empty it returns an empty list.
         *
         * @param context [Context]
         * @param list [List] of packageNames
         * @return list of [ApplicationModel]s
         */
        @JvmStatic
        fun prepareApplicationModelsList(
            context: Context,
            list: List<*>?
        ): List<ApplicationModel> {
            if (list == null || list.isEmpty()) return ArrayList()
            val applicationModels: MutableList<ApplicationModel> = ArrayList()
            for (o in list) if (o is String) {
                val applicationModel = getApplicationModelForPackageName(context, o)
                if (applicationModel != null) applicationModels.add(applicationModel)
            }
            return applicationModels
        }

        /**
         * Generates an array of [ApplicationModel]s from a [List] of packageNames.
         *
         *
         * If the given [List] is null or empty it returns an empty list.
         *
         * @param context [Context]
         * @param list [List] of packageNames
         * @return array of [ApplicationModel]s
         */
        @JvmStatic
        fun prepareApplicationModelsArray(
            context: Context,
            list: List<*>?
        ): Array<ApplicationModel?> {
            if (list == null || list.isEmpty()) return arrayOfNulls(0)
            val applicationModels = prepareApplicationModelsList(context, list)
            return applicationModels.toTypedArray()
        }

        /**
         * Checks if an application is launchable by checking if there is a launch intent available.
         */
        @JvmStatic
        fun isLaunchable(
            context: Context,
            packageName: String
        ) = !TextUtils.isEmpty(packageName) && context.packageManager.getLaunchIntentForPackage(packageName) != null

        /**
         * Removes any application from the list that is not launchable.
         *
         * @param context context for getting the [android.content.SharedPreferences]
         */
        @JvmStatic
        fun removeNotLaunchableAppsFromList(context: Context) {
            val rxSharedPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(context))
            val adapter = GsonPreferenceAdapter(Gson(), MutableList::class.java)
            val packageNamesPref = rxSharedPreferences.getObject(
                context.getString(R.string.pref_package_names),
                null,
                adapter
            )
            val packageNameList = packageNamesPref.get()
            if (packageNameList == null || packageNameList.isEmpty()) return
            val iterator = packageNameList.iterator()
            while (iterator.hasNext()) {
                val packageName = iterator.next()
                if (packageName is String && !isLaunchable(context, packageName)) {
                    iterator.remove()
                    Timber.d("Removed app '$packageName' from list, because it can not be launched (maybe it was uninstalled)")
                }
            }
            packageNamesPref.set(packageNameList)
        }
    }
}

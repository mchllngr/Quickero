package de.mchllngr.quickero.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import de.mchllngr.quickero.R;
import de.mchllngr.quickero.extension.BitmapExtKt;
import de.mchllngr.quickero.util.GsonPreferenceAdapter;
import timber.log.Timber;

/**
 * Model for needed information about an application.
 */
public class ApplicationModel {

    public final String packageName;
    public final Drawable iconDrawable;
    public final Bitmap iconBitmap;
    public final String name;

    /**
     * Private constructor. ApplicationModel should be instantiated through
     * {@link ApplicationModel#getApplicationModelForPackageName(Context, String)}.
     *
     * @param packageName package-name
     * @param icon application icon
     * @param name application name
     */
    private ApplicationModel(String packageName, Drawable icon, String name) {
        this.packageName = packageName;
        this.iconDrawable = icon;
        this.iconBitmap = BitmapExtKt.toBitmap(icon);
        this.name = name;
    }

    /**
     * Factory-method to create an {@link ApplicationModel} from a package-name.
     *
     * @param context {@link Context}
     * @param packageName package-name
     * @return {@link ApplicationModel} if an application with the {@code packageName} is found, otherwise null.
     */
    @Nullable
    public static ApplicationModel getApplicationModelForPackageName(Context context,
                                                                     String packageName) {
        try {
            // will throw NameNotFoundException if no application with packageName is found
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            String name = (String) packageManager.getApplicationLabel(info);

            return new ApplicationModel(
                    packageName,
                    getApplicationIconForPackageName(context, packageName),
                    name
            );
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets the iconBitmap for an Application.
     *
     * @param context {@link Context}
     * @param packageName package-name
     * @return {@link Drawable} if an application with the {@code packageName} is found, otherwise null.
     */
    private static Drawable getApplicationIconForPackageName(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Generates a {@link List} of {@link ApplicationModel}s from a {@link List} of packageNames.
     * <p>
     * If the given {@link List} is null or empty it returns an empty list.
     *
     * @param context {@link Context}
     * @param list {@link List} of packageNames
     * @return list of {@link ApplicationModel}s
     */
    @NonNull
    public static List<ApplicationModel> prepareApplicationModelsList(Context context, List list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();

        List<ApplicationModel> applicationModels = new ArrayList<>();

        for (Object o : list)
            if (o instanceof String) {
                ApplicationModel applicationModel = ApplicationModel.getApplicationModelForPackageName(
                        context,
                        (String) o
                );

                if (applicationModel != null)
                    applicationModels.add(applicationModel);
            }

        return applicationModels;
    }

    /**
     * Generates an array of {@link ApplicationModel}s from a {@link List} of packageNames.
     * <p>
     * If the given {@link List} is null or empty it returns an empty list.
     *
     * @param context {@link Context}
     * @param list {@link List} of packageNames
     * @return array of {@link ApplicationModel}s
     */
    @NonNull
    public static ApplicationModel[] prepareApplicationModelsArray(Context context, List list) {
        if (list == null || list.isEmpty()) return new ApplicationModel[0];

        List<ApplicationModel> applicationModels = prepareApplicationModelsList(context, list);

        return applicationModels.toArray(new ApplicationModel[0]);
    }

    /**
     * Checks if an application is launchable by checking if there is a launch intent available.
     */
    public static boolean isLaunchable(@NonNull Context context, String packageName) {
        return !TextUtils.isEmpty(packageName) && context.getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    /**
     * Removes any application from the list that is not launchable.
     *
     * @param context context for getting the {@link android.content.SharedPreferences}
     */
    public static void removeNotLaunchableAppsFromList(@NonNull Context context) {
        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(context));
        GsonPreferenceAdapter<List> adapter = new GsonPreferenceAdapter<>(new Gson(), List.class);
        Preference<List> packageNamesPref = rxSharedPreferences.getObject(
                context.getString(R.string.pref_package_names),
                null,
                adapter
        );

        List packageNameList = packageNamesPref.get();
        if (packageNameList == null || packageNameList.isEmpty()) return;

        Iterator iterator = packageNameList.iterator();
        while (iterator.hasNext()) {
            Object packageName = iterator.next();
            if (packageName instanceof String && !isLaunchable(context, (String) packageName)) {
                iterator.remove();
                Timber.d("Removed app '" + packageName + "' from list, because it can not be launched (maybe it was uninstalled)");
            }
        }

        packageNamesPref.set(packageNameList);
    }
}

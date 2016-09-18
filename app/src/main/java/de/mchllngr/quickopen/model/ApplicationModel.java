package de.mchllngr.quickopen.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.mchllngr.quickopen.util.CustomNotificationHelper;
import de.mchllngr.quickopen.util.DrawableToBitmapConverter;

/**
 * Model for needed informations about an application.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class ApplicationModel {

    public String packageName;
    public Drawable iconDrawable;
    public Bitmap iconBitmap;
    public String name;

    /**
     * Private constructor. ApplicationModel should be instantiated through
     * {@link ApplicationModel#getApplicationModelForPackageName(Context, String)}.
     *
     * @param packageName package-name
     * @param icon        application icon
     * @param name        application name
     */
    private ApplicationModel(String packageName, Drawable icon, String name) {
        this.packageName = packageName;
        this.iconDrawable = icon;
        this.iconBitmap = DrawableToBitmapConverter.convertDrawableToBitmap(icon);
        this.name = name;
    }

    /**
     * Factory-method to create an {@link ApplicationModel} from a package-name.
     *
     * @param context     {@link Context}
     * @param packageName package-name
     * @return {@link ApplicationModel} if an application with the {@code packageName} is found,
     * otherwise null.
     */
    @Nullable
    public static ApplicationModel getApplicationModelForPackageName(Context context,
                                                                     String packageName) {
        try {
            // will throw NameNotFoundException if no application with packageName is found
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            String name = (String) context.getPackageManager().getApplicationLabel(info);

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
     * @param context     {@link Context}
     * @param packageName package-name
     * @return {@link Drawable} if an application with the {@code packageName} is found,
     * otherwise null.
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
                ApplicationModel applicationModel = ApplicationModel.
                        getApplicationModelForPackageName(
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

        return applicationModels.toArray(new ApplicationModel[applicationModels.size()]);
    }
}

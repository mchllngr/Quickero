package de.mchllngr.quickopen.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import de.mchllngr.quickopen.util.DrawableToBitmapConverter;

/**
 * Model for needed informations about an application.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class ApplicationModel {

    public String packageName;
    public Bitmap icon;
    public String name;

    /**
     * Private constructor. ApplicationModel should be instantiated through
     * {@link ApplicationModel#getApplicationModelForPackageName(Context, String)}.
     *
     * @param packageName package-name
     * @param icon        application icon
     * @param name        application name
     */
    private ApplicationModel(String packageName, Bitmap icon, String name) {
        this.packageName = packageName;
        this.icon = icon;
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

            return new ApplicationModel(packageName,
                    getApplicationIconForPackageName(context, packageName),
                    name);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets the icon for an Application.
     *
     * @param context     {@link Context}
     * @param packageName package-name
     * @return {@link Bitmap} if an application with the {@code packageName} is found,
     * otherwise null.
     */
    private static Bitmap getApplicationIconForPackageName(Context context, String packageName) {
        try {
            Drawable applicationIconDrawable = context.getPackageManager().
                    getApplicationIcon(packageName);
            return DrawableToBitmapConverter.convertDrawableToBitmap(applicationIconDrawable);
        } catch (Exception e) {
            return null;
        }
    }
}

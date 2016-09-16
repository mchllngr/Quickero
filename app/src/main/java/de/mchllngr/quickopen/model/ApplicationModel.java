package de.mchllngr.quickopen.model;

import android.content.Context;
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

    /**
     * Private constructor. ApplicationModel should be instantiated through
     * {@link ApplicationModel#getApplicationModelForPackageName(Context, String)}.
     *
     * @param packageName package-name
     * @param icon        application icon
     */
    private ApplicationModel(String packageName, Bitmap icon) {
        this.packageName = packageName;
        this.icon = icon;
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
            context.getPackageManager().getApplicationInfo(packageName, 0);

            return new ApplicationModel(packageName,
                    getApplicationIconForPackageName(context, packageName));
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

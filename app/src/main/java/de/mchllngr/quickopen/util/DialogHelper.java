package de.mchllngr.quickopen.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.mchllngr.quickopen.R;
import timber.log.Timber;

/**
 * Helper-Class for showing dialogs.
 */
public class DialogHelper {


    private enum DialogType {VERSION_NOT_SUPPORTED, PROGRESS, APPLICATION_LIST, NOTIFICATION_DISABLED}

    /**
     * Link to the Play Store listing.
     */
    private static final String URL_PLAY_STORE_LISTING = "https://play.google.com/store/apps/details?id=de.mchllngr.quickopen";

    private final Activity activity;
    private final GoToNotificationSettingsListener listener;

    /**
     * {@link MaterialDialog} for showing various dialogs.
     */
    private MaterialDialog dialog;
    /**
     * Indicates if the version-not-supported-dialog is currently shown.
     */
    private boolean versionNotSupportedDialogIsShown = false;

    public DialogHelper(@NonNull Activity activity, @Nullable GoToNotificationSettingsListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void showVersionNotSupportedDialog() {
        showDialog(DialogType.VERSION_NOT_SUPPORTED);
    }

    public void showNotificationDisabledDialog() {
        showDialog(DialogType.NOTIFICATION_DISABLED);
    }

    public void showApplicationListDialog(@NonNull MaterialSimpleListAdapter adapter) {
        showDialog(DialogType.APPLICATION_LIST, adapter);
    }

    public void showProgressDialog() {
        showDialog(DialogType.PROGRESS);
    }

    private void showDialog(@NonNull DialogType type) {
        showDialog(type, null);
    }

    private void showDialog(@NonNull DialogType type, @Nullable MaterialSimpleListAdapter adapter) {
        // do not hide the version-not-supported-dialog
        if (versionNotSupportedDialogIsShown) return;

        hideDialog();

        MaterialDialog.Builder dialogBuilder = null;
        switch (type) {
            case VERSION_NOT_SUPPORTED:
                dialogBuilder = new MaterialDialog.Builder(activity)
                        .title(activity.getString(R.string.dialog_version_not_supported_title, activity.getString(R.string.app_name)))
                        .content(activity.getString(R.string.dialog_version_not_supported_text, activity.getString(R.string.app_name)))
                        .cancelable(false)
                        .positiveText(R.string.dialog_version_not_supported_button_update)
                        .onPositive((d, w) -> {
                            openPlayStoreListing();
                            activity.finish();
                        })
                        .negativeText(R.string.dialog_version_not_supported_button_close)
                        .onNegative((d, w) -> activity.finish());
                versionNotSupportedDialogIsShown = true;
                break;
            case PROGRESS:
                dialogBuilder = new MaterialDialog.Builder(activity)
                        .content(R.string.dialog_progress_please_wait)
                        .cancelable(false)
                        .progress(true, 0);
                break;
            case APPLICATION_LIST:
                if (adapter != null)
                    dialogBuilder = new MaterialDialog.Builder(activity)
                            .title(R.string.dialog_application_list_title)
                            .adapter(adapter, null);
                else
                    Timber.w("Could not show application list: adapter is null");
                break;
            case NOTIFICATION_DISABLED:
                dialogBuilder = new MaterialDialog.Builder(activity)
                        .title(R.string.dialog_notification_disabled_title)
                        .content(activity.getString(R.string.dialog_notification_disabled_text, activity.getString(R.string.app_name)))
                        .cancelable(false)
                        .positiveText(R.string.dialog_notification_disabled_button_ok)
                        .negativeText(R.string.dialog_notification_disabled_button_settings)
                        .onNegative((d, w) -> {
                            if (listener != null) listener.onGoToNotificationSettings();
                        });
                break;
            default:
                break;
        }

        if (dialogBuilder != null) {
            dialog = dialogBuilder.show();
        }
    }

    public void hideDialog() {
        // do not hide the version-not-supported-dialog
        if (versionNotSupportedDialogIsShown) return;

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    private void openPlayStoreListing() {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PLAY_STORE_LISTING)));
    }

    public interface GoToNotificationSettingsListener {
        void onGoToNotificationSettings();
    }
}

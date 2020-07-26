package de.mchllngr.quickopen.util.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.list.DialogListExtKt;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import de.mchllngr.quickopen.R;
import kotlin.Unit;
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
    private final GoToNotificationSettingsListener goToNotificationSettingsListener;

    /**
     * {@link MaterialDialog} for showing various dialogs.
     */
    private MaterialDialog dialog;
    /**
     * Indicates if the version-not-supported-dialog is currently shown.
     */
    private boolean versionNotSupportedDialogIsShown = false;

    public DialogHelper(@NonNull Activity activity, @Nullable GoToNotificationSettingsListener goToNotificationSettingsListener) {
        this.activity = activity;
        this.goToNotificationSettingsListener = goToNotificationSettingsListener;
    }

    public void showVersionNotSupportedDialog() {
        showDialog(DialogType.VERSION_NOT_SUPPORTED);
    }

    public void showNotificationDisabledDialog() {
        showDialog(DialogType.NOTIFICATION_DISABLED);
    }

    public void showApplicationListDialog(@NonNull List<ApplicationItem> items) {
        showDialog(DialogType.APPLICATION_LIST, items);
    }

    public void showProgressDialog() {
        showDialog(DialogType.PROGRESS);
    }

    private void showDialog(@NonNull DialogType type) {
        showDialog(type, null);
    }

    private void showDialog(@NonNull DialogType type, @Nullable List<ApplicationItem> items) {
        // do not hide the version-not-supported-dialog
        if (versionNotSupportedDialogIsShown) return;

        hideDialog();

        MaterialDialog localDialog = null;
        switch (type) {
            case VERSION_NOT_SUPPORTED:
                localDialog = new MaterialDialog(activity, MaterialDialog.getDEFAULT_BEHAVIOR())
                        .title(null, activity.getString(R.string.dialog_version_not_supported_title, activity.getString(R.string.app_name)))
                        .message(null, activity.getString(R.string.dialog_version_not_supported_text, activity.getString(R.string.app_name)), null)
                        .cancelable(false)
                        .cancelOnTouchOutside(false)
                        .positiveButton(R.string.dialog_version_not_supported_button_update, null, materialDialog -> {
                            openPlayStoreListing();
                            activity.finish();
                            return Unit.INSTANCE;
                        })
                        .negativeButton(R.string.dialog_version_not_supported_button_close, null, materialDialog -> {
                            activity.finish();
                            return Unit.INSTANCE;
                        });
                versionNotSupportedDialogIsShown = true;
                break;
            case PROGRESS:
                localDialog = new MaterialDialog(activity, MaterialDialog.getDEFAULT_BEHAVIOR())
                        .message(R.string.dialog_progress_please_wait, null, null)
                        .cancelable(false)
                        .cancelOnTouchOutside(false);
                break;
            case APPLICATION_LIST:
                if (items != null) {
                    localDialog = new MaterialDialog(activity, new BottomSheet())
                            .title(R.string.dialog_application_list_title, null);
                    localDialog = DialogListExtKt.customListAdapter(
                            localDialog,
                            new ApplicationListAdapter(items),
                            new LinearLayoutManager(activity)
                    );
                } else
                    Timber.w("Could not show application list: adapter is null");
                break;
            case NOTIFICATION_DISABLED:
                localDialog = new MaterialDialog(activity, MaterialDialog.getDEFAULT_BEHAVIOR())
                        .title(R.string.dialog_notification_disabled_title, null)
                        .message(null, activity.getString(R.string.dialog_notification_disabled_text, activity.getString(R.string.app_name)), null)
                        .cancelable(false)
                        .cancelOnTouchOutside(false)
                        .positiveButton(R.string.dialog_notification_disabled_button_ok, null, null)
                        .negativeButton(R.string.dialog_notification_disabled_button_settings, null, materialDialog -> {
                            if (goToNotificationSettingsListener != null) goToNotificationSettingsListener.onGoToNotificationSettings();
                            return Unit.INSTANCE;
                        });
                break;
            default:
                break;
        }

        if (localDialog != null) {
            this.dialog = localDialog;
            localDialog.show();
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

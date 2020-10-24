package de.mchllngr.quickero.module.main;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.mchllngr.quickero.base.BaseView;
import de.mchllngr.quickero.model.ApplicationModel;
import de.mchllngr.quickero.util.dialog.ApplicationItem;

/**
 * Interface for the {@link MainActivity}
 */
interface MainView extends BaseView {

    /**
     * Will check if the notification for the given {@code channelId} is enabled.
     */
    boolean isNotificationEnabled(@Nullable String channelId);

    /**
     * Will show a dialog to the user, because the current version is not supported anymore.
     */
    void showVersionNotSupportedDialog();

    /**
     * Will show a dialog to the user, because the notification is disabled.
     */
    void showNotificationDisabledDialog();

    /**
     * Will update the state of the view that represents if the notification is enabled/disabled.
     */
    void setEnableState(boolean stateEnabled);

    /**
     * Shows the list dialog for choosing one of the installed applications.
     *
     * @param items the items to show
     */
    void showApplicationListDialog(@NonNull List<ApplicationItem> items);

    /**
     * Shows an indeterminate progress dialog while loading.
     */
    void showProgressDialog();

    /**
     * Hides the shown progress dialog when loading is finished.
     */
    void hideDialog();

    /**
     * Will set the visibility for the View shown when the list is empty.
     */
    void setEmptyListViewVisibility(boolean visible);

    /**
     * Enables/disables the Reorder-Mode.
     */
    void setReorderMode(boolean enable);

    /**
     * Updates the shown items.
     */
    void updateItems(List<ApplicationModel> items);

    /**
     * Adds an item at {@code positon} to the shown list.
     */
    void addItem(int position, ApplicationModel applicationModel);

    /**
     * Removes the item at {@code position} from the shown list.
     */
    void removeItem(int position);

    /**
     * Moves an item in the shown list from {@code fromPosition} to {@code toPosition}.
     */
    void moveItem(int fromPosition, int toPosition);

    /**
     * Shows the button to add items.
     */
    void showAddItemsButton();

    /**
     * Hides the button to add items.
     */
    void hideAddItemsButton();

    /**
     * Shows the button to undo the last remove;
     */
    void showUndoButton();

    /**
     * Hides the button to undo the last remove;
     */
    void dismissSnackbar();

    /**
     * Shows an error message.
     */
    void onOpenApplicationListError();

    /**
     * Shows an error message when no addable applications were found.
     */
    void onEmptyApplicationListError();

    /**
     * Shows an error message when trying to add an item, but maxCount is already reached.
     */
    void showMaxItemsError();
}

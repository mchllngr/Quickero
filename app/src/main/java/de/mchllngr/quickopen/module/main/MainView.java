package de.mchllngr.quickopen.module.main;

import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;

import java.util.List;

import de.mchllngr.quickopen.base.BaseView;
import de.mchllngr.quickopen.model.ApplicationModel;

/**
 * Interface for the {@link MainActivity}
 */
interface MainView extends BaseView {

    /**
     * Will update the state of the view that represents if the notification is enabled/disabled.
     */
    void setEnableState(boolean stateEnabled);

    /**
     * Shows the list dialog for choosing one of the installed applications.
     *
     * @param adapter prepared {@code adapter} containing the items to show
     */
    void showApplicationListDialog(MaterialSimpleListAdapter adapter);

    /**
     * Gets the Callback which is called when an item is selected.
     */
    MaterialSimpleListAdapter.Callback getApplicationChooserCallback();

    /**
     * Shows an indeterminate progress dialog while loading.
     */
    void showProgressDialog();

    /**
     * Hides the shown progress dialog when loading is finished.
     */
    void hideProgressDialog();

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

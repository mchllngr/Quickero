package de.mchllngr.quickopen.module.main;

import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;

import java.util.List;

import de.mchllngr.quickopen.base.BaseView;
import de.mchllngr.quickopen.model.ApplicationModel;

/**
 * Interface for the {@link MainActivity}
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
interface MainView extends BaseView {

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
     * Shows an error message.
     */
    void onOpenApplicationListError();

    /**
     * Updates the shown items.
     */
    void updateItems(List<ApplicationModel> items);

    /**
     * Adds an item to the shown list.
     */
    void addItem(ApplicationModel applicationModel);

    /**
     * Removes the item at {@code position} from the shown list.
     */
    void removeItem(int position);

    /**
     * Shows an error message when trying to add an item, but maxCount is already reached.
     */
    void showMaxItemsError();

    /**
     * Shows the button to add items.
     */
    void showAddItemsButton();

    /**
     * Hides the button to add items.
     */
    void hideAddItemsButton();
}

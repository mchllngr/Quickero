package de.mchllngr.quickopen.module.main;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BasePresenter;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.model.RemovedApplicationModel;
import de.mchllngr.quickopen.util.GsonPreferenceAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * {@link com.hannesdorfmann.mosby.mvp.MvpPresenter} for the {@link MainActivity}
 */
@SuppressWarnings("ConstantConditions")
public class MainPresenter extends BasePresenter<MainView> {

    /**
     * Represents the max count of dummy items that can be added on the first start.
     */
    private static final int MAX_DUMMY_ITEMS = 5;

    private final Context context;
    /**
     * {@link Preference}-reference for easier usage of the saved value for firstStart in the
     * {@link RxSharedPreferences}.
     */
    private Preference<Boolean> firstStartPref;
    /**
     * {@link Preference}-reference for easier usage of the saved value for packageNames in the
     * {@link RxSharedPreferences}.
     */
    private Preference<List> packageNamesPref;
    /**
     * Contains the last shown {@link ApplicationModel}s to get the selected item.
     */
    private List<ApplicationModel> lastShownApplicationModels;
    /**
     * Contains the last removed item for undoing.
     */
    private RemovedApplicationModel lastRemovedItem;
    /**
     * Represents the state of the item-list before reordering.
     */
    private List<ApplicationModel> listStateBeforeReorder;

    MainPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);

        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(context));

        firstStartPref = rxSharedPreferences.getBoolean(
                context.getString(R.string.pref_first_start),
                Boolean.parseBoolean(context.getString(R.string.first_start_default_value))
        );

        GsonPreferenceAdapter<List> adapter = new GsonPreferenceAdapter<>(new Gson(), List.class);
        packageNamesPref = rxSharedPreferences.getObject(
                context.getString(R.string.pref_package_names),
                null,
                adapter
        );

        addDummyItemsIfFirstStart();
    }

    /**
     * Adds up to {@code MAX_DUMMY_ITEMS} dummy items to the saved list if its the first start.
     */
    private void addDummyItemsIfFirstStart() {
        if (firstStartPref.get()) {
            List applicationModels = packageNamesPref.get();
            if (applicationModels == null || applicationModels.isEmpty()) {
                if (isViewAttached()) getView().showProgressDialog();

                final List<String> dummyItemsPackageNames = Arrays.asList(context.getResources().getStringArray(R.array.dummy_items_package_names));

                // TODO rebuild with better rxjava-integration
                Observable.from(context.getPackageManager().getInstalledApplications(0))
                        .subscribeOn(Schedulers.newThread())
                        .toList()
                        .toSingle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(applicationInfos -> {
                            List<String> dummyItems = new ArrayList<>();

                            for (int i = 0; i < dummyItemsPackageNames.size(); i++) {
                                for (ApplicationInfo applicationInfo : applicationInfos)
                                    if (dummyItemsPackageNames.get(i).equals(applicationInfo.packageName)) {
                                        dummyItems.add(applicationInfo.packageName);
                                        break;
                                    }

                                if (dummyItems.size() >= MAX_DUMMY_ITEMS)
                                    break;
                            }

                            packageNamesPref.set(dummyItems);

                            loadItems();
                        }, e -> loadItems());
            }

            firstStartPref.set(false);
        }
    }

    /**
     * Loads the list of installed applications, prepares them and calls the {@link MainView}
     * to show them.
     */
    void openApplicationList() {
        if (!isViewAttached()) return;

        getView().showProgressDialog();

        final List<ApplicationModel> savedApplicationModels = ApplicationModel.prepareApplicationModelsList(context, packageNamesPref.get());

        if (savedApplicationModels.size() >= context.getResources().getInteger(R.integer.max_apps_in_notification)) {
            getView().hideAddItemsButton();
            getView().hideProgressDialog();
            getView().showMaxItemsError();
            return;
        }

        // TODO rebuild with better rxjava-integration
        Observable.from(context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES))
                .subscribeOn(Schedulers.newThread())
                .filter(packageInfo -> ApplicationModel.isLaunchable(context, packageInfo.packageName))
                .filter(packageInfo -> !isAppAlreadyInList(savedApplicationModels, packageInfo))
                .map(packageInfo -> packageInfo.applicationInfo)
                .map(applicationInfo -> ApplicationModel.getApplicationModelForPackageName(context, applicationInfo.packageName))
                .filter(applicationModel -> applicationModel != null &&
                        !TextUtils.isEmpty(applicationModel.packageName) &&
                        !TextUtils.isEmpty(applicationModel.name) &&
                        applicationModel.iconDrawable != null &&
                        applicationModel.iconBitmap != null)
                .toSortedList((applicationModel, applicationModel2) -> applicationModel.name.toLowerCase().compareTo(applicationModel2.name.toLowerCase()))
                .toSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(applicationList -> {
                    if (!isViewAttached()) return;

                    if (applicationList.isEmpty()) {
                        getView().onEmptyApplicationListError();
                        getView().hideProgressDialog();
                        return;
                    }

                    lastShownApplicationModels = applicationList;

                    final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(getView().getApplicationChooserCallback());

                    for (ApplicationModel applicationModel : applicationList) {
                        adapter.add(new MaterialSimpleListItem.Builder(context)
                                .content(applicationModel.name)
                                .icon(applicationModel.iconDrawable)
                                .backgroundColor(Color.WHITE)
                                .build());
                    }

                    getView().hideProgressDialog();
                    getView().showApplicationListDialog(adapter);
                }, e -> {
                    if (isViewAttached()) {
                        getView().hideProgressDialog();
                        getView().onOpenApplicationListError();
                    }
                });
    }

    /**
     * Checks is the given app is already in the list.
     */
    private boolean isAppAlreadyInList(List<ApplicationModel> savedApplicationModels, PackageInfo packageInfo) {
        boolean isAlreadyInList = false;
        for (ApplicationModel savedApplicationModel : savedApplicationModels)
            if (packageInfo.packageName.equals(savedApplicationModel.packageName)) {
                isAlreadyInList = true;
                break;
            }

        return isAlreadyInList;
    }

    /**
     * Gets called when an item from the application-list is selected.
     */
    void onApplicationSelected(int position) {
        if (lastShownApplicationModels != null && !lastShownApplicationModels.isEmpty())
            addItem(lastShownApplicationModels.get(position));
    }

    /**
     * Gets called when the Reorder-Icon is clicked and enables the Reorder-Mode.
     *
     * @param currentState Current state of the list
     */
    void onReorderIconClick(@NonNull List<ApplicationModel> currentState) {
        if (currentState.size() < 2) return;

        listStateBeforeReorder = new ArrayList<>();
        listStateBeforeReorder.addAll(currentState);

        if (isViewAttached()) {
            getView().hideAddItemsButton();
            getView().setReorderMode(true);
        }
    }

    /**
     * Gets called when the Reorder-Accept-Icon is clicked.
     *
     * @param newState New state of the list
     */
    void onReorderAcceptIconClick(List<ApplicationModel> newState) {
        if (isViewAttached()) {
            getView().showProgressDialog();
            getView().showAddItemsButton();
            getView().setReorderMode(false);
        }

        List<String> newStateToSave = new ArrayList<>();
        for (ApplicationModel applicationModel : newState)
            if (applicationModel != null && !TextUtils.isEmpty(applicationModel.packageName))
                newStateToSave.add(applicationModel.packageName);

        packageNamesPref.set(newStateToSave);

        if (isViewAttached())
            getView().hideProgressDialog();
    }

    /**
     * Gets called when the Reorder-Cancel-Icon is clicked.
     */
    void onReorderCancelIconClick() {
        if (isViewAttached()) {
            getView().showProgressDialog();
            getView().showAddItemsButton();
            getView().setReorderMode(false);
            getView().updateItems(listStateBeforeReorder);
            getView().hideProgressDialog();
        }
    }

    /**
     * Loads the list saved in {@link RxSharedPreferences} and calls the {@link MainView} to
     * show them.
     */
    void loadItems() {
        if (!isViewAttached()) return;

        getView().showProgressDialog();

        List<ApplicationModel> applicationModels = ApplicationModel.prepareApplicationModelsList(context, packageNamesPref.get());

        if (applicationModels.isEmpty())
            getView().setEmptyListViewVisibility(true);
        else {
            getView().setEmptyListViewVisibility(false);

            if (applicationModels.size() >= context.getResources()
                    .getInteger(R.integer.max_apps_in_notification))
                getView().hideAddItemsButton();
        }

        getView().updateItems(applicationModels);

        getView().hideProgressDialog();
    }

    /**
     * Adds an item at the end of the list in {@link android.content.SharedPreferences} and calls
     * the {@link MainView} to also add it to the shown list.
     */
    private void addItem(ApplicationModel applicationModel) {
        addItem(Integer.MAX_VALUE, applicationModel);
    }

    /**
     * Adds an item at {@code position} to the list in {@link android.content.SharedPreferences}
     * and calls the {@link MainView} to also add it to the shown list.
     */
    @SuppressWarnings("unchecked")
    private void addItem(int position, ApplicationModel applicationModel) {
        List applicationModels = packageNamesPref.get();

        if (applicationModels == null)
            applicationModels = new ArrayList();

        int maxApplications = context.getResources().getInteger(R.integer.max_apps_in_notification);
        int itemsLeftToAdd = maxApplications - applicationModels.size();

        if (isViewAttached()) {
            if (itemsLeftToAdd == 1)
                getView().hideAddItemsButton();
            else if (itemsLeftToAdd <= 0) {
                getView().hideAddItemsButton();
                getView().showMaxItemsError();
                return;
            }
        }

        if (position >= applicationModels.size())
            applicationModels.add(applicationModel.packageName);
        else
            applicationModels.add(position, applicationModel.packageName);

        packageNamesPref.set(applicationModels);

        if (isViewAttached()) {
            getView().setEmptyListViewVisibility(false);
            getView().addItem(position, applicationModel);
        }
    }

    /**
     * Removes the item at {@code position} from the list in
     * {@link android.content.SharedPreferences} and calls the {@link MainView} to also remove
     * it from the shown list.
     */
    void removeItem(int position) {
        List applicationModels = packageNamesPref.get();

        if (applicationModels == null) {
            packageNamesPref.delete();
            return;
        }

        lastRemovedItem = new RemovedApplicationModel(
                position,
                ApplicationModel.getApplicationModelForPackageName(context, (String) applicationModels.get(position))
        );

        applicationModels.remove(position);
        if (!applicationModels.isEmpty())
            packageNamesPref.set(applicationModels);
        else
            packageNamesPref.delete();

        if (isViewAttached()) {
            getView().setEmptyListViewVisibility(applicationModels.isEmpty());
            getView().showAddItemsButton();
            getView().removeItem(position);
            getView().showUndoButton();
        }
    }

    void undoRemove() {
        if (lastRemovedItem == null) return;

        addItem(lastRemovedItem.position, lastRemovedItem.applicationModel);

        if (isViewAttached())
            getView().dismissSnackbar();
    }
}

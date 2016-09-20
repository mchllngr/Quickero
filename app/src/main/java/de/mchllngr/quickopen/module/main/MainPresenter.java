package de.mchllngr.quickopen.module.main;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BasePresenter;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.model.RemovedApplicationModel;
import de.mchllngr.quickopen.util.GsonPreferenceAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * {@link com.hannesdorfmann.mosby.mvp.MvpPresenter} for the {@link MainActivity}
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
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

    MainPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
        getApplicationComponent().inject(this);

        RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(
                PreferenceManager.getDefaultSharedPreferences(context)
        );

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

                final List<String> dummyItemsPackageNames = Arrays.asList(
                        context.getResources().getStringArray(R.array.dummy_items_package_names)
                );

                Observable.from(context.getPackageManager().getInstalledApplications(0))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toList()
                        .subscribe(new Observer<List<ApplicationInfo>>() {
                            @Override
                            public void onCompleted() {
                                loadItems();
                            }

                            @Override
                            public void onError(Throwable e) {
                                loadItems();
                            }

                            @Override
                            public void onNext(List<ApplicationInfo> applicationInfos) {
                                List<String> dummyitems = new ArrayList<>();

                                for (int i = 0; i < dummyItemsPackageNames.size(); i++) {
                                    for (ApplicationInfo applicationInfo : applicationInfos)
                                        if (dummyItemsPackageNames.get(i)
                                                .equals(applicationInfo.packageName)) {
                                            dummyitems.add(applicationInfo.packageName);
                                            break;
                                        }

                                    if (dummyitems.size() >= MAX_DUMMY_ITEMS)
                                        break;
                                }

                                packageNamesPref.set(dummyitems);
                            }
                        });
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
        final List<ApplicationModel> savedApplicationModels = ApplicationModel
                .prepareApplicationModelsList(
                        context,
                        packageNamesPref.get()
                );

        if (savedApplicationModels.size() >= context.getResources()
                .getInteger(R.integer.max_apps_in_notification)) {
            getView().hideAddItemsButton();
            getView().hideProgressDialog();
            getView().showMaxItemsError();
            return;
        }

        Observable.from(context.getPackageManager().getInstalledApplications(0))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<ApplicationInfo, Boolean>() {
                    @Override
                    public Boolean call(ApplicationInfo applicationInfo) {
                        if (isSystemPackage(applicationInfo) &&
                                !TextUtils.isEmpty(applicationInfo.packageName))
                            return false;

                        boolean isAlreadyInList = false;
                        for (ApplicationModel savedApplicationModel : savedApplicationModels)
                            if (applicationInfo.packageName
                                    .equals(savedApplicationModel.packageName)) {
                                isAlreadyInList = true;
                                break;
                            }

                        return !isAlreadyInList;
                    }
                })
                .map(new Func1<ApplicationInfo, ApplicationModel>() {
                    @Override
                    public ApplicationModel call(ApplicationInfo applicationInfo) {
                        return ApplicationModel.getApplicationModelForPackageName(
                                context,
                                applicationInfo.packageName
                        );
                    }
                })
                .filter(new Func1<ApplicationModel, Boolean>() {
                    @Override
                    public Boolean call(ApplicationModel applicationModel) {
                        return applicationModel != null &&
                                !TextUtils.isEmpty(applicationModel.packageName) &&
                                !TextUtils.isEmpty(applicationModel.name) &&
                                applicationModel.iconDrawable != null &&
                                applicationModel.iconBitmap != null;
                    }
                })
                .toSortedList(new Func2<ApplicationModel, ApplicationModel, Integer>() {
                    @Override
                    public Integer call(ApplicationModel applicationModel, ApplicationModel applicationModel2) {
                        return applicationModel.name.compareTo(applicationModel2.name);
                    }
                })
                .subscribe(new Observer<List<ApplicationModel>>() {
                    @Override
                    public void onCompleted() {
                        getView().hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgressDialog();
                        getView().onOpenApplicationListError();
                    }

                    @Override
                    public void onNext(List<ApplicationModel> applicationModels) {
                        if (!isViewAttached()) return;

                        lastShownApplicationModels = applicationModels;

                        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(
                                getView().getApplicationChooserCallback()
                        );

                        for (ApplicationModel applicationModel : applicationModels) {
                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                    .content(applicationModel.name)
                                    .icon(applicationModel.iconDrawable)
                                    .backgroundColor(Color.WHITE)
                                    .build());
                        }

                        getView().showApplicationListDialog(adapter);
                    }
                });
    }

    /**
     * Checks if an application is a system application.
     */
    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    /**
     * Gets called when an item from the application-list is selected.
     */
    void onApplicationSelected(int position) {
        if (lastShownApplicationModels != null && !lastShownApplicationModels.isEmpty())
            addItem(lastShownApplicationModels.get(position));
    }

    /**
     * Loads the list saved in {@link RxSharedPreferences} and calls the {@link MainView} to
     * show them.
     */
    void loadItems() {
        if (!isViewAttached()) return;

        getView().showProgressDialog();

        List<ApplicationModel> applicationModels = ApplicationModel.prepareApplicationModelsList(
                context,
                packageNamesPref.get()
        );

        if (applicationModels.size() >= context.getResources()
                .getInteger(R.integer.max_apps_in_notification))
            getView().hideAddItemsButton();

        getView().updateItems(applicationModels);

        getView().hideProgressDialog();
    }

    /**
     * Adds an item at the end of the list in {@link android.content.SharedPreferences} and calls
     * the {@link MainView} to also add it to the shown list.
     */
    void addItem(ApplicationModel applicationModel) {
        addItem(Integer.MAX_VALUE, applicationModel);
    }

    /**
     * Adds an item at {@code position} to the list in {@link android.content.SharedPreferences}
     * and calls the {@link MainView} to also add it to the shown list.
     */
    void addItem(int position, ApplicationModel applicationModel) {
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

        if (isViewAttached())
            getView().addItem(position, applicationModel);
    }

    /**
     * Removes the item at {@code position} from the list in
     * {@link android.content.SharedPreferences} and calls the {@link MainView} to also remove
     * it from the shown list.
     */
    void removeItem(int position) {
        List applicationModels = packageNamesPref.get();

        lastRemovedItem = new RemovedApplicationModel(
                position,
                ApplicationModel.getApplicationModelForPackageName(
                        context,
                        (String) applicationModels.get(position)
                )
        );

        if (applicationModels != null && applicationModels.size() > 1) {
            applicationModels.remove(position);
            packageNamesPref.set(applicationModels);
        } else
            packageNamesPref.delete();

        if (isViewAttached()) {
            getView().showAddItemsButton();
            getView().removeItem(position);
            getView().showUndoButton();
        }
    }

    /**
     * Moves an item at {@code fromPosition} to {@code toPosition} from the list in
     * {@link android.content.SharedPreferences} and calls the {@link MainView} to also move
     * it in the shown list.
     */
    void moveItem(int fromPosition, int toPosition) {
        List applicationModels = packageNamesPref.get();

        if (applicationModels == null || applicationModels.isEmpty()) return;

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(applicationModels, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(applicationModels, i, i - 1);
            }
        }

        packageNamesPref.set(applicationModels);

        if (isViewAttached())
            getView().moveItem(fromPosition, toPosition);
    }

    void undoRemove() {
        if (lastRemovedItem == null) return;

        addItem(lastRemovedItem.position, lastRemovedItem.applicationModel);

        if (isViewAttached())
            getView().hideUndoButton();
    }
}

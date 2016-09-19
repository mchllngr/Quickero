package de.mchllngr.quickopen.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BaseActivity;
import de.mchllngr.quickopen.model.ApplicationModel;
import de.mchllngr.quickopen.module.settings.SettingsActivity;
import de.mchllngr.quickopen.service.NotificationService;

/**
 * {@link android.app.Activity} for handling the selection of applications.
 *
 * @author Michael Langer (<a href="https://github.com/mchllngr" target="_blank">GitHub</a>)
 */
public class MainActivity extends BaseActivity<MainView, MainPresenter>
        implements MainView, MaterialSimpleListAdapter.Callback {

    /**
     * {@link android.support.design.widget.CoordinatorLayout} from the layout for showing the
     * {@link Snackbar}.
     *
     * @see Snackbar
     */
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    /**
     * {@link Toolbar} for this {@link android.app.Activity}.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /**
     * {@link android.support.v7.widget.RecyclerView} for showing list of items.
     */
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    /**
     * {@link android.support.design.widget.FloatingActionButton} for adding items.
     */
    @BindView(R.id.fab)
    FloatingActionButton fab;

    /**
     * {@link MainAdapter} for updating shown items in {@code recyclerView}.
     */
    private MainAdapter adapter;
    /**
     * {@link MaterialDialog} for showing the installed application-list.
     */
    private MaterialDialog applicationDialog;
    /**
     * {@link MaterialDialog} for showing the loading-process of the installed applications.
     */
    private MaterialDialog progressDialog;

    /**
     * {@link Snackbar} for showing the undo-remove-button.
     */
    private Snackbar snackbar;

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initRecyclerView();

        startNotificationService();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().openApplicationList();
            }
        });
    }

    /**
     * Initialises the {@code recyclerView}.
     */
    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MainAdapter(new ArrayList<ApplicationModel>());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                getPresenter().moveItem(
                        viewHolder.getAdapterPosition(),
                        target.getAdapterPosition()
                );

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                getPresenter().removeItem(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getPresenter().loadItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                SettingsActivity.start(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    /**
     * Starts the {@link NotificationService}.
     */
    private void startNotificationService() {
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void onMaterialListItemSelected(int index, MaterialSimpleListItem item) {
        getPresenter().onApplicationSelected(index);

        if (applicationDialog != null)
            applicationDialog.dismiss();
    }

    @Override
    public void showApplicationListDialog(MaterialSimpleListAdapter adapter) {
        applicationDialog = new MaterialDialog.Builder(this)
                .title("DialogTitle") // TODO change
                .adapter(adapter, null)
                .show();
    }

    @Override
    public MaterialSimpleListAdapter.Callback getApplicationChooserCallback() {
        return this;
    }

    @Override
    public void showProgressDialog() {
        hideProgressDialog();

        progressDialog = new MaterialDialog.Builder(this)
                .content("ProgressContent") // TODO change
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onOpenApplicationListError() {
        Log.d("DEBUG_TAG", "MainActivity#onOpenApplicationListError()"); // FIXME delete
        // TODO show error msg
    }

    @Override
    public void updateItems(List<ApplicationModel> items) {
        if (adapter != null)
            adapter.updateItems(items);
    }

    @Override
    public void addItem(int position, ApplicationModel applicationModel) {
        if (adapter != null)
            adapter.add(position, applicationModel);
    }

    @Override
    public void removeItem(int position) {
        if (adapter != null)
            adapter.remove(adapter.get(position));
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (adapter != null)
            adapter.move(fromPosition, toPosition);
    }

    @Override
    public void showMaxItemsError() {
        Log.d("DEBUG_TAG", "MainActivity#showMaxItemsError()"); // FIXME delete
        // TODO show error msg
    }

    @Override
    public void showAddItemsButton() {
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAddItemsButton() {
        fab.setVisibility(View.GONE);
    }

    @Override
    public void showUndoButton() {
        hideUndoButton();

        // TODO change texts
        snackbar = Snackbar
                .make(coordinatorLayout, "Item removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPresenter().undoRemove();
                        snackbar.dismiss();
                    }
                });

        snackbar.show();
    }

    @Override
    public void hideUndoButton() {
        if (snackbar != null)
            snackbar.dismiss();
    }
}

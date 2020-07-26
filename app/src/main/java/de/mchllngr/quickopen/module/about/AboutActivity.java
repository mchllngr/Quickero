package de.mchllngr.quickopen.module.about;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import de.mchllngr.quickopen.BuildConfig;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BaseActivity;
import de.mchllngr.quickopen.base.BasePresenter;
import de.mchllngr.quickopen.base.BaseView;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * {@link Activity} for the AboutPage.
 */
public class AboutActivity extends BaseActivity<BaseView, BasePresenter<BaseView>> implements BaseView {

    private static final String DESCRIPTION = "TODO description"; // TODO set description
    private static final String EMAIL = "quickopen@michaellanger.me";
    private static final String GITHUB = "mchllngr/quickopen";

    public static void start(Context context) {
        Intent starter = new Intent(context, AboutActivity.class);
        context.startActivity(starter);
    }

    @NonNull
    @Override
    public BasePresenter<BaseView> createPresenter() {
        return new BasePresenter<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildAboutPage());
    }

    /**
     * Builds the {@link View} of the {@link AboutActivity}.
     */
    private View buildAboutPage() {
        Element versionElement = new Element();
        versionElement.setTitle("Version " + BuildConfig.VERSION_NAME);

        return new AboutPage(this)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(DESCRIPTION)
                .addItem(versionElement)
                .addEmail(EMAIL)
                .addPlayStore(BuildConfig.APPLICATION_ID)
                .addGitHub(GITHUB)
                .create();
    }
}

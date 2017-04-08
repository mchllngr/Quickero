package de.mchllngr.quickopen.module.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import de.mchllngr.quickopen.BuildConfig;
import de.mchllngr.quickopen.R;
import de.mchllngr.quickopen.base.BaseActivity;
import de.mchllngr.quickopen.base.BasePresenter;
import de.mchllngr.quickopen.base.BaseView;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * {@link android.app.Activity} for the AboutPage.
 *
 * @author Michael Langer <a href="https://github.com/mchllngr" target="_blank">(GitHub)</a>
 */
public class AboutActivity extends BaseActivity<BaseView, BasePresenter<BaseView>>
        implements BaseView {

    /**
     * Static factory method that initializes and starts the {@link AboutActivity}.
     */
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
                .setImage(R.mipmap.ic_launcher) // TODO set image
//                .setDescription(DESCRIPTION) // TODO set description
                .addItem(versionElement)
                .addWebsite("http://mchllngr.de/") // TODO set website
                .addEmail("quickopen@mlanger.net") // TODO set email
                .addPlayStore(BuildConfig.APPLICATION_ID)
                .addGitHub("mchllngr/quickopen") // TODO set github
                .create();
    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }
}

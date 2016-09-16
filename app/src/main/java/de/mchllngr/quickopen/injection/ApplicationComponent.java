package de.mchllngr.quickopen.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.mchllngr.quickopen.module.main.MainPresenter;

/**
 * Dagger2-component for the whole application.
 *
 * @author Michael Langer <a href="https://github.com/mchllngr" target="_blank">(GitHub)</a>
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainPresenter presenter);
}

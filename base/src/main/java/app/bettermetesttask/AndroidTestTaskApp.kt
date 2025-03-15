package app.bettermetesttask

import android.app.Application
import android.widget.Toast
import app.bettermetesttask.featurecommon.initializers.AppInitializers
import app.bettermetesttask.featurecommon.injection.utils.AppInjector
import app.bettermetesttask.injection.components.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

class AndroidTestTaskApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var appInitializers: AppInitializers

    override fun onCreate() {
        super.onCreate()
        setupGlobalExceptionHandler()

        AppInjector.init(this) {
            DaggerAppComponent.builder().application(this).appContext(this)
                .build().inject(this)
        }

        appInitializers.init(this)
    }

    private fun setupGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val errorMsg = "GlobalException: uncaught exception on thread ${thread.name}: $throwable"
            Timber.e(errorMsg)

            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()

            exitProcess(1)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}
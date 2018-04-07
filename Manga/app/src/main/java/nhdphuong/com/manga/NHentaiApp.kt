package nhdphuong.com.manga

import android.app.Application

/*
 * Created by nhdphuong on 3/21/18.
 */
class NHentaiApp : Application() {
    companion object {
        private lateinit var mInstance: NHentaiApp
        val instance
            get() = mInstance
    }

    private lateinit var mApplicationComponent: ApplicationComponent

    val applicationComponent
        get() = mApplicationComponent

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mApplicationComponent = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()
    }
}
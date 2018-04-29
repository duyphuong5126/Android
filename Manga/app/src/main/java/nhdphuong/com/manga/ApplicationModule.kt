package nhdphuong.com.manga

import dagger.Module
import dagger.Provides
import nhdphuong.com.manga.api.ApiConstants
import nhdphuong.com.manga.api.BookApiService
import nhdphuong.com.manga.supports.ServiceGenerator
import javax.inject.Singleton

/*
 * Created by nhdphuong on 3/21/18.
 */
@Module
class ApplicationModule(private val mApplication: NHentaiApp) {
    @Provides
    fun provideContext() = mApplication.applicationContext!!

    @Provides
    fun provideApplication() = mApplication

    @Singleton
    @Provides
    fun provideHomeApiService(): BookApiService {
        ServiceGenerator.setBaseUrl(ApiConstants.NHENTAI_HOME)
        ServiceGenerator.setInterceptor(null)
        return ServiceGenerator.createService(BookApiService::class.java)
    }
}
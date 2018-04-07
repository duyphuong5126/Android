package nhdphuong.com.manga

import dagger.Component
import dagger.Provides
import nhdphuong.com.manga.data.RepositoryModule
import nhdphuong.com.manga.data.repository.BookRepository
import nhdphuong.com.manga.features.home.HomeComponent
import nhdphuong.com.manga.features.home.HomeModule
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Created by nhdphuong on 3/21/18.
 */
@Singleton
@Component(modules = [ApplicationModule::class, RepositoryModule::class])
interface ApplicationComponent {
    fun plus(homeModule: HomeModule): HomeComponent
}
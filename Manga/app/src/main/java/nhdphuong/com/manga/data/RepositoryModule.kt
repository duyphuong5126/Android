package nhdphuong.com.manga.data

import android.support.annotation.NonNull
import dagger.Module
import dagger.Provides
import nhdphuong.com.manga.api.BookApiService
import nhdphuong.com.manga.data.remote.BookRemoteDataSource
import nhdphuong.com.manga.scope.Remote
import javax.inject.Singleton

/*
 * Created by nhdphuong on 3/24/18.
 */
@Module
class RepositoryModule {
    @Provides
    @NonNull
    @Singleton
    @Remote
    fun provideBookDataSource(bookApiService: BookApiService): BookDataSource = BookRemoteDataSource(bookApiService)
}
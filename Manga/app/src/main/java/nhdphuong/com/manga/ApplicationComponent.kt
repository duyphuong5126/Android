package nhdphuong.com.manga

import dagger.Component
import nhdphuong.com.manga.data.RepositoryModule
import nhdphuong.com.manga.features.header.HeaderModule
import nhdphuong.com.manga.features.home.HomeComponent
import nhdphuong.com.manga.features.home.HomeModule
import nhdphuong.com.manga.features.preview.BookPreviewComponent
import nhdphuong.com.manga.features.preview.BookPreviewModule
import nhdphuong.com.manga.features.reader.ReaderComponent
import nhdphuong.com.manga.features.reader.ReaderModule
import nhdphuong.com.manga.features.tags.TagsComponent
import nhdphuong.com.manga.features.tags.TagsModule
import javax.inject.Singleton

/*
 * Created by nhdphuong on 3/21/18.
 */
@Singleton
@Component(modules = [ApplicationModule::class, RepositoryModule::class])
interface ApplicationComponent {
    fun plus(homeModule: HomeModule, headerModule: HeaderModule): HomeComponent
    fun plus(bookPreviewModule: BookPreviewModule): BookPreviewComponent
    fun plus(readerModule: ReaderModule): ReaderComponent
    fun plus(tagsModule: TagsModule, headerModule: HeaderModule): TagsComponent
}
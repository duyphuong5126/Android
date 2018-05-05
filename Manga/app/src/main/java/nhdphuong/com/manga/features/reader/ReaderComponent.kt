package nhdphuong.com.manga.features.reader

import dagger.Subcomponent

/*
 * Created by nhdphuong on 5/5/18.
 */
@Subcomponent(modules = [ReaderModule::class])
interface ReaderComponent {
    fun inject(readerActivity: ReaderActivity)
}
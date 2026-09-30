package dunbar.mike.mediabrowser.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dunbar.mike.mediabrowser.data.user.RealUserDataSource
import dunbar.mike.mediabrowser.data.user.UserDataSource

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    fun provideUserDataDataSource(@ApplicationContext context: Context): UserDataSource = RealUserDataSource(context)
}
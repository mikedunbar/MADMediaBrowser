package dunbar.mike.mediabrowser.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dunbar.mike.mediabrowser.util.AndroidLogger
import dunbar.mike.mediabrowser.util.Logger

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {

    @Binds
    abstract fun provideLogger(impl: AndroidLogger): Logger
}
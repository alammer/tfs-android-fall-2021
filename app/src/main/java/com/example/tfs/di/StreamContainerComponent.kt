package com.example.tfs.di

import com.example.tfs.database.MessengerDataDao
import com.example.tfs.di.core.AppComponent
import com.example.tfs.domain.streams.StreamInteractor
import com.example.tfs.domain.streams.StreamRepository
import com.example.tfs.domain.streams.StreamRepositoryImpl
import com.example.tfs.network.ApiService
import com.example.tfs.ui.stream.streamcontainer.StreamContainerFragment
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerActor
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope


@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class StreamContainerScope

@StreamContainerScope
@Component(modules = [StreamContainerModule::class],
    dependencies = [AppComponent::class])
interface StreamContainerComponent {

    fun inject(streamContainerFragment: StreamContainerFragment)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): StreamContainerComponent
    }
}

@Module
class StreamContainerModule {

    @Provides
    fun provideStreamRepository(service: ApiService, database: MessengerDataDao): StreamRepository {
        return StreamRepositoryImpl(service, database)
    }

    @Provides
    fun provideStreamContainerActor(interactor: StreamInteractor): StreamContainerActor {
        return StreamContainerActor(interactor)
    }
}
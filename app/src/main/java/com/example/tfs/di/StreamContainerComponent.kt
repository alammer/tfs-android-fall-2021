package com.example.tfs.di

import com.example.tfs.di.app.AppComponent
import com.example.tfs.domain.stream.StreamInteractor
import com.example.tfs.domain.stream.StreamRepository
import com.example.tfs.domain.stream.StreamRepositoryImpl
import com.example.tfs.ui.stream.streamcontainer.StreamContainerFragment
import com.example.tfs.ui.stream.streamcontainer.elm.StreamContainerActor
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope


@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class StreamContainerScope

@StreamContainerScope
@Component(
    modules = [StreamContainerModule::class, StreamBindings::class],
    dependencies = [AppComponent::class]
)
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

    @StreamContainerScope
    @Provides
    internal fun provideStreamContainerActor(interactor: StreamInteractor): StreamContainerActor {
        return StreamContainerActor(interactor)
    }
}

@Module
interface StreamContainerBindings {

    @Binds
    fun bindStreamRepository_to_Impl(impl: StreamRepositoryImpl): StreamRepository
}
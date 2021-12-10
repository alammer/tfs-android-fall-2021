package com.example.tfs.di

import com.example.tfs.di.core.AppComponent
import com.example.tfs.domain.streams.StreamInteractor
import com.example.tfs.domain.streams.StreamRepository
import com.example.tfs.domain.streams.StreamRepositoryImpl
import com.example.tfs.ui.stream.StreamFragment
import com.example.tfs.ui.stream.elm.StreamActor
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class StreamScope

@StreamScope
@Component(
    modules = [StreamModule::class, StreamBindings::class],
    dependencies = [AppComponent::class]
)
interface StreamComponent {

    fun inject(streamFragment: StreamFragment)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): StreamComponent
    }
}

@Module
class StreamModule {

    @StreamScope
    @Provides
    internal fun provideStreamActor(interactor: StreamInteractor): StreamActor {
        return StreamActor(interactor)
    }
}

@Module
interface StreamBindings {

    @Binds
    fun bindStreamRepository_to_Impl(impl: StreamRepositoryImpl): StreamRepository
}
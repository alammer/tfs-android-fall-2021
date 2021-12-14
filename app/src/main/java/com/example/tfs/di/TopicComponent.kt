package com.example.tfs.di

import com.example.tfs.di.app.AppComponent
import com.example.tfs.domain.topic.TopicInteractor
import com.example.tfs.domain.topic.TopicRepository
import com.example.tfs.domain.topic.TopicRepositoryImpl
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.ui.topic.elm.TopicActor
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class TopicScope

@TopicScope
@Component(
    modules = [TopicModule::class, TopicBindings::class],
    dependencies = [AppComponent::class]
)
interface TopicComponent {

    fun inject(topicFragment: TopicFragment)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): TopicComponent
    }
}

@Module
class TopicModule {

    @Provides
    fun provideTopicActor(interactor: TopicInteractor): TopicActor {
        return TopicActor(interactor)
    }
}

@Module
interface TopicBindings {

    @Binds
    fun bindTopicRepository_to_Impl(impl: TopicRepositoryImpl): TopicRepository
}
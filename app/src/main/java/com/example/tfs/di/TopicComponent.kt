package com.example.tfs.di

import com.example.tfs.database.MessengerDataDao
import com.example.tfs.di.core.AppComponent
import com.example.tfs.domain.topic.TopicInteractor
import com.example.tfs.domain.topic.TopicRepository
import com.example.tfs.domain.topic.TopicRepositoryImpl
import com.example.tfs.network.ApiService
import com.example.tfs.ui.topic.TopicFragment
import com.example.tfs.ui.topic.elm.TopicActor
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class TopicScope

@TopicScope
@Component(modules = [TopicModule::class],
    dependencies = [AppComponent::class])
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
    fun provideTopicRepository(service: ApiService, database: MessengerDataDao): TopicRepository {
        return TopicRepositoryImpl(service, database)
    }

    @Provides
    fun provideTopicActor(interactor: TopicInteractor): TopicActor {
        return TopicActor(interactor)
    }

/*    @Provides
    fun provideTopicInteractor(repository: TopicRepository): TopicInteractor {
        return TopicInteractor(repository)
    }*/
}
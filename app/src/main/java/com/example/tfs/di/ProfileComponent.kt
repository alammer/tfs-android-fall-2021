package com.example.tfs.di

import com.example.tfs.di.core.AppComponent
import com.example.tfs.domain.contacts.ContactInteractor
import com.example.tfs.domain.contacts.ContactRepository
import com.example.tfs.domain.contacts.ContactRepositoryImpl
import com.example.tfs.ui.profile.ProfileFragment
import com.example.tfs.ui.profile.elm.ProfileActor
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ProfileScope

@ProfileScope
@Component(
    modules = [ProfileModule::class, ProfileBindings::class],
    dependencies = [AppComponent::class]
)
interface ProfileComponent {

    fun inject(profileFragment: ProfileFragment)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): ProfileComponent
    }
}

@Module
class ProfileModule {

    @Provides
    fun provideProfileActor(interactor: ContactInteractor): ProfileActor {
        return ProfileActor(interactor)
    }
}

@Module
interface ProfileBindings {

    @Binds
    fun bindContactRepository_to_Impl(impl: ContactRepositoryImpl): ContactRepository
}
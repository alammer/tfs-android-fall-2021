package com.example.tfs.di

import com.example.tfs.di.app.AppComponent
import com.example.tfs.domain.contacts.ContactInteractor
import com.example.tfs.domain.contacts.ContactRepository
import com.example.tfs.domain.contacts.ContactRepositoryImpl
import com.example.tfs.ui.contacts.ContactsFragment
import com.example.tfs.ui.contacts.elm.ContactActor
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ContactsScope

@ContactsScope
@Component(
    modules = [ContactsModule::class, ContactBindings::class],
    dependencies = [AppComponent::class]
)
interface ContactsComponent {

    fun inject(contactsFragment: ContactsFragment)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): ContactsComponent
    }
}

@Module
class ContactsModule {

    @Provides
    fun provideContactActor(interactor: ContactInteractor): ContactActor {
        return ContactActor(interactor)
    }
}

@Module
interface ContactBindings {

    @Binds
    fun bindContactRepository_to_Impl(impl: ContactRepositoryImpl): ContactRepository
}
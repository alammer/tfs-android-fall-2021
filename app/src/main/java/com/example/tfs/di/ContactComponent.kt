package com.example.tfs.di

import com.example.tfs.di.app.AppComponent
import com.example.tfs.domain.contact.ContactInteractor
import com.example.tfs.domain.contact.ContactRepository
import com.example.tfs.domain.contact.ContactRepositoryImpl
import com.example.tfs.ui.contacts.ContactFragment
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
    modules = [ContactModule::class, ContactBindings::class],
    dependencies = [AppComponent::class]
)
interface ContactComponent {

    fun inject(contactFragment: ContactFragment)

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): ContactComponent
    }
}

@Module
class ContactModule {

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
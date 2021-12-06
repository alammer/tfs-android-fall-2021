package com.example.tfs.di

import android.content.Context
import com.example.tfs.ui.main.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}
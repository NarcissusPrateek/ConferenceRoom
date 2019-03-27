package com.example.conferencerommapp.Components

import com.example.conferencerommapp.Activity.BuildingsActivity
import com.example.conferencerommapp.Modules.RetrofitModule
import com.example.conferencerommapp.services.ConferenceService
import dagger.Component

@Component(modules = [RetrofitModule::class])
interface ApplicationComponent {

    fun getApiInterface() : ConferenceService
    fun inject(mBuildingsActivity: BuildingsActivity)
}


package com.example.conferencerommapp;

import android.app.Application;
import android.content.ServiceConnection;
import com.example.conferencerommapp.Components.ApplicationComponent;
import com.example.conferencerommapp.Modules.RetrofitModule;

public class ConferenceApplication extends Application {

    private ApplicationComponent applicationComponent;

    public RetrofitModule getRetrofitModle(){
        return new RetrofitModule(this);
    }

    public ApplicationComponent getComponent(){
        return applicationComponent;
    }
}

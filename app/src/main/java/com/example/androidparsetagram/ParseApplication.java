package com.example.androidparsetagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

//Application extends root application class
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Required to register Parse model (such as Post)
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("V0xQXYm7nYVCrkLXj8iBz5tdBL4oGX632IchoJZ3")
                .clientKey("2coQH30VDhdO1Ix7rjtm7xNPaDVy3CyzdUtGAWUz")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}

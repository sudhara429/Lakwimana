package com.lakwimana.lakwimana;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by Sudhara on 5/7/2016.
 */
public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS="Registration Success";
    public static final String REGISTRATION_ERROR="Registration Error";

    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    registerGCM();

    }

    private void registerGCM(){
        Intent registrationComplete=null;
        String token=null;
        try {
            InstanceID instanceID= InstanceID.getInstance(getApplicationContext());
            token=instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            Log.w("GCMRegIntentService","token:" + token);
            //notify that ui reg success
            registrationComplete= new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token",token);
        }catch (Exception e){
            Log.w("GCMRegIntentService","Registration Error");
            registrationComplete= new Intent(REGISTRATION_ERROR);

        }
        //send Brodcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}

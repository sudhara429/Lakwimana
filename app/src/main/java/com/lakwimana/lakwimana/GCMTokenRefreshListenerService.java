package com.lakwimana.lakwimana;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Sudhara on 5/7/2016.
 */
public class GCMTokenRefreshListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
    Intent intent= new Intent(this,GCMRegistrationIntentService.class);
        startService(intent);

    }
}

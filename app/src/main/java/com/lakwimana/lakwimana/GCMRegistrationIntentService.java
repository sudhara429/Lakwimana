package com.lakwimana.lakwimana;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.lakwimana.lakwimana.backend.registration.Registration;

import java.io.IOException;

/**
 * Created by Sudhara on 5/7/2016.
 */
public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS="Registration Success";
    public static final String REGISTRATION_ERROR="Registration Error";
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    registerGCM();

    }

    private void registerGCM(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent registrationComplete=null;
        String token=null;
        try {
            InstanceID instanceID= InstanceID.getInstance(getApplicationContext());
            token=instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            Log.w("GCMRegIntentService","token:" + token);
            //notify that ui reg success
            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);
            subscribeTopics(token);
            registrationComplete= new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token",token);

// You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();

        }catch (Exception e){
            Log.w("GCMRegIntentService","Registration Error");
            registrationComplete= new Intent(REGISTRATION_ERROR);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        //send Brodcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
   /* private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }*/
    private void sendRegistrationToServer(String token) throws IOException {
        Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl("https://lakwimana-app.appspot.com/_ah/api/");
        Registration regService = builder.build();
        regService.register(token).execute();
    }
    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
}

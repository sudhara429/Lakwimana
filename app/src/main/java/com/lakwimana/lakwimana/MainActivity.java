package com.lakwimana.lakwimana;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //for webview loading
        String url = "http://lakwimana.com/app.php" ; // Defining URL
        WebView view = (WebView) this .findViewById (R.id.webView);   // synchronization object based on the id
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);



        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.setWebViewClient(new WebViewClient());
        view.loadUrl (url);    // URL that is currently open applications terload


        mRegistrationBroadcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //check type of intent filter
            if(intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                String token =intent.getStringExtra("token");
                Toast.makeText(getApplicationContext(),"GCM token:"+token,Toast.LENGTH_LONG).show();
            }else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                Toast.makeText(getApplicationContext(),"GCM token Registration error:",Toast.LENGTH_LONG).show();
            }else{

            }
            }
        };
        //check google play status
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS!=resultCode){
            //check type of error
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                Toast.makeText(getApplicationContext(),"Google Play service not Installed",Toast.LENGTH_LONG).show();
                //so nortification
                GooglePlayServicesUtil.showErrorNotification(resultCode,getApplicationContext());
            }else{

                Toast.makeText(getApplicationContext(),"this device not supported play service",Toast.LENGTH_LONG).show();
            }
        }else{
            //start service
            Intent intent=new Intent(this,GCMRegistrationIntentService.class);
            startService(intent);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity","onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity","onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("lakwimana.com")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        View menuItemView = findViewById(R.id.action_call); // SAME ID AS MENU ID
        PopupMenu popupMenu = new PopupMenu(this, menuItemView);
        popupMenu.inflate(R.menu.menu_contact);
        // ...
        popupMenu.show();
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        WebView view = (WebView) this .findViewById (R.id.webView);
        final WebView view2=view;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            // Handle the cart action
            view.loadUrl("http://lakwimana.com/shopping_cart.php");
        } else if (id == R.id.nav_new) {

        } else if (id == R.id.nav_special) {

        } else if (id == R.id.nav_checkout) {

        } else if (id == R.id.nav_lang) {

            CharSequence colors[] = new CharSequence[] {"English", "Sinhala"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick a Language");
            builder.setItems(colors, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 1:
                            // int which = -2
                            view2.loadUrl("http://www.lakwimana.com/index.php?language=si");
                            dialog.dismiss();
                            break;
                        case 0:
                            view2.loadUrl("http://www.lakwimana.com/index.php?language=en");
                            // int which = -3
                            dialog.dismiss();
                            break;
                    }
                    // the user clicked on colors[which]
                }
            });
            builder.show();
        } else if (id == R.id.nav_currency) {

            CharSequence colors[] = new CharSequence[] {"USD", "LKR"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick a Currency");
            builder.setItems(colors, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 1:
                            // int which = -2
                            view2.loadUrl("http://www.lakwimana.com/index.php?currency=USD");
                            dialog.dismiss();
                            break;
                        case 0:
                            view2.loadUrl("http://www.lakwimana.com/index.php?currency=LKR");
                            // int which = -3
                            dialog.dismiss();
                            break;
                    }
                    // the user clicked on colors[which]
                }
            });
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

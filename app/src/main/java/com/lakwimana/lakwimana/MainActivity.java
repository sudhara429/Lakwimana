package com.lakwimana.lakwimana;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
        String url = "http://lakwimana.com/app.php"; // Defining URL
        WebView view = (WebView) this.findViewById(R.id.webView);   // synchronization object based on the id
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);


        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.setWebViewClient(new WebViewClient());
        view.loadUrl(url);    // URL that is currently open applications terload


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //check type of intent filter
                if (intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    String token = intent.getStringExtra("token");
                  //  Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM token Registration error:", Toast.LENGTH_LONG).show();
                } else {

                }
            }
        };
        //check google play status
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            //check type of error
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play service not Installed", Toast.LENGTH_LONG).show();
                //so nortification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {

                Toast.makeText(getApplicationContext(), "this device not supported play service", Toast.LENGTH_LONG).show();
            }
        } else {
            //start service
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
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
        WebView view = (WebView) this.findViewById(R.id.webView);
        final WebView view2 = view;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_call) {
            View menuItemView = findViewById(R.id.action_call); // SAME ID AS MENU ID
            PopupMenu popupMenu = new PopupMenu(this, menuItemView);
            popupMenu.inflate(R.menu.menu_contact);
            // ...
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.contact_item_skype: //Domingo

                            Intent sky = new Intent("android.intent.action.VIEW");
                            sky.setData(Uri.parse("skype:lakwimana.com"));
                            startActivity(sky);
                            break;
                        case R.id.contact_item_viber: //Segunda
                            String sphone = "+94778892006";
                            Uri uri = Uri.parse("tel:" + Uri.encode(sphone));
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
                            intent.setData(uri);
                            startActivity(intent);
                            break;
                        case R.id.contact_item_phone: //Terça
                            Intent dial = new Intent();
                            dial.setAction("android.intent.action.DIAL");
                            dial.setData(Uri.parse("tel:+94778892006"));
                            startActivity(dial);

                            break;
                        case R.id.contact_item_email: //Terça
                            // ACTION_SENDTO filters for email apps (discard bluetooth and others)
                            String uriText =
                                    "mailto:info@lakwimana.com" +
                                            "?subject=" + Uri.encode("Contact") +
                                            "&body=" + Uri.encode("");

                            Uri mail = Uri.parse(uriText);

                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                            sendIntent.setData(mail);
                            startActivity(Intent.createChooser(sendIntent, "Send email"));
                            break;

                        default:
                            break;
                    }

                    return true;
                }
            });
            popupMenu.show();
            return true;
        }
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
            view.loadUrl("http://lakwimana.com/products_new.php");
        } else if (id == R.id.nav_special) {
            view.loadUrl("http://lakwimana.com/specials.php");
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
                            view2.loadUrl("http://lakwimana.com/index.php?language=si");
                            dialog.dismiss();
                            break;
                        case 0:
                            view2.loadUrl("http://lakwimana.com/index.php?language=en");
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
            //start for categories
        switch (id) {
            case R.id.nav_cat_23: //
                view2.loadUrl("http://lakwimana.com/index.php?cPath=23");
                break;
            case R.id.nav_cat_25: //Segunda
                view2.loadUrl("http://lakwimana.com/index.php?cPath=25");
                break;
            case R.id.nav_cat_30: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=30");
                break;
            case R.id.nav_cat_32: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=32");
                break;
            case R.id.nav_cat_43: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=43");
                break;
            case R.id.nav_cat_45: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=45");
                break;
            case R.id.nav_cat_51: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=51");
                break;
            case R.id.nav_cat_67: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=67");
                break;
            case R.id.nav_cat_70: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=70");
                break;
            case R.id.nav_cat_76: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=76");
                break;
            case R.id.nav_cat_79: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=79");
                break;
            case R.id.nav_cat_91: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=91");
                break;
            case R.id.nav_cat_92: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=92");
                break;
            case R.id.nav_cat_93: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=93");
                break;
            case R.id.nav_cat_110: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=110");
                break;
            case R.id.nav_cat_115: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=115");
                break;
            case R.id.nav_cat_125: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=125");
                break;
            case R.id.nav_cat_184: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=184");
                break;
            case R.id.nav_cat_202: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=202");
                break;
            case R.id.nav_cat_256: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=256");
                break;
            case R.id.nav_cat_260: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=260");
                break;
            case R.id.nav_cat_279: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=279");
                break;
            case R.id.nav_cat_284: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=284");
                break;
            case R.id.nav_cat_286: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=286");
                break;
            case R.id.nav_cat_289: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=289");
                break;
            case R.id.nav_cat_295: //Terça
                view2.loadUrl("http://lakwimana.com/index.php?cPath=295");
                break;

            default:
                break;
        }
        //end for categories
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ///
}

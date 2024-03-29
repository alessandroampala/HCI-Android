package it.unito.ium_android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import it.unito.ium_android.requests.Requests;

// Main activity class
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private boolean loggedIn = false;
    private boolean wasConnected = false;

    /*
     * On create function
     * Manage navigation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Activity act = this;
        BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected && !wasConnected) {
                    // Request to fix navigation drawer options
                    Requests requests = new Requests(act, "getSessionLogin");
                    String data = "action=getSessionLogin";
                    String method = "POST";
                    requests.execute(data, Requests.url, method);

                    NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment);
                    int oldDestId = -1;
                    if (navController.getCurrentDestination() != null)
                         oldDestId = navController.getCurrentDestination().getId();
                    navController.popBackStack();

                    if (oldDestId != -1) {
                        if(oldDestId == R.id.nav_booking)
                        {
                            navigationView.setCheckedItem(R.id.nav_prenota);
                            navController.navigate(R.id.nav_prenota);
                        }
                        else {
                            navigationView.setCheckedItem(navigationView.getCheckedItem());
                            navController.navigate(oldDestId);
                        }
                    } else {
                        navController.navigate(R.id.nav_prenota);
                    }
                }
                wasConnected = isConnected;
            }
        };

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, intentFilter);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_login, R.id.nav_prenota, R.id.nav_prenotazioni, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        navigationView.setCheckedItem(R.id.nav_prenota);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.nav_prenota:
                    navController.popBackStack();
                    navigationView.setCheckedItem(R.id.nav_prenota);
                    navController.navigate(R.id.nav_prenota);
                    break;
                case R.id.nav_prenotazioni:
                    navController.popBackStack();
                    navigationView.setCheckedItem(R.id.nav_prenotazioni);
                    navController.navigate(R.id.nav_prenotazioni);
                    break;
                case R.id.nav_login:
                    navController.popBackStack();
                    navigationView.setCheckedItem(R.id.nav_login);
                    navController.navigate(R.id.nav_login);
                    break;
                case R.id.nav_logout:
                    Requests requests = new Requests(MainActivity.this, "logout");
                    String data = "action=logout";
                    String method = "POST";
                    requests.execute(data, Requests.url, method);
                    break;
            }

            drawer.closeDrawer(GravityCompat.START);

            return true;
        });
    }

    // Called whenever the user chooses to navigate Up within your application's activity hierarchy from the action bar
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // On resume method checks the session status
    @Override
    protected void onResume() {
        super.onResume();
        Requests requests = new Requests(this, "getSessionLogin");
        String data = "action=getSessionLogin";
        String method = "POST";
        requests.execute(data, Requests.url, method);
    }

    // Sets the loggedIn variable
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    // Gets the loggedIn variable
    public boolean isLoggedIn() {
        return loggedIn;
    }
}

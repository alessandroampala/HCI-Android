package it.unito.ium_android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import it.unito.ium_android.requests.Requests;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private boolean loggedIn = false;

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

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

            /*if (savedInstanceState != null) {
                //Restore the fragment's instance
                navigationView.setCheckedItem(savedInstanceState.getInt("checkedItem"));
                Toast.makeText(getBaseContext(), "c'è bundle" + savedInstanceState.getInt("checkedItem") + " mentre id corrente è" + navigationView.getCheckedItem(),Toast.LENGTH_SHORT).show();
            }*/

            //registerNetworkCallback();

            return true;
        });
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        Fragment f;
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            f = getSupportFragmentManager().getFragments().get(0);
        else
            f = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1);
        getSupportFragmentManager().putFragment(outState, "myFragmentName", f);

        NavigationView navigationView = findViewById(R.id.nav_view);
        //navigationView.getCheckedItem();
        outState.putInt("checkedItem", navigationView.getCheckedItem().getItemId());
        Toast.makeText(getBaseContext(), "sto salvando " + navigationView.getCheckedItem() + "con itemid" + navigationView.getCheckedItem().getItemId(),Toast.LENGTH_SHORT).show();

    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Requests requests = new Requests(this, "getSessionLogin");
        String data = "action=getSessionLogin";
        String method = "POST";
        requests.execute(data, Requests.url, method);
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
/*

    Documentation:
    https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback


    // Network Check
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                                                               @Override
                                                               public void onAvailable(Network network) {
                                                                   // Reload current fragment
                                                                   /*Fragment frg = getVisibleFragment();
                                                                   if(frg != null) {
                                                                       final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                                                       ft.detach(frg);
                                                                       ft.attach(frg);
                                                                       ft.commit();
                                                                   }*/

                                                                   /*Toast.makeText(getApplicationContext(), "onAvailable", Toast.LENGTH_SHORT).show();
                                                                   Log.e("fds", "onAvailable");
                                                               }

                                                               @Override
                                                               public void onLost(Network network) {
                                                                   // Global Static Variable
                                                                   Log.e("fds", "onLost");
                                                               }
                                                           }

        );
    }*/


    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

}
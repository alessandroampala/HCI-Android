package it.unito.ium_android;

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

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private boolean loggedIn=false;

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
            switch (id){
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
                    String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
                    String method = "POST";
                    requests.execute(data, url, method);
                    break;
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

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
        String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        String method = "POST";
        requests.execute(data, url, method);
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
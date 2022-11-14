package com.rdajila.tandroidsocketio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.rdajila.tandroidsocketio.databinding.ActivityMainBinding;
import com.rdajila.tandroidsocketio.services.DeviceSocketIO;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = DeviceSocketIO.class.getSimpleName();

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Filtro de acciones que serán alertadas
        final String ACTION_RUN_SERVICE = "com.rdajila.tandroidsocketio.services.action.RUN_SERVICE";
        final String ACTION_MEMORY_EXIT = "com.rdajila.tandroidsocketio.services.action.MEMORY_EXIT";

        IntentFilter filter = new IntentFilter(ACTION_RUN_SERVICE);
        filter.addAction(ACTION_MEMORY_EXIT);

        // Crear un nuevo ResponseReceiver
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        // Iniciar el servicio
        Intent intentDeviceSocketIO = new Intent(getApplicationContext(), DeviceSocketIO.class);
        startService(intentDeviceSocketIO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Broadcast receiver que recibe las emisiones desde los servicios
    private class ResponseReceiver extends BroadcastReceiver {
        // Filtro de acciones que serán alertadas
        final String ACTION_RUN_SERVICE = "com.rdajila.tandroidsocketio.services.action.RUN_SERVICE";
        final String ACTION_MEMORY_EXIT = "com.rdajila.tandroidsocketio.services.action.MEMORY_EXIT";
        final String COUNTER = "com.rdajila.tandroidsocketio.services.extra.COUNT";

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_RUN_SERVICE:
                    //memoryUsageText.setText(intent.getStringExtra(Constants.EXTRA_MEMORY));
                    Log.d(TAG, "Servicio iniciado escucha desde MainActivity...");
                    Log.d(TAG, "Counter: " + intent.getStringExtra(COUNTER));
                    break;
                case ACTION_MEMORY_EXIT:
                    // Guardar info en base de datos que el servicio ha sido destruido
                    Log.d(TAG, "Servicio finalizado escucha desde MainActivity...");
                    break;
            }
        }
    }
}
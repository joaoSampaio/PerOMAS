package pt.ulisboa.tecnico.peromas.peromas;


import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;


public class MainActivity extends AppCompatActivity {
	private Toolbar toolbar;
	private NavigationView navigationView;
	private DrawerLayout drawerLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initializing Toolbar and setting it as the actionbar
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//Initializing NavigationView
		navigationView = (NavigationView) findViewById(R.id.navigation_view);

		//Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

			// This method will trigger on item Click of navigation menu
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {


				//Checking if the item is in checked state or not, if not make it in checked state
				if(menuItem.isChecked()) menuItem.setChecked(false);
				else menuItem.setChecked(true);

				//Closing drawer on item click
				drawerLayout.closeDrawers();

                Fragment fragment = null;
				//Check to see which item was being clicked and perform appropriate action
                displayView(menuItem.getItemId());

                return true;
			}
		});

		// Initializing Drawer Layout and ActionBarToggle
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

			@Override
			public void onDrawerClosed(View drawerView) {
				// Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

				super.onDrawerOpened(drawerView);
			}
		};

		//Setting the actionbarToggle to drawer layout
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		//calling sync state is necessay or else your hamburger icon wont show up
		actionBarDrawerToggle.syncState();
        displayView(R.id.controls);
	}


    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
//            case R.id.home:
//                Toast.makeText(getApplicationContext(),"home Selected",Toast.LENGTH_SHORT).show();
//                fragment = new HomeFragment();
//                break;
//            case R.id.statistics:
//                Toast.makeText(getApplicationContext(),"statis Selected",Toast.LENGTH_SHORT).show();
//                fragment = new StatisticsFragment();
//                break;
            case R.id.controls:
                Toast.makeText(getApplicationContext(),"controls Selected",Toast.LENGTH_SHORT).show();
                fragment = new ControlsFragment();
                break;
            case R.id.track:
				if (!this.isBLESupported()) {
					Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
					break;
				}
                Toast.makeText(getApplicationContext(),"track Selected",Toast.LENGTH_SHORT).show();
                fragment = new TrakerBLEFragment();
                break;
            case R.id.location:
				if (!this.isBLESupported()) {
					Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
					break;
				}
                Toast.makeText(getApplicationContext(),"location Selected",Toast.LENGTH_SHORT).show();
                fragment = new BluetoothPreferencesFragment();
                break;
			case R.id.settings:
				fragment = new PerOMASPreferencesFragment();
				break;
            default:
                Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                break;
        }

        if(fragment != null){
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, fragment, position+"").commit();
        }
    }

	public boolean isBLESupported(){
		return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
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


    public void transmitBLEBeacons(Collection<Beacon> beacons){
        Fragment fragment =  getFragmentManager().findFragmentByTag(R.id.track + "");
        if(fragment != null && fragment.isVisible()){

            ((TrakerBLEFragment)fragment).onBeaconNotifier(beacons);
        }
    }

	public boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}




}

package com.cmpe295.grabz.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cmpe295.grabz.R;
import com.cmpe295.grabz.fragment.AboutUsFragment;
import com.cmpe295.grabz.fragment.HomeTabbedFragment;
import com.cmpe295.grabz.fragment.PromotionsFragment;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MainActivity extends FragmentActivity {

	public static final String LOG_PREFIX = "MainActivity";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
 
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mNavBarTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();
		mNavBarTitles = getResources().getStringArray(
				R.array.left_navigation_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mNavBarTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();

			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Configuration for image loader library
        initImageLoader(this);

		if (savedInstanceState == null) {
			selectItem(0);
		}

	}

    private void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache()).build();

        ImageLoader.getInstance().init(config);

    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

        switch (position) {

            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, HomeTabbedFragment.newInstance(), HomeTabbedFragment.CLASS_NAME).commit();
                break;

            case 1:

                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, PromotionsFragment.newInstance(), PromotionsFragment.CLASS_NAME).commit();
                break;

            case 2:

                AboutUsFragment fragment = new AboutUsFragment();
                fragment.show(getSupportFragmentManager(), "About us");
                //getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, AboutUsFragment.newInstance(), AboutUsFragment.CLASS_NAME).commit();
                break;

            default:
                Log.d("MainActivity", "Defaulting to home fragment");
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, HomeTabbedFragment.newInstance(), HomeTabbedFragment.CLASS_NAME).commit();
                break;


        }

		mDrawerList.setItemChecked(position, true);
		setTitle(mNavBarTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

	}

}

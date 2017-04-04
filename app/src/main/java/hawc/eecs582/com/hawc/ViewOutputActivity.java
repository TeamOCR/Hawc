package hawc.eecs582.com.hawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONObject;

public class ViewOutputActivity extends Activity {

    Toolbar mToolbar;
    private NavigationView mNavView;
    private DrawerLayout mDrawerLayout;
    private Menu mNavMenu;

    private View mNavHeaderLayout;

    String mTitle = "Output";

    TextView txtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);

        FrameLayout layout = (FrameLayout) findViewById(R.id.container_body);
        LayoutInflater.from(this).inflate(R.layout.activity_view_output, layout, true);




        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setActionBar(mToolbar);
        }
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);
        this.getActionBar().setTitle(mTitle);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close)
        {
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavView = (NavigationView) findViewById(R.id.nav_view);

        mNavHeaderLayout = mNavView.getHeaderView(0);

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                displayFragment(menuItem.getItemId(), menuItem.getTitle().toString());
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mNavMenu = mNavView.getMenu();

        txtView = (TextView) findViewById(R.id.output);


        Intent intent = getIntent();
        String data = intent.getStringExtra("message");
        Log.d("ASDF", data);
        String temp = "";
        try
        {
            JSONObject jObject = new JSONObject(data);
            temp = jObject.getString("output");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            int i = data.indexOf("u\'output\': u\'") + 13;
            temp = data.substring(i);
            temp = temp.substring(0,data.indexOf("'"));
        }

        temp.replace("\\n", "\n");
        temp.replace("\n", "\r\n");

        Log.d("ASDF", temp);
        txtView.setText(data);


    }

    private void displayFragment(int id, String title) {

        boolean action = false;

        switch (id) {

            case R.id.upload_picture: {

                Intent intent = new Intent(ViewOutputActivity.this, GetPictureActivity.class);
                intent.putExtra("message", "");
                startActivity(intent);



                break;
            }

            case R.id.upload_code: {


                Intent intent = new Intent(ViewOutputActivity.this, EditCodeActivity.class);
                intent.putExtra("message", "");
                startActivity(intent);

                break;
            }

            case R.id.view_output: {


                break;
            }



        }


    }


}

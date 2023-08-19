package com.sp.restuarantlist;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.database.Cursor;
import android.widget.TabHost;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;

import android.content.Context;
import androidx.cursoradapter.widget.CursorAdapter;

import java.util.ArrayList;
import java.util.List;

public class RestaurantList extends AppCompatActivity {
    private EditText restaurantName;
    private RadioGroup restaurantTypes;
    private Button buttonSave;
    private EditText restaurantAddress;
    private EditText restaurantTel;

    private Cursor model = null;
    private RestaurantAdapter adapter = null;
    private ListView list;
    private RestaurantHelper helper = null;
    private TabHost host;
    private boolean showMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        restaurantName = findViewById(R.id.restauarant_name);
        restaurantTypes = findViewById(R.id.restaurant_types);

        buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(onSave);

        restaurantAddress = findViewById(R.id.restaurant_address);
        restaurantTel = findViewById(R.id.restaurant_tel);

        helper = new RestaurantHelper(this);
        list = findViewById(R.id.restaurants);
        model = helper.getAll();
        adapter = new RestaurantAdapter(this, model, 0);
        list.setAdapter(adapter);

        host = findViewById(R.id.tabHost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("List");
        spec.setContent(R.id.restaurants_tab);
        spec.setIndicator("List");
        host.addTab(spec);

        spec = host.newTabSpec("Details");
        spec.setContent(R.id.details_tab);
        spec.setIndicator("Details");
        host.addTab(spec);
        host.setCurrentTab(1);
        list.setOnItemClickListener(onListClick);

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        invalidateOptionsMenu();
        super.onStart();
    }

    @Override
    public void invalidateOptionsMenu() {
        if (host.getCurrentTab() == 0) {
            showMenu = false;
        }
        else if (host.getCurrentTab() == 1) {
            showMenu = true;
        }
        super.invalidateOptionsMenu();;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showMenu == true) {
            getMenuInflater().inflate(R.menu.option, menu);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
                Toast.makeText(this, "Restaurant List - Version 1.0", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String nameStr = restaurantName.getText().toString();
            String addressStr = restaurantAddress.getText().toString();
            String telStr = restaurantTel.getText().toString();
            String restType= "";
            int radioID = restaurantTypes.getCheckedRadioButtonId();
            if (radioID == R.id.chinese) {
                restType = "Chinese";
            } else if (radioID == R.id.western) {
                restType = "Western";
            } else if (radioID == R.id.indian) {
                restType = "Indian";
            } else if (radioID == R.id.indonesian) {
                restType = "Indonesian";
            } else if (radioID == R.id.korean) {
                restType = "Korean";
            } else if (radioID == R.id.japanese) {
                restType = "Japanese";
            } else if (radioID == R.id.thai) {
                restType = "Thai";
            }
            String combineStr = nameStr + "\n" + addressStr + "\n" + telStr + "\n" + restType;
            Toast.makeText(getApplicationContext(), combineStr, Toast.LENGTH_LONG).show();

            helper.insert(nameStr, addressStr, telStr, restType);

            model = helper.getAll();
            adapter.swapCursor(model);
            host.setCurrentTab(0);
        }
    };

    static class RestaurantHolder {
        private TextView restName = null;
        private TextView addr = null;
        private ImageView icon = null;
        RestaurantHolder(View row) {
            restName = row.findViewById(R.id.restName);
            addr = (row.findViewById(R.id.restAddr));
            icon = row.findViewById(R.id.icon);
        }
        void populateFrom(Cursor c, RestaurantHelper helper) {
            restName.setText(helper.getRestaurantName(c));
            addr.setText(helper.getRestaurantAddress(c).toString() + " " + helper.getRestaurantTel(c).toString());
            if (helper.getRestaurantType(c).equals("Chinese")) {
                icon.setImageResource(R.drawable.ball_red);
            }
            else if (helper.getRestaurantType(c).equals("Western")) {
                icon.setImageResource(R.drawable.ball_green);
            }
            else {
                icon.setImageResource(R.drawable.ball_yellow);
            }
        }
    }

    class RestaurantAdapter extends CursorAdapter {
        RestaurantAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            RestaurantHolder holder = (RestaurantHolder) view.getTag();
            holder.populateFrom(cursor, helper);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            RestaurantHolder holder = new RestaurantHolder(row);
            row.setTag(holder);
            return (row);
        }
    }

    AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            model.moveToPosition(position);
            restaurantName.setText(helper.getRestaurantName(model));
            restaurantAddress.setText(helper.getRestaurantAddress(model));
            restaurantTel.setText(helper.getRestaurantTel(model));

            if (helper.getRestaurantType(model).equals("Chinese")) {
                restaurantTypes.check(R.id.chinese);
            }
            else if (helper.getRestaurantType(model).equals("Western")) {
                restaurantTypes.check(R.id.western);
            }
            else if (helper.getRestaurantType(model).equals("Indian")) {
                restaurantTypes.check(R.id.indian);
            }
            else if (helper.getRestaurantType(model).equals("Indonesia")) {
                restaurantTypes.check(R.id.indonesian);
            }
            else if (helper.getRestaurantType(model).equals("Korean")) {
                restaurantTypes.check(R.id.korean);
            }
            else if (helper.getRestaurantType(model).equals("Japanese")) {
                restaurantTypes.check(R.id.japanese);
            }
            else {
                restaurantTypes.check(R.id.thai);
            }
            host.setCurrentTab(1);
        }
    };
}
package com.example.test;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.test.parsers.Tables;
import com.example.test.ui.RecycleAdapter;
import com.google.android.material.navigation.NavigationView;
import com.example.test.parsers.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Menu map;
    RecycleAdapter recycleAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecycleView();
        createMenu();
    }

    private void loadItems(ArrayList<String> keys, ArrayList<String> value){
        recycleAdapter.setItemList(keys, value);
    }

    private void initRecycleView(){
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter = new RecycleAdapter();
        recyclerView.setAdapter(recycleAdapter);
    } //создание RecycleView

    private void createMenu(){ // создание меню
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        buildMenu(navigationView.getMenu());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                onSelectMenu(menuItem);
                return true;
            }
        });
        onSelectMenu(navigationView.getMenu().getItem(0));
    }

    private void onSelectMenu(MenuItem menuItem){
        recycleAdapter.clearItemList();
        recyclerView.setAdapter(recycleAdapter);
        getSupportActionBar().setTitle(menuItem.getTitle());
        drawer.closeDrawers();
        for (Menu.MenuItem item : map.getMenuItems())
            if (item.getName().contentEquals(menuItem.getTitle()) && !item.getUrl().isEmpty())
                loadInfo(item.getUrl());
    }

    private void loadInfo(String url){
        /*if ("payments".equals(url))
            fromJSON();
        else {*/
            Tables.xmlTable xmlTable = new Tables.xmlTable(url, this);
            if (xmlTable.getKeys() != null && xmlTable.getValues() != null)
                loadItems(xmlTable.getKeys(), xmlTable.getValues());
        //}
    }

    private void fromJSON(){
        try {
            ArrayList<String> keys = new ArrayList<>(), values = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(Tables.loadFromAsset(/*url*/"payments", this));
            JSONArray jsonArray = jsonObject.getJSONArray("keys");
            for (int i = 0; i < jsonArray.length(); i++)
                keys.add(jsonArray.getString(i));
            jsonArray = jsonObject.getJSONArray("values");
            for (int i = 0; i < jsonArray.length(); i++)
                values.add(jsonArray.getString(i));
            loadItems(keys, values);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки пункта меню", Toast.LENGTH_LONG).show();
        }
    }

    private void buildMenu(android.view.Menu menu){
        map = new Menu();
       if (map.parse(getResources().getXml(R.xml.menu)))
           for (Menu.MenuItem item : map.getMenuItems())
               if (item.isVisible())
                   menu.add(item.getName());
    } // генерация меню
}

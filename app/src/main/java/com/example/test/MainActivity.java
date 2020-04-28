package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.parsers.Element;
import com.example.test.parsers.Menu;
import com.example.test.parsers.XMLTable;
import com.example.test.ui.RecycleAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecycleAdapter recycleAdapter;
    RecyclerView recyclerView;
    Toolbar toolbar;
    DrawerLayout drawer;
    final Map<Menu.MenuItem, List<Element>> tableMap = new HashMap<>();
    public static final String logTag = "workError";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecycleView();
        createMenu();
    }

    private void initRecycleView() {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter = new RecycleAdapter();
        recyclerView.setAdapter(recycleAdapter);
    } //создание RecycleView

    private void createMenu() { // создание меню
        try {
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
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка построения меню", Toast.LENGTH_LONG).show();
            Log.d(logTag, e.getMessage());
        }
    }

    private void onSelectMenu(MenuItem menuItem) {
        recycleAdapter.clearItemList();
        recyclerView.setAdapter(recycleAdapter);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            getSupportActionBar().setTitle(menuItem.getTitle());
        drawer.closeDrawers();
        for (Map.Entry item : tableMap.entrySet()) {
            Menu.MenuItem itemKey = (Menu.MenuItem) item.getKey();
            if (itemKey.getName().contentEquals(menuItem.getTitle()))
                loadItems(itemKey);
        }
    }

    private void loadItems(Menu.MenuItem menuItem) {
        try {
            List<Element> elementList = tableMap.get(menuItem);
            if (elementList != null && elementList.size() == 0) {
                elementList = new XMLTable(menuItem.getUrl(), this).getElementList(true);
                tableMap.get(menuItem).addAll(elementList);
            }
            recycleAdapter.setItemList(elementList);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки таблицы", Toast.LENGTH_LONG).show();
            Log.d(logTag, e.getLocalizedMessage());
        }
    }

    private void buildMenu(android.view.Menu menu) throws Exception {
        Menu menuMap = new Menu(getResources().getXml(R.xml.menu));
        if (menuMap.isParse())
            for (Menu.MenuItem item : menuMap.getMenuItems())
                if (item.isVisible() && !item.getUrl().isEmpty()) {
                    menu.add(item.getName());
                    tableMap.put(item, new ArrayList<Element>());
                }
    } // генерация меню
}

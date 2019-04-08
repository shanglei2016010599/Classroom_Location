package com.example.classroom_location;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_classroom:
                    replaceFragment(new ClassroomFragment());
                    return true;
                case R.id.navigation_forum:
                    replaceFragment(new ForumFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if ( actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        /* 左侧菜单栏按钮点击事件 */
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch ( menuItem.getItemId() ) {
                    case R.id.nav_call:
                        Toast.makeText(MainActivity.this, "You clicked call",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_friends:
                        Toast.makeText(MainActivity.this, "You clicked friends",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_location:
                        Toast.makeText(MainActivity.this, "You clicked location",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_mail:
                        Toast.makeText(MainActivity.this, "You clicked mail",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_task:
                        Toast.makeText(MainActivity.this, "You clicked task",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        replaceFragment(new ClassroomFragment());
    }

    /* 动态添加碎片 */
    /*
        创建待添加的碎片实例
        获取FragmentManager，在获得中可以直接通过调用getSupportFragmentManager()方法得到
        开启一个事务，通过调用beginTransaction()方法开启
        向容器内添加或替换碎片，一般使用replace()方法实现，需要传入容器的id和待添加的碎片实例
        提交事务，调用commit()方法来完成。
     */
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /* toolbar按钮的点击事件 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "You clicked Backup",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked delete",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked settings",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}

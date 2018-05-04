package com.example.project.keywordnews;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

    //카테고리 별 태그버튼
    TextView politicsBt, economyBt, societyBt, cultureBt, entertainmentBt, internationalBt, sportsBt;
    String page;

    //슬라이드 메뉴 생성을 위한 변수
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("route log", "MainActivity > onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        //카테고리 별로 뉴스출력 링크를 바꿔주기 위한 SwitchHandler(onClickListener)를 태그버튼에 연결
        politicsBt = findViewById(R.id.politicsBt);
        economyBt = findViewById(R.id.economyBt);
        societyBt = findViewById(R.id.societyBt);
        cultureBt = findViewById(R.id.cultureBt);
        entertainmentBt = findViewById(R.id.entertainmentBt);
        internationalBt = findViewById(R.id.internationalBt);
        sportsBt = findViewById(R.id.sportsBt);
        SwitchHandler sh = new SwitchHandler();
        politicsBt.setOnClickListener(sh);
        economyBt.setOnClickListener(sh);
        societyBt.setOnClickListener(sh);
        cultureBt.setOnClickListener(sh);
        entertainmentBt.setOnClickListener(sh);
        internationalBt.setOnClickListener(sh);
        sportsBt.setOnClickListener(sh);
        /*
        Fragment의 Transaction과 관련하여 중요한 내용 중에 addToBackStack() 함수가 있습니다.
        바로 commit()이 실행되기 이전 상태를 "백 스택"에 추가할 수 있는 함수입니다.
        "백 스택"에 들어간 내용은 Activity에 의해 관리되며,
        "뒤로(Back)" 버튼을 누르면 이전 상태에 저장된 Fragment를 다시 가져올 수 있는 것이죠.
        addToBackStack() 함수의 사용에 대한 내용은 다음 기회에 자세히 다루겠습니다.

        출처: http://recipes4dev.tistory.com/58 [개발자를 위한 레시피]
        */

        //슬라이드 메뉴
        mTitle = mDrawerTitle = getTitle();                                           //??
        Log.d("route log", "mTitle: "+mTitle+" mDrawerTitle: "+mDrawerTitle);    //로그
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);         //strings.xml에 정의되어 있음.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);              //layout_main.xml의 <Drawerlayout>
        mDrawerList = (ListView) findViewById(R.id.left_drawer);                      //layout_main.xml의 <ListView>

        // set a custom shadow that overlays the gotokeyword content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START); //슬라이드 메뉴 그림자 설정.

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(                              //ListView에 Adapter를 set.
                this, R.layout.listview_drawer_item, mPlanetTitles));             //this, listview_drawer_itemitem.xml, mPlanetTitles
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());            //ListView에 OnItemClickListener를 set.

        // enable ActionBar app icon to behave as action to toggle nav drawer         //drawer가 토글 액션 할 수 있도록 설정.
        //Log.d("route log", "getActionBar(): " + getActionBar().toString());
        getActionBar().setDisplayHomeAsUpEnabled(true);                               //오류상태
        getActionBar().setHomeButtonEnabled(true);                                    //

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon                     //아이콘과 Drawer를 연결.
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity 얘는 무조건 this(MainActivity) */
                mDrawerLayout,                /* DrawerLayout object 위에서 id로 가져온 DrawerLayout */
                R.drawable.ic_drawer,         /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,         /* "open drawer" description for accessibility */
                R.string.drawer_close         /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {                                   //닫았을 때 > mTitle로 바꾼다.
                getActionBar().setTitle(mTitle);                                      //로그
                Log.d("route log", "mTitle: " + mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {                             //열었을 때 > mDrawerTitle로 바꾼다.
                getActionBar().setTitle(mDrawerTitle);                                //로그
                Log.d("route log", "mDrawerTitle: " + mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);                               //위에서 5줄로 설정한 mDrawerToggle을
        //<DrawerLayout>에 셋한다.

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    //정치, 경제, 사회, 문화 등 카테고리 클릭 시 호출되는 메소드.
    public class SwitchHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d("route log", "MainActivity > SwitchHandler > onClick()");

            switch (view.getId()) {
                case R.id.politicsBt:
                    Log.d("route log", "politics click");
                    page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=01";
                    Log.d("route log", "page: " + page);
                    getActionBar().setTitle("Politics");
                    break;
                case R.id.economyBt:
                    Log.d("route log", "economy click");
                    page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=02";
                    Log.d("route log", "page: " + page);
                    getActionBar().setTitle("Economy");
                    break;
                case R.id.societyBt:
                    Log.d("route log", "society click");
                    page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=03";
                    Log.d("route log", "page: " + page);
                    getActionBar().setTitle("Society");
                    break;
                case R.id.cultureBt:
                    Log.d("route log", "culture click");
                    page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=08";
                    Log.d("route log", "page: " + page);
                    getActionBar().setTitle("Culture");
                    break;
                case R.id.entertainmentBt:
                    Log.d("route log", "entertainment click");
                    page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=14";
                    Log.d("route log", "page: " + page);
                    getActionBar().setTitle("Entertainment");
                    break;
                case R.id.internationalBt:
                    Log.d("route log", "international click");
                    page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=07";
                    Log.d("route log", "page: " + page);
                    getActionBar().setTitle("International");
                    break;
                case R.id.sportsBt:
                    Log.d("route log", "sports click");
                    page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=09";
                    Log.d("route log", "page: " + page);
                    getActionBar().setTitle("Sports");
                    break;
            }
            mTitle = getTitle();
            Log.d("route log", "MainActivity > page: " + page);

            //MainFragment 생성하고 가져갈 RSS page 값 저장.
            Fragment fr = new MainFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("page", page);
            fr.setArguments(bundle);

            //RSS page 값을 가지고) MainFragment로 이동.
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.activityFrameLayout, fr); //add에서 replace로 바꿈
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("route log", "MainActivity > onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gotokeyword, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("route log", "MainActivity > onPrepareOptionsMenu");
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("route log", "MainActivity > onOptionsItemSelected");
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_websearch:  // TODO : 네이버 웹서치 잠시 지움
                Intent intent = new Intent(this, Activity_Keyword.class);
                startActivity(intent);

                // create intent to perform web search for this planet
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());

                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("route log", "MainActivity > DrawerItemClickListener > onItemClick");
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        Log.d("route log", "MainActivity > selectItem()");
        Log.d("route log", "position: " + position);

//        Fragment fr = null;
//        switch (position) {
//            //News
//            case 0:
//                break;
//            //Keyword
//            case 1:
//                break;
//        }
//        FragmentManager fm = getFragmentManager();
//        Log.d("route log", "fm: " + fm.toString());
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.fragmentView, fr);
//        ft.commit();

        // update the gotokeyword content by replacing fragments
        //Fragment fragment = new PlanetFragment();
        Fragment fragment = null;
        switch (position) {
            case 0:
                //News 클릭 시
                fragment = new MainFragment();
                break;
            case 1:
                //Keyword 클릭 시
                break;
        }
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activityFrameLayout, fragment).commit();
        //원래 파일에선 <FrameLayout>에 content_frame이라는 id를 썼지만 여기선 원래 내 것인 fragmentView로 주었다.

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        // Empty constructor required for fragment subclasses
        public PlanetFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()), "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);

            return rootView;
        }
    }
}

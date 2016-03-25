package com.ph1ash.sentinel;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import com.ph1ash.sentinel.Decal;

public class SentinelActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static String TAG = "SentinelActivity";

    private ArrayList<Decal> decals = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentinel);

        final EditText inputText = (EditText) findViewById(R.id.tagNumber);
        final ImageView mImageView = (ImageView) findViewById(R.id.imageConfirmation);

        xmlParse();

        tagCheck(inputText, mImageView);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void xmlParse()
    {
        try {
            XmlResourceParser parser = getResources().getXml(R.xml.decals);

            int eventType = parser.getEventType();
            Decal currentDecal = null;

            while (eventType != XmlResourceParser.END_DOCUMENT){

                String name;

                //Log.d(TAG, "Event type: " + eventType);

                switch (eventType){
                    case XmlResourceParser.START_DOCUMENT:
                        decals = new ArrayList();
                        break;
                    case XmlResourceParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("decal")){
                            currentDecal = new Decal();
                        } else if (currentDecal != null){
                            if (name.equals("decalnumber")){
                                currentDecal.decalNumber = parser.nextText();
                            } else if (name.equals("firstname")){
                                currentDecal.firstName = parser.nextText();
                            } else if (name.equals("lastname")){
                                currentDecal.lastName = parser.nextText();
                            } else if (name.equals("remotenumber")){
                                currentDecal.remoteNumber = parser.nextText();
                            }
                        }
                        break;
                    case XmlResourceParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("decal") && currentDecal != null){
                            decals.add(currentDecal);
                        }
                }
                eventType = parser.next();
            }
            Log.d(TAG, "End document");
        }
        catch(XmlPullParserException | IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public void tagCheck(final EditText text, final ImageView mView)
    {
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    // Toast.makeText(SentinelActivity.this, inputText.getText().toString(), Toast.LENGTH_SHORT).show();
                    String incomingTag = text.getText().toString();
                    // Log.d(TAG, String.valueOf(incomingTag.length()));
                    if((incomingTag.length() == 4) && (incomingTag.startsWith("0"))) {

                        // Implement search function here

                        // Show items that are missing and found

                        boolean matchFound = false;
                        String fName = "";
                        String lName = "";
                        String rNum = "";

                        for(Decal mDecal : decals)
                        {
                            if(mDecal.decalNumber.equals(incomingTag))
                            {
                                matchFound = true;
                                fName = mDecal.firstName;
                                lName = mDecal.lastName;
                                rNum = mDecal.remoteNumber;
                            }
                        }
                        if(matchFound)
                        {
                            //Log.d(TAG, "Found tag: "+incomingTag);
                            Toast.makeText(SentinelActivity.this,
                                    "Match found: " + fName + " " + lName + "; " + rNum,
                                    Toast.LENGTH_LONG).show();
                            mView.setImageResource(R.drawable.green_check);
                        }
                        else
                        {
                            //Log.d(TAG, "Did not find tag: "+incomingTag);
                            mView.setImageResource(R.drawable.red_x);
                        }
                        return true;
                    }
                    else
                    {
                        mView.setImageResource(R.drawable.question_mark);
                        Toast.makeText(SentinelActivity.this, "Tag must be four digits, starting with 0", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.sentinel, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sentinel, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((SentinelActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}

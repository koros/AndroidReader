package com.korosmatick.androidreader;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;

    private static List<String> pageContents = new ArrayList<String>();
    LinearLayout mPageIndicator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //===========================================================
        // Calculate ActionBar height
        /*TypedValue tv = new TypedValue();
        int actionBarHeight = 60;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }*/
        //===========================================================

        ViewGroup textviewPage = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment, (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content) , false);
        TextView contentTextView = (TextView) textviewPage.findViewById(R.id.text);

        String contentString = getString(R.string.lorem);
        float horizontalMargin = getResources().getDimension(R.dimen.activity_horizontal_margin) * 2;
        float verticalMargin = getResources().getDimension(R.dimen.activity_vertical_margin) * 2;

        // obtaining screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        int screenWidth = (int) (display.getWidth() - horizontalMargin);
        int screenHeight = display.getHeight();

        TextPaint paint = contentTextView.getPaint();

        //===========================================================
        //Working Out How Many Lines Can Be Entered In The Screen
        Paint.FontMetrics fm = paint.getFontMetrics();
        float fullHeight = fm.top - fm.bottom;
        fullHeight = Math.abs(fullHeight);
        //===========================================================

        int numChars = 0;
        int lineCount = 0;
        int maxLineCount = (int) ((screenHeight - (/*actionBarHeight + */ verticalMargin) )/fullHeight);
        //contentTextView.setLines(maxLineCount);

        // contentString is the whole string of the book
        int totalPages = 0;
        while (contentString != null && contentString.length() != 0 )
        {
            while ((lineCount < maxLineCount) && (numChars < contentString.length())) {
                numChars = numChars + paint.breakText(contentString.substring(numChars), true, screenWidth, null);
                lineCount ++;
            }

            // retrieve the String to be displayed in the current textbox
            String toBeDisplayed = contentString.substring(0, numChars);
            int nextIndex = numChars;
            char nextChar = nextIndex < contentString.length() ? contentString.charAt(nextIndex) : ' ';
            if (!Character.isWhitespace(nextChar)) {
                toBeDisplayed = toBeDisplayed.substring(0, toBeDisplayed.lastIndexOf(" "));
            }
            numChars = toBeDisplayed.length();
            contentString = contentString.substring(numChars);
            pageContents.add(toBeDisplayed.trim());

            numChars = 0;
            lineCount = 0;

            totalPages ++;
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), totalPages);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                showPageIndicator(position);
            }
        });

        createPageIndicator();
        //============================================================================================

    }

    @SuppressWarnings("deprecation")
    private void createPageIndicator() {
        mPageIndicator = (LinearLayout) findViewById(R.id.pageIndicator);
        for (int i = 0; i < pageContents.size(); i++) {
            View view = new View(this);
            ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            view.setLayoutParams(params );
            view.setBackgroundDrawable(getResources().getDrawable(i == 0 ? R.drawable.current_page_indicator : R.drawable.indicator_background));
            view.setTag(i);
            mPageIndicator.addView(view);
        }
    }

    @SuppressWarnings("deprecation")
    protected void showPageIndicator(int position) {
        try {
            mPageIndicator = (LinearLayout) findViewById(R.id.pageIndicator);
            for (int i = 0; i < pageContents.size(); i++) {
                View view = mPageIndicator.findViewWithTag(i);
                view.setBackgroundDrawable(getResources().getDrawable(i == position ? R.drawable.current_page_indicator : R.drawable.indicator_background));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public String getContents(int pageNumber){
        return pageContents.get(pageNumber);
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
}

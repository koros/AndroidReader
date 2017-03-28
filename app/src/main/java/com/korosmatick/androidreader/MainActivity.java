package com.korosmatick.androidreader;

import android.graphics.Paint;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;

    private Map<String, String> mPages = new HashMap<String, String>();
    private LinearLayout mPageIndicator = null;
    private ProgressBar mProgressBar = null;

    private String mContentString = "";

    private Display mDisplay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        ViewGroup textviewPage = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment, (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content) , false);
        TextView contentTextView = (TextView) textviewPage.findViewById(R.id.mText);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mContentString = getString(R.string.lorem);
        // obtaining screen dimensions
        mDisplay = getWindowManager().getDefaultDisplay();

        ViewAndPaint  vp = new ViewAndPaint(contentTextView.getPaint(), textviewPage, getScreenWidth(), getMaxLineCount(contentTextView), mContentString);

        PagerTask pt = new PagerTask(this);
        pt.execute(vp);
    }

    private int getScreenWidth(){
        float horizontalMargin = getResources().getDimension(R.dimen.activity_horizontal_margin) * 2;
        int screenWidth = (int) (mDisplay.getWidth() - horizontalMargin);
        return screenWidth;
    }

    private int getMaxLineCount(TextView view){
        float verticalMargin = getResources().getDimension(R.dimen.activity_vertical_margin) * 2;
        int screenHeight = mDisplay.getHeight();
        TextPaint paint = view.getPaint();

        //Working Out How Many Lines Can Be Entered In The Screen
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = fm.top - fm.bottom;
        textHeight = Math.abs(textHeight);

        int maxLineCount = (int) ((screenHeight - verticalMargin ) / textHeight);

        // add extra spaces at the bottom, remove 2 lines
        maxLineCount -= 2;

        return maxLineCount;
    }

    private void initViewPager(){
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), 1);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                showPageIndicator(position);
            }
        });
    }

    public void onPageProcessedUpdate(ProgressTracker progress){
        mPages = progress.pages;
        // init the pager if necessary
        if (mPagerAdapter == null){
            initViewPager();
            hideProgress();
        }else {
            ((MyPagerAdapter)mPagerAdapter).incrementPageCount();
        }
        addPageIndicator(progress.totalPages);
    }

    private void hideProgress(){
        mProgressBar.setVisibility(View.GONE);
    }

    private void addPageIndicator(int pageNumber) {
        mPageIndicator = (LinearLayout) findViewById(R.id.pageIndicator);
        View view = new View(this);
        ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        view.setLayoutParams(params );
        view.setBackgroundDrawable(getResources().getDrawable(pageNumber == 0 ? R.drawable.current_page_indicator : R.drawable.indicator_background));
        view.setTag(pageNumber);
        mPageIndicator.addView(view);
    }

    protected void showPageIndicator(int position) {
        try {
            mPageIndicator = (LinearLayout) findViewById(R.id.pageIndicator);
            View selectedIndexIndicator = mPageIndicator.getChildAt(position);
            selectedIndexIndicator.setBackgroundDrawable(getResources().getDrawable(R.drawable.current_page_indicator));
            // dicolorize the neighbours
            if (position > 0){
                View leftView = mPageIndicator.getChildAt(position -1);
                leftView.setBackgroundDrawable(getResources().getDrawable(R.drawable.indicator_background));
            }
            if (position < mPages.size()){
                View rightView = mPageIndicator.getChildAt(position +1);
                rightView.setBackgroundDrawable(getResources().getDrawable(R.drawable.indicator_background));
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public String getContents(int pageNumber){
        String page = String.valueOf(pageNumber);
        String textBoundaries = mPages.get(page);
        if (textBoundaries != null) {
            String[] bounds = textBoundaries.split(",");
            int startIndex = Integer.valueOf(bounds[0]);
            int endIndex = Integer.valueOf(bounds[1]);
            return mContentString.substring(startIndex, endIndex);
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class ViewAndPaint {

        public ViewGroup textviewPage;
        public TextPaint paint;
        public int screenWidth;
        public int maxLineCount;
        public String contentString;

        public ViewAndPaint(TextPaint paint, ViewGroup textviewPage, int screenWidth, int maxLineCount, String contentString){
            this.paint = paint;
            this.textviewPage = textviewPage;
            this.maxLineCount = maxLineCount;
            this.contentString = contentString;
            this.screenWidth = screenWidth;
        }
    }

    static class ProgressTracker {

        public int totalPages;
        public Map<String, String> pages = new HashMap<String, String>();

        public void addPage(int page, int startIndex, int endIndex) {
            String thePage = String.valueOf(page);
            String indexMarker = String.valueOf(startIndex) + "," + String.valueOf(endIndex);
            pages.put(thePage, indexMarker);
        }
    }
}

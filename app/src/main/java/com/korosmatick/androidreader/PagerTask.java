package com.korosmatick.androidreader;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextPaint;



/**
 * Created by gkoros on 12/03/2017.
 */

public class PagerTask extends AsyncTask<MainActivity.ViewAndPaint, MainActivity.ProgressTracker, Void> {

    private Context mContext;

    public PagerTask(Context context){
        this.mContext = context;
    }

    protected Void doInBackground(MainActivity.ViewAndPaint... vps) {

        MainActivity.ViewAndPaint vp = vps[0];
        MainActivity.ProgressTracker progress = new MainActivity.ProgressTracker();
        TextPaint paint = vp.paint;
        int numChars = 0;
        int lineCount = 0;
        int maxLineCount = vp.maxLineCount;
        int totalCharactersProcessedSoFar = 0;

        // contentString is the whole string of the book
        int totalPages = 0;
        while (vp.contentString != null && vp.contentString.length() != 0 )
        {
            while ((lineCount < maxLineCount) && (numChars < vp.contentString.length())) {
                numChars = numChars + paint.breakText(vp.contentString.substring(numChars), true, vp.screenWidth, null);
                lineCount ++;
            }

            // retrieve the String to be displayed in the current textview
            String stringToBeDisplayed = vp.contentString.substring(0, numChars);
            int nextIndex = numChars;
            char nextChar = nextIndex < vp.contentString.length() ? vp.contentString.charAt(nextIndex) : ' ';
            if (!Character.isWhitespace(nextChar)) {
                stringToBeDisplayed = stringToBeDisplayed.substring(0, stringToBeDisplayed.lastIndexOf(" "));
            }
            numChars = stringToBeDisplayed.length();
            vp.contentString = vp.contentString.substring(numChars);

            // publish progress
            progress.totalPages = totalPages;
            progress.addPage(totalPages, totalCharactersProcessedSoFar, totalCharactersProcessedSoFar + numChars);
            publishProgress(progress);

            totalCharactersProcessedSoFar += numChars;

            // reset per page items
            numChars = 0;
            lineCount = 0;

            // increment  page counter
            totalPages ++;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(MainActivity.ProgressTracker... values) {
        ((MainActivity)mContext).onPageProcessedUpdate(values[0]);
    }


}

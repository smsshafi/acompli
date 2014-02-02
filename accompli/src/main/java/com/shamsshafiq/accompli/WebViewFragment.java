package com.shamsshafiq.accompli;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.shamsshafiq.accompli.db.AcompliSQLiteOpenHelper;
import com.shamsshafiq.accompli.events.DatabaseModifiedEvent;
import com.shamsshafiq.accompli.events.SubmitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

public class WebViewFragment extends Fragment {

    private WebView webView;
    private AcompliSQLiteOpenHelper mSqLiteOpenHelper;

    private static List<String> inputFieldNames = Arrays.asList("item0", "item7", "item2", "item5", "GARBAGE");

    private List<Pair<String, String>> mAddedEntries = new ArrayList<Pair<String, String>>();
    private int mInsertLoopCounter = 0;
    private int mInsertCounter = 0;

    public WebViewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        webView = (WebView) view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(getActivity().getApplicationContext()), "parseForm");
        webView.loadUrl("https://dl.dropboxusercontent.com/u/233148577/test.html");

    }

    class MyJavaScriptInterface {

        private Context mContext;

        public MyJavaScriptInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void captureKeyValuePair(String name, String value) {
            mInsertLoopCounter++;
            if (name != null && !name.isEmpty() && value != null && !value.isEmpty()) {
                if (mSqLiteOpenHelper == null) {
                    mSqLiteOpenHelper = new AcompliSQLiteOpenHelper(getActivity().getApplicationContext());
                }

                Pair entry = new Pair<String, String>(name, value);
                mSqLiteOpenHelper.addEntry(entry);
                mAddedEntries.add(entry);
                mInsertCounter++;
            }

            if (mInsertLoopCounter >= inputFieldNames.size()) {
                EventBus.getDefault().post(new DatabaseModifiedEvent());
                showConfirmationToast();

                mInsertLoopCounter = 0;
                mInsertCounter = 0;
            }
        }

        private void showConfirmationToast() {
            View layout = View.inflate(mContext, R.layout.added_toast, null);

            String addedMessage = generateConfirmationMessage(mAddedEntries);
            ((TextView)layout.findViewById(R.id.text_added_title)).setText(mContext.getString(R.string.string_added_title, mInsertCounter));
            ((TextView)layout.findViewById(R.id.text_added_values)).setText(addedMessage);

            Toast toast = new Toast(mContext);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

            mAddedEntries.clear();
        }

        private String generateConfirmationMessage(List<Pair<String, String>> entries) {
            StringBuilder sb = new StringBuilder();
            if (entries.size() > 0) {
            for (Pair entry : entries) {
                sb.append(mContext.getResources().getString(R.string.string_added_message, entry.first, entry.second));
                sb.append("\n");
            }
            sb.deleteCharAt(sb.length()-1);
            } else {
                sb.append(mContext.getResources().getString(R.string.string_empty_input_message));
            }
            return sb.toString();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(SubmitEvent event) {
        for (String inputFieldName : inputFieldNames) {
            webView.loadUrl(String.format("javascript:window.parseForm.captureKeyValuePair('%s', document.getElementsByName('%s')[0].value);", inputFieldName, inputFieldName));
        }
    }
}

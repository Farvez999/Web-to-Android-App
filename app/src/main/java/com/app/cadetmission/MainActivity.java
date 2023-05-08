package com.app.cadetmission;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    String webAddress = "https://cadetmission.com/";
    WebView webView;
    FrameLayout frameLayout;
    ProgressBar progressBar;
    private Object fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView=(WebView)findViewById(R.id.webView);
        frameLayout=(FrameLayout)findViewById(R.id.frameLayout);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        webView.setWebViewClient(new HelpClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                setTitle("Loading...");
                if (newProgress == 100){
                    frameLayout.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                }
                super.onProgressChanged(view, newProgress);
            }

        });
        //register web view //downloading image
        registerForContextMenu(webView);

        //InternetConnetion
        if (haveNetWorkConnection()) {
            webView.loadUrl(webAddress);
        }else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        progressBar.setProgress(0);



        webView.getSettings().setJavaScriptEnabled(true);   //enable javascript

    }

    private class HelpClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            frameLayout.setVisibility(View.VISIBLE);
            return true;
        }
    }

    //Internet Connection
    private boolean haveNetWorkConnection(){
        boolean haveConnectionWiFi =false;
        boolean haveConnectionMobile=false;

        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos=cm.getAllNetworkInfo();

        for (NetworkInfo ni : networkInfos){
            if (ni.getTypeName().equalsIgnoreCase("WiFi"));
            if (ni.isConnected())
                haveConnectionWiFi =true;

            if (ni.getTypeName().equalsIgnoreCase("Mobile Data"));
            if (ni.isConnected())
                haveConnectionMobile =true;
        }

        return haveConnectionWiFi || haveConnectionMobile;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode==KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //image download


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        final WebView.HitTestResult hitTestResult=webView.getHitTestResult();

        if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
              hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE){

            //set handle titel
            menu.setHeaderTitle("Download");
            //set handle icon
            menu.setHeaderIcon(R.drawable.ic_download);
            menu.add(0,1,0,"Save - Download Image")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            String downloadImageUrl=hitTestResult.getExtra();
                            if (URLUtil.isValidUrl(downloadImageUrl)){

                                //handle downloading
                                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(downloadImageUrl));
                                request.allowScanningByMediaScanner();
                                //show notification when download compelete
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);

                                DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);

                                //show toast that downloading
                                Toast.makeText(MainActivity.this, "downloading...", Toast.LENGTH_SHORT).show();

                            }else {
                                //if there is any error
                                Toast.makeText(MainActivity.this, "Sorry...Somethings went wrong, cheek internet connection", Toast.LENGTH_SHORT).show();
                            }

                            return false;
                        }
                    });
        }

    }
}

package com.example.microcontroladores;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jjoe64.graphview.series.PointsGraphSeries;

/**
 * A simple {@link Fragment} subclass.
 */
public class Radar1Fragment extends Fragment {
    static TextView textotemporal;
    static Canvas viewCanvas;
    static Paint paint;
    static View vi;
    static WebView myWebView;


    public Radar1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View  view =inflater.inflate(R.layout.fragment_radar1, container, false);
        textotemporal = view.findViewById(R.id.textoTemporal);
        myWebView = view.findViewById(R.id.webView);

        myWebView.setWebViewClient(new wvClient());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.clearCache(true);
        myWebView.getSettings().setAllowContentAccess(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.loadUrl("file:///android_asset/www/radar1.html");
        return view;
    }

    public static void agregarPunto(String grados, String distancia){
        textotemporal.setText(textotemporal.getText()+"grados :"+grados+", distancia : "+distancia+"\n");
        //Se convierte de polar a rectangular
        //Se obtiene la distancia
        int x = (int) (146-((Math.sin(Double.parseDouble(grados)*Math.PI/180)*Double.parseDouble(distancia))*3.75)*(-1));
        int y = (int) (146-((Math.cos(Double.parseDouble(grados)*Math.PI/180)*Double.parseDouble(distancia))*3.75));
        Log.d("DEBUG_POINTS_X",String.valueOf(x));
        Log.d("DEBUG_POINTS_Y",String.valueOf(y));
        myWebView.loadUrl("javascript:agregarPunto("+String.valueOf(x)+","+String.valueOf(y)+",'"+String.valueOf(Integer.parseInt(grados))+"','"+String.valueOf(Integer.parseInt(distancia))+"')");
    }
    public static void limpiarRadar(){
        myWebView.loadUrl("javascript:limpiarRadar()");
    }

    final class wvClient extends WebViewClient {
        public void onPageFinished(WebView view, String url) {
            // when our web page is loaded, let's call a function that is contained within the page
            // this is functionally equivalent to placing an onload attribute in the <body> tag
            // whenever the loadUrl method is used, we are essentially "injecting" code into the page when it is prefixed with "javascript:"
            //myWebView.loadUrl("javascript:(function agregarPunto() {var ul = document.getElementById(\"radar\");var li = document.createElement(\"li\");li.setAttribute(\"class\", \"point\");li.style.left = \"55%\";li.style.top = \"25%\";ul.appendChild(li);})()");
        }
    }

    final class DataHandler{
        @JavascriptInterface
        public String javascriptExample(){
            return "var ul = document.getElementById(\"radar\");\n" +
                    "\t\tvar li = document.createElement(\"li\");\n" +
                    "\t\tli.setAttribute(\"class\", \"point\"); // added line\n" +
                    "\t\tli.style.left = \"55%\";\n" +
                    "\t\tli.style.top = \"25%\";\n" +
                    "\t\tul.appendChild(li);";
        }
    }
}

package android.webkit;
import android.view.View;
import android.view.OnKeyListener;
public class WebView extends View {
    public WebView(android.content.Context context) { super(context); }
    public void setWebViewClient(WebViewClient client) {}
    public void setWebChromeClient(WebChromeClient client) {}
    public void loadUrl(String url) {}
    public void destroy() {}
    public boolean setOnKeyListener(OnKeyListener listener) { return false; }
    public WebSettings getSettings() { return null; }
}

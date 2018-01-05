package com.greetty.printerdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.greetty.printerdemo.utils.BitImageEncoder;
import com.posin.device.Printer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    /**
     * JS页面通过该别名获取类中的方法
     */
    private static final String PRINTER_CLASS_METHOD_ALIAS = "android";

    private static final byte[] CMD_LINE_FEED = {0x0a};
    private static final byte[] CMD_INIT = {0x1B, 0x40};
    private static final byte[] CMD_ALIGN_CENTER = {0x1B, 0x61, 1};
    private static final byte[] CMD_ALIGN_LEFT = {0x1B, 0x61, 0};
    private static final byte[] CMD_ALIGN_RIGHT = {0x1B, 0x61, 2};
    private static final byte[] CMD_FEED_AND_CUT = {0x0A, 0x0A, 0x0A, 0x0A, 0x1D, 0x56, 0x01};
    private static final int QRCODE_SIZE = 350;
    private static final int BARCODE_WIDTH = 500;
    private static final int BARCODE_HEIGHT = 80;

    @BindView(R.id.wv_main_printer)
    WebView wvPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initWebView();
    }

    /**
     * 初始化WebView页面
     */
    @SuppressLint("JavascriptInterface")
    private void initWebView() {

        wvPrinter.getSettings().setJavaScriptEnabled(true);
        wvPrinter.addJavascriptInterface(new PrinterJsInteration(), PRINTER_CLASS_METHOD_ALIAS);

        wvPrinter.setWebViewClient(new WebViewClient());
        wvPrinter.setWebChromeClient(new WebChromeClient());

        wvPrinter.loadUrl("file:///android_asset/Printer.html");
    }

    /**
     * JS访问类
     */
    private class PrinterJsInteration {

        @JavascriptInterface
        public boolean PrinterSample() {
            Printer prt = null;
            try {

                final byte[] data = genSamplePage2();
                prt = Printer.newInstance();

                final OutputStream os = prt.getOutputStream();

                if (!prt.ready())  //打印机未准备就绪
                    return false;

                os.write(data);

                if (!prt.ready()) //打印机未打印机完成
                    return false;
                else
                    return true; //打印完成

            } catch (Throwable throwable) {
                throwable.printStackTrace();
                Log.e(TAG, "error: " + throwable.getMessage());
            }
            return false;
        }
    }

    /**
     * 生成SamplePage
     *
     * @return
     * @throws UnsupportedEncodingException exception
     * @throws IOException                  io
     * @throws WriterException              exception
     */
    private byte[] genSamplePage2() throws UnsupportedEncodingException,
            IOException, WriterException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        os.write(CMD_INIT);
        os.write(CMD_ALIGN_LEFT);
        os.write(getSamplePage().getBytes("GBK"));


        os.write(CMD_ALIGN_CENTER);
        os.write(BitImageEncoder.genBarcodePrinterCommand("6955885000208",
                BARCODE_WIDTH, BARCODE_HEIGHT));
        os.write("6955885000208\n\n".getBytes());

        os.write(BitImageEncoder.genQrcodePrinterCommand(ErrorCorrectionLevel.H,
                "QRCode. 二维码测试.", QRCODE_SIZE, QRCODE_SIZE));

        final byte[] CMD_CUT = {'\n', '\n', '\n', '\n', 0x1D, 0x56, 0};
        os.write(CMD_CUT);

        return os.toByteArray();
    }


    /**
     * 生成测试页面
     */
    public static String getSamplePage() {
        StringBuilder sb = new StringBuilder();

        Date d = new Date();
        String date = d.toLocaleString();

        // 页面内容
        sb.append('\n');
        sb.append(date + "\n");
        sb.append("Waiter : Alex.\n");
        sb.append("Table  : T01,   Order#: 10132\n");
        sb.append("Cust.Cat.: InHouse Clients   \n");
        sb.append("-----------------------------\n");
        sb.append("2 x  Duck Pancake        1.2 \n");
        sb.append("1 x  Fried Rice          3.0 \n");
        sb.append("1 x  Banana Fritter      1.8 \n");
        sb.append("1 x  Pineapple Fritter   1.8 \n");
        sb.append("1 x  Curry Sauce         1.0 \n");
        sb.append("2 x  Chilli Sauce        1.0 \n");
        sb.append("1 x  炒面 (大)            2.9 \n");
        sb.append("2 x  可乐(瓶装)            1.3 \n");
        sb.append("-----------------------------\n");
        sb.append("Total Discount MarkUp Balance\n");
        sb.append("16.20 0.00      0.00    16.20\n");
        sb.append("\n");

        return sb.toString();
    }

}

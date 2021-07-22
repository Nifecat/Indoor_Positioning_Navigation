package edu.xmu.inroomlocation.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class NetworkUtil {

    public static List<ScanResult> getWifiList(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();

        return scanResults;
    }


    private static final String TAG = "NetworkUtil";


    public static void sendAHelloMessageToServer(Handler mainHandler, String serverIp) {

        new Thread(() -> {

            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(serverIp, 6409), 10000);

                OutputStream outputStream = socket.getOutputStream();

                byte[] bytes = "{\"message\": \"Hello\"}".getBytes();
                outputStream.write(bytes);
                outputStream.flush();
                socket.shutdownOutput();

                outputStream.flush();
                InputStream inputStream = socket.getInputStream();
                int read = inputStream.read(new byte[1024]);
                Log.d("TAG", "sendAMessageToServer: " + read + "read");

                Message message = new Message();
                message.what = 1;
                mainHandler.sendMessage(message);

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();

                Message message = new Message();
                message.what = 999;
                mainHandler.sendMessage(message);
            }


        }).start();

    }


    public static void sendAMessageToServer(Handler mainHandler, String serverIp, String msg) {

        new Thread(() -> {

            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(serverIp, 6409), 10000);

                OutputStream outputStream = socket.getOutputStream();

                byte[] bytes = msg.getBytes();
                outputStream.write(bytes);
                outputStream.flush();
                socket.shutdownOutput();
//                outputStream.flush();

                byte[] in = new byte[128];
                InputStream inputStream = socket.getInputStream();
                int read = inputStream.read(in);
                Log.d("TAG", "sendAMessageToServer: " + read + "read");

                String inStr = new String(in, 0, read);

                Message message = new Message();
                message.what = 1;
                message.arg1 = Integer.valueOf(inStr).intValue();
                Log.d(TAG, "sendAMessageToServer: " + message.arg1);
                mainHandler.sendMessage(message);

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();

                Message message = new Message();
                message.what = 999;
                mainHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();

                Message message = new Message();
                message.what = 999;
                mainHandler.sendMessage(message);
            }


        }).start();

    }

    public static void sendPicToServer(Handler mainHandler, String serverIp, String picFilename) {

        byte[] bytes = StorageUtils.readPicToBytes(picFilename);


        String b64edImage = Base64.encodeToString(bytes, Base64.DEFAULT);


        sendAMessageToServer(mainHandler, serverIp, b64edImage);
    }


    public static void sendPicAndWifiStrengthToServer(Handler mainHandler, String serverIp, String picFilename, Map<String, Integer> wifiStrength) {

        byte[] bytes = StorageUtils.readPicToBytes(picFilename);


        String b64edImage = Base64.encodeToString(bytes, Base64.DEFAULT);

        JSONObject jo = new JSONObject();
        try {
            jo.put("image", b64edImage);

            JSONArray jsonWifiStrength = new JSONArray();
            for (Map.Entry<String, Integer> entry : wifiStrength.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                JSONObject w = new JSONObject();
                w.put("name", key);
                w.put("rssi", value);
                jsonWifiStrength.put(w);
            }

            jo.put("wifi", jsonWifiStrength);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        sendAMessageToServer(mainHandler, serverIp, jo.toString());
    }
}
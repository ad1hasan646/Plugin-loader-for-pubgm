package com.skullshooter.ssloader.app.api;

import static com.skullshooter.ssloader.app.api.API.PG64_VERSION;
import static com.skullshooter.ssloader.app.api.API.PG_VERSION;
import static com.skullshooter.ssloader.app.api.API.PLUG64_DIR;
import static com.skullshooter.ssloader.app.api.API.PLUG64_NAME;
import static com.skullshooter.ssloader.app.api.API.PLUGIN;
import static com.skullshooter.ssloader.app.api.API.PLUGIN64;
import static com.skullshooter.ssloader.app.api.API.PLUGIN_VERSION;
import static com.skullshooter.ssloader.app.api.API.PLUG_DIR;
import static com.skullshooter.ssloader.app.api.API.PLUG_NAME;
import static com.skullshooter.ssloader.app.api.API.SAFE;
import static com.skullshooter.ssloader.app.api.API.STATUS64;
import static com.skullshooter.ssloader.app.api.API.UNSAFE;
import static com.skullshooter.ssloader.app.api.API._ROOT_URL;
import static com.skullshooter.ssloader.app.api.API.plugin32DownloadLink;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skullshooter.ssloader.app.loader.Utilities;
import com.skullshooter.ssloader.app.preferences.Preferences;
import com.skullshooter.ssloader.app.screens.MainElements;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class FetchAPI {
    public static String appData;
    public static String safetyStatus;
    public static String pluginData;
    public static String pluginData64;

    public static String APIpluginUrl = "";
    public static String APIplugin64Url = "";
    public static String APIpluginVersion = "";
    public static String APIplugin64Version = "";

    public static void fetchData(Context context, TextView connectionErrorTxt, RelativeLayout refresh_layout,
                                 LinearLayout updates_info, LinearLayout hackSelections,
                                 RelativeLayout error_layout, TextView announcements,
                                 TextView safety32bit, TextView safety64bit, RelativeLayout download_launch_pg_32bit,
                                 TextView installation_status_32bit, TextView installation_status_64bit, TextView updateLoaderInfo_text, RelativeLayout download_launch_pg_64bit) {
        new Thread(()->{
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(API._LOADER_DATA).openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Connection", "close");
                connection.connect();
                int responseCode = connection.getResponseCode();

                StringBuffer response = new StringBuffer();
                if (responseCode != 200) {
                    throw new IOException("Internal server error!");
                }

                BufferedReader inUrl = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()
                ));

                String inputLine;
                while ((inputLine = inUrl.readLine()) != null) {
                    response.append(inputLine);
                }

                inUrl.close();
                appData = response.toString();

                try {
                    parseAppData(appData, context, connectionErrorTxt, refresh_layout, updates_info, hackSelections, error_layout, announcements, safety32bit, safety64bit,
                            download_launch_pg_32bit, installation_status_32bit, installation_status_64bit, updateLoaderInfo_text, download_launch_pg_64bit);
                } catch (Exception e) {
                    ((Activity)context).runOnUiThread(()->{
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                //TODO: Fetch Plugin32 Data
                HttpURLConnection connectionPlugin = (HttpURLConnection) new URL(API._PLUGIN32_DATA).openConnection();
                connectionPlugin.setRequestMethod("GET");
                connectionPlugin.setInstanceFollowRedirects(true);
                connectionPlugin.setConnectTimeout(10000);
                connectionPlugin.setReadTimeout(10000);
                connectionPlugin.setRequestProperty("Connection", "close");
                connectionPlugin.connect();

                int responseCodePlugin = connectionPlugin.getResponseCode();
                StringBuffer responsePlugin = new StringBuffer();
                if (responseCodePlugin != 200) {
                    throw new IOException("Internal server error!");
                }

                BufferedReader inUrlPlugin = new BufferedReader(new InputStreamReader(
                        connectionPlugin.getInputStream()
                ));

                String inputLinePlugin;
                while ((inputLinePlugin = inUrlPlugin.readLine()) != null) {
                    responsePlugin.append(inputLinePlugin);
                }

                inUrlPlugin.close();
                pluginData = responsePlugin.toString();

                try {
                    parsePluginData(pluginData, context, connectionErrorTxt, refresh_layout, updates_info, hackSelections, error_layout, announcements, safety32bit, safety64bit,
                            download_launch_pg_32bit, installation_status_32bit, installation_status_64bit, updateLoaderInfo_text, download_launch_pg_64bit);
                } catch (Exception e) {
                    ((Activity)context).runOnUiThread(()->{
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .show();
                    });
                }




                //TODO: Fetch Plugin64 Data
                connection = (HttpURLConnection) new URL(API._PLUGIN64_DATA).openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Connection", "close");
                connection.connect();

                int responseCodePlugin64 = connection.getResponseCode();
                StringBuffer responsePlugin64 = new StringBuffer();
                if (responseCodePlugin64 != 200) {
                    throw new IOException("Internal server error!");
                }

                BufferedReader inUrlPlugin64 = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()
                ));

                String inputLinePlugin64;
                while ((inputLinePlugin64 = inUrlPlugin64.readLine()) != null) {
                    responsePlugin64.append(inputLinePlugin64);
                }

                inUrlPlugin64.close();
                pluginData64 = responsePlugin64.toString();

                try {
                    parsePlugin64Data(pluginData64, context, connectionErrorTxt, refresh_layout, updates_info, hackSelections, error_layout, announcements, safety32bit, safety64bit,
                            download_launch_pg_32bit, installation_status_32bit, installation_status_64bit, updateLoaderInfo_text, download_launch_pg_64bit);
                } catch (Exception e) {
                    ((Activity)context).runOnUiThread(()->{
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .show();
                    });
                }


                connection = (HttpURLConnection) new URL(API._SAFETY_STATUS).openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Connection", "close");
                connection.connect();

                int responseCodeStatus = connection.getResponseCode();
                StringBuffer responseSafety = new StringBuffer();
                if (responseCodeStatus != 200) {
                    throw new IOException("Internal server error!");
                }

                BufferedReader inUrlSafety = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()
                ));

                String inputLineSafety;
                while ((inputLineSafety = inUrlSafety.readLine()) != null) {
                    responseSafety.append(inputLineSafety);
                }

                inUrlSafety.close();
                safetyStatus = responseSafety.toString();

                try {
                    parseSafetyData(safetyStatus, context, connectionErrorTxt, refresh_layout, updates_info, hackSelections, error_layout, announcements, safety32bit, safety64bit,
                            download_launch_pg_32bit, installation_status_32bit, installation_status_64bit, updateLoaderInfo_text, download_launch_pg_64bit);
                } catch (Exception e) {
                    ((Activity)context).runOnUiThread(()->{
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .show();
                    });
                }

            } catch (Exception e) {
                ((Activity)context).runOnUiThread(()->{
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .show();
                });
            }
        }).start();
    }

    private static void parsePlugin64Data(
            String pluginData64,
            Context context,
            TextView connectionErrorTxt,
            RelativeLayout refresh_layout,
            LinearLayout updates_info,
            LinearLayout hackSelections,
            RelativeLayout error_layout,
            TextView announcements,
            TextView safety32bit,
            TextView safety64bit,
            RelativeLayout download_launch_pg_32bit,
            TextView installation_status_32bit,
            TextView installation_status_64bit,
            TextView updateLoaderInfo_text,
            RelativeLayout download_launch_pg_64bit) throws JSONException
    {
        String _plugin64Name, _plugin64Version, _plugin64Url;
        JSONObject appDObject = new JSONObject(pluginData64);
        JSONArray appDArray = appDObject.getJSONArray("data");
        for (int i = 0; i < appDObject.length(); i++) {
            JSONObject object = appDArray.getJSONObject(i);
            Log.d("PLUGIN64_data=>>>> ", object.toString());

            _plugin64Name = object.getString("plugin64Name");
            _plugin64Version = object.getString("plugin64Version");
            _plugin64Url = object.getString("plugin64Url");

            APIplugin64Version = Objects.requireNonNull(_plugin64Version);
            APIplugin64Url = Objects.requireNonNull(_ROOT_URL + "uploads/plugins/plugin64/archiver/" + _plugin64Url);

            String currentPlugin64Version = Preferences.getInstance().read(PG64_VERSION);
            File pg64 = new File(Preferences.getInstance().read(PLUG64_DIR), PLUG64_NAME);

            if (pg64.exists()) {
                if (currentPlugin64Version.equals(_plugin64Version)) {
                    ((Activity)context).runOnUiThread(()->{
                        installation_status_64bit.setText(Preferences.getInstance().read(PG64_VERSION));
                    });
                } else {
                    installation_status_64bit.setText("NEW-UPDATE");
                    installation_status_64bit.setTextSize(24);
                    installation_status_64bit.setTypeface(null, Typeface.NORMAL);
                    safety64bit.setVisibility(View.INVISIBLE);
                    installation_status_64bit.setTextColor(Color.parseColor("#00FFE6"));
                    download_launch_pg_64bit.setOnClickListener(v -> {
                        downloadPlugin64WithDialogVersionFirstTime(
                                context,
                                refresh_layout,
                                updates_info,
                                hackSelections,
                                error_layout,
                                updateLoaderInfo_text,
                                connectionErrorTxt,
                                PLUGIN64,
                                PLUG64_DIR,
                                PLUG64_NAME,
                                "Updating PUBG Mobile [64-bit]",
                                PG64_VERSION,
                                APIplugin64Version);
                    });
                }
            } else {
                ((Activity)context).runOnUiThread(()->{
                    installation_status_64bit.setText("Not installed");
                    download_launch_pg_64bit.setOnClickListener(v -> {
                        downloadPlugin64WithDialogVersionFirstTime(
                                context,
                                refresh_layout,
                                updates_info,
                                hackSelections,
                                error_layout,
                                updateLoaderInfo_text,
                                connectionErrorTxt,
                                PLUGIN64,
                                PLUG64_DIR,
                                PLUG64_NAME,
                                "Downloading PUBG Mobile [64-bit]",
                                PG64_VERSION,
                                APIplugin64Version);
                    });
                });
            }
        }

    }

    private static void parsePluginData(
            String pluginData, Context context,
            TextView connectionErrorTxt,
            RelativeLayout refresh_layout,
            LinearLayout updates_info,
            LinearLayout hackSelections,
            RelativeLayout error_layout,
            TextView announcements,
            TextView safety32bit,
            TextView safety64bit,
            RelativeLayout download_launch_pg_32bit,
            TextView installation_status_32bit,
            TextView installation_status_64bit,
            TextView updateLoaderInfo_text,
            RelativeLayout download_launch_pg_64bit) throws JSONException
    {
        String _pluginName, _pluginVersion, _pluginUrl;
        JSONObject appDObject = new JSONObject(pluginData);
        JSONArray appDArray = appDObject.getJSONArray("data");
        for (int i = 0; i < appDObject.length(); i++) {
            JSONObject object = appDArray.getJSONObject(i);
            Log.d("PLUGIN_data=>>>> ", object.toString());

            _pluginName = object.getString("pluginName");
            _pluginVersion = object.getString("pluginVersion");
            _pluginUrl = object.getString("pluginUrl");

            APIpluginVersion = Objects.requireNonNull(_pluginVersion);
            APIpluginUrl = Objects.requireNonNull(_ROOT_URL + "uploads/plugins/plugin32/archiver/" + _pluginUrl);

            String currentPluginVersion = Preferences.getInstance().read(PG_VERSION);
            File pg = new File(Preferences.getInstance().read(PLUG_DIR), PLUG_NAME);

            if (pg.exists()) {
                if (currentPluginVersion.equals(_pluginVersion)) {
                    ((Activity)context).runOnUiThread(()->{
                        installation_status_32bit.setText(Preferences.getInstance().read(PG_VERSION));
                    });
                } else {
                    installation_status_32bit.setText("NEW-UPDATE");
                    installation_status_32bit.setTextSize(24);
                    installation_status_32bit.setTypeface(null, Typeface.NORMAL);
                    safety32bit.setVisibility(View.INVISIBLE);
                    installation_status_32bit.setTextColor(Color.parseColor("#00FFE6"));
                    download_launch_pg_32bit.setOnClickListener(v -> {
                        downloadPlugin32WithDialogVersionFirstTime(
                                context,
                                refresh_layout,
                                updates_info,
                                hackSelections,
                                error_layout,
                                updateLoaderInfo_text,
                                connectionErrorTxt,
                                PLUGIN,
                                PLUG_DIR,
                                PLUG_NAME,
                                "Updating PUBG Mobile [32-bit]",
                                PG_VERSION,
                                APIpluginVersion);
                    });
                }
            } else {
                ((Activity)context).runOnUiThread(()->{
                    installation_status_32bit.setText("Not installed");
                    download_launch_pg_32bit.setOnClickListener(v -> {
                        downloadPlugin32WithDialogVersionFirstTime(
                                context,
                                refresh_layout,
                                updates_info,
                                hackSelections,
                                error_layout,
                                updateLoaderInfo_text,
                                connectionErrorTxt,
                                PLUGIN,
                                PLUG_DIR,
                                PLUG_NAME,
                                "Downloading PUBG Mobile [32-bit]",
                                PG_VERSION,
                                APIpluginVersion);
                    });
                });
            }
        }
    }

    private static void parseSafetyData(
            String safetyStatus,
            Context context,
            TextView connectionErrorTxt,
            RelativeLayout refresh_layout,
            LinearLayout updates_info,
            LinearLayout hackSelections,
            RelativeLayout error_layout,
            TextView announcements,
            TextView safety32bit,
            TextView safety64bit,
            RelativeLayout download_launch_pg_32bit,
            TextView installation_status_32bit,
            TextView installation_status_64bit,
            TextView updateLoaderInfo_text,
            RelativeLayout download_launch_pg_64bit) throws JSONException
    {
        int _32bitStatus, _64bitStatus;
        JSONObject appDObject = new JSONObject(safetyStatus);
        JSONArray appDArray = appDObject.getJSONArray("data");
        for (int i = 0; i < appDObject.length(); i++) {
            JSONObject object = appDArray.getJSONObject(i);
            Log.d("SAFETY_data=>>>> ", object.toString());

            _32bitStatus = object.getInt("is32Bit");
            _64bitStatus = object.getInt("is64Bit");

            if (_32bitStatus == 0) {
                ((Activity)context).runOnUiThread(()-> {
                    safety32bit.setText(SAFE);
                    safety32bit.setTextColor(Color.parseColor("#FF00FF00"));
                    safety32bit.setTypeface(null, Typeface.BOLD);
                });
            } else {
                ((Activity)context).runOnUiThread(()->{
                    safety32bit.setText(UNSAFE);
                    safety32bit.setTextColor(Color.parseColor("#FFFF0000"));
                    safety32bit.setTypeface(null, Typeface.BOLD);
                });
            }

            if (_64bitStatus == 0) {
                ((Activity)context).runOnUiThread(()->{
                    safety64bit.setText(SAFE);
                    safety64bit.setTextColor(Color.parseColor("#FF00FF00"));
                    safety64bit.setTypeface(null, Typeface.BOLD);
                });
            } else {
                ((Activity)context).runOnUiThread(()->{
                    safety64bit.setText(UNSAFE);
                    safety64bit.setTextColor(Color.parseColor("#FFFF0000"));
                    safety64bit.setTypeface(null, Typeface.BOLD);
                });
            }

            ((Activity)context).runOnUiThread(()->{
                refresh_layout.setVisibility(View.GONE);
                updates_info.setVisibility(View.VISIBLE);
                hackSelections.setVisibility(View.VISIBLE);
                error_layout.setVisibility(View.GONE);
            });
        }
    }

    //Parse App data
    public static void parseAppData(String data, Context context, TextView connectionErrorTxt, RelativeLayout refresh_layout,
                                    LinearLayout updates_info, LinearLayout hackSelections,
                                    RelativeLayout error_layout, TextView announcements,
                                    TextView safety32bit, TextView safety64bit, RelativeLayout download_launch_pg_32bit,
                                    TextView installation_status_32bit, TextView installation_status_64bit, TextView updateLoaderInfo_text,
                                    RelativeLayout download_launch_pg_64bit) throws JSONException {
        JSONObject appDObject = new JSONObject(data);
        JSONArray appDArray = appDObject.getJSONArray("data");
        String loader_version, announcement, pluginVersion, plugin64Version, pluginUrl, plugin64Url;

        for (int i = 0; i < appDObject.length(); i++) {
            JSONObject object = appDArray.getJSONObject(i);
            Log.d("APP_DATA=>>>> ", object.toString());

            loader_version = object.getString("loaderVersion");

            ((Activity)context).runOnUiThread(()->{
                refresh_layout.setOnClickListener(v -> {
                    fetchData(context,
                            connectionErrorTxt,
                            refresh_layout,
                            updates_info,
                            hackSelections,
                            error_layout,
                            announcements,
                            safety32bit,
                            safety64bit,
                            download_launch_pg_32bit,
                            installation_status_32bit,
                            installation_status_64bit,
                            updateLoaderInfo_text,
                            download_launch_pg_64bit);
                });
            });
        }
    }

    //Download Plugin
    public static void downloadPlugin(Context context, RelativeLayout refresh_layout, LinearLayout updates_info, LinearLayout hackSelections, RelativeLayout error_layout,
                                      TextView updateLoaderInfo_text, TextView connectionErrorTxt, String pgLink, String PLUG, String pluginDir, String pluginName, String downloadInfo,
                                      String pluginVersion, String fromApiVersion) {
        new Thread(()-> {
            try {
                HttpURLConnection connection;
                connection = (HttpURLConnection) new URL(pgLink).openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Connection", "close");
                connection.connect();
                int responseCode = connection.getResponseCode();

                if (responseCode != 200) {
                    throw new IOException("Request Code Not 200");
                }

                File loaderDir = new File(context.getFilesDir(), PLUG);
                if(loaderDir.exists()){
                    Utilities.deleteRecursive(loaderDir);
                }
                loaderDir.mkdir();

                if (!Preferences.getInstance().contains(pluginDir)) {
                    Preferences.getInstance().write(pluginDir, loaderDir.getAbsolutePath());
                }

                File loaderPath = new File(loaderDir, pluginName);

                InputStream bStream = connection.getInputStream();
                FileOutputStream fileOut = new FileOutputStream(loaderPath);
                int fileLength = connection.getContentLength();

                byte data[] = new byte[4096];
                long total = 0;
                int count;

                while ((count = bStream.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        long finalTotal1 = total;
                        ((Activity)context).runOnUiThread(() -> {
                            String txt = (downloadInfo + " ("+ (int) (finalTotal1 * 100 / fileLength)+"%"+")");
                            ((Activity)context).runOnUiThread(() -> {
                                refresh_layout.setVisibility(View.VISIBLE);
                                updates_info.setVisibility(View.GONE);
                                hackSelections.setVisibility(View.GONE);
                                updateLoaderInfo_text.setText(txt);
                            });
                        });
                    }
                    fileOut.write(data, 0, count);
                }
                bStream.close();
                fileOut.flush();
                fileOut.close();
                connection.disconnect();

                ((Activity)context).runOnUiThread(()->{
                    Preferences.getInstance().write(pluginVersion, fromApiVersion);
                    error_layout.setVisibility(View.GONE);
                    Toast.makeText(context, "Please Re-start...", Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context)
                                    .setTitle("Restart required")
                                    .setMessage("Loader need restart to load the file you downloaded. Please Re-start the loader")
                                    .setPositiveButton("Re-start", (d,w)->{
                                        Utilities.triggerRebirth(context);
                                    })
                                    .show();
                        }
                    }, 3000);

                });
            } catch (IOException e) {
                ((Activity)context).runOnUiThread(()->{
                    refresh_layout.setVisibility(View.GONE);
                    updates_info.setVisibility(View.GONE);
                    hackSelections.setVisibility(View.GONE);
                    error_layout.setVisibility(View.VISIBLE);
                    connectionErrorTxt.setText(e.getMessage());
                });
            }
        }).start();

    }

    public static void downloadPlugin32WithDialogVersionFirstTime(Context context, RelativeLayout refresh_layout, LinearLayout updates_info, LinearLayout hackSelections, RelativeLayout error_layout,
                                                                  TextView updateLoaderInfo_text, TextView connectionErrorTxt, String PLUG, String pluginDir, String pluginName, String downloadInfo,
                                                                  String pluginVersion, String fromApiVersion) {
        ((Activity)context).runOnUiThread(()->{
            new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Material_Dialog_Alert))
                    .setTitle("PUBG Mobile [32-bit]")
                    .setMessage(String.format("Version: %s", APIpluginVersion))
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isInternetAvailable(context))
                            {
                                /** TODO implement pluginDownload here*/
                                FetchAPI.downloadPlugin(
                                        context,
                                        refresh_layout,
                                        updates_info,
                                        hackSelections,
                                        error_layout,
                                        updateLoaderInfo_text,
                                        connectionErrorTxt,
                                        APIpluginUrl ,
                                        PLUG,
                                        pluginDir,
                                        pluginName,
                                        downloadInfo,
                                        pluginVersion,
                                        fromApiVersion);
                            } else {
                                updates_info.setVisibility(View.GONE);
                                hackSelections.setVisibility(View.GONE);
                                error_layout.setVisibility(View.VISIBLE);
                                connectionErrorTxt.setText("Unable to download plugin: No address associated with hostname.");
                            }
                        }
                    }).show();
        });
    }

    public static void downloadPlugin64WithDialogVersionFirstTime(Context context, RelativeLayout refresh_layout, LinearLayout updates_info, LinearLayout hackSelections, RelativeLayout error_layout,
                                                            TextView updateLoaderInfo_text, TextView connectionErrorTxt, String PLUG, String pluginDir, String pluginName, String downloadInfo,
                                                            String pluginVersion, String fromApiVersion) {
        ((Activity)context).runOnUiThread(()->{
            new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Material_Dialog_Alert))
                    .setTitle("PUBG Mobile [64-bit]")
                    .setMessage(String.format("Version: %s", APIplugin64Version))
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setPositiveButton("Download and launch", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isInternetAvailable(context))
                            {
                                /** TODO implement pluginDownload here*/
                                downloadPlugin(
                                        context,
                                        refresh_layout,
                                        updates_info,
                                        hackSelections,
                                        error_layout,
                                        updateLoaderInfo_text,
                                        connectionErrorTxt,
                                        APIplugin64Url,
                                        PLUG,
                                        pluginDir,
                                        pluginName,
                                        downloadInfo,
                                        pluginVersion,
                                        fromApiVersion);
                            } else {
                                updates_info.setVisibility(View.GONE);
                                hackSelections.setVisibility(View.GONE);
                                error_layout.setVisibility(View.VISIBLE);
                                connectionErrorTxt.setText("Unable to download plugin: No address associated with hostname.");
                            }
                        }
                    }).show();
        });
    }

    //InternetConnectio
    @SuppressLint("MissingPermission")
    public static boolean isInternetAvailable(Context context) {
        @SuppressLint({"NewApi", "LocalSuppress"}) ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }//InternetConnectio

}

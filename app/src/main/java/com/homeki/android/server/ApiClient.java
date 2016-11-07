package com.homeki.android.server;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.homeki.android.misc.Settings;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    private static final String TAG = ApiClient.class.getSimpleName();
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final Gson mGson = new Gson();
    private OkHttpClient mOkHttpClient;

    private Context context;

    public ApiClient(Context context) {
        this.context = context;
        mOkHttpClient = new OkHttpClient();
    }

    private String getServerUrl() {
        return "http://" + Settings.getServerUrl(context) + ":" + Settings.getServerPort(context) + "/api";
    }

    public List<JsonDevice> getDevices() {
        Log.i(TAG, "Fetching all devices.");
        return get(getServerUrl() + "/devices", new TypeToken<List<JsonDevice>>() {
        }.getType());
    }

    public List<JsonActionGroup> getActionGroups() {
        Log.i(TAG, "Fetching all action groups.");
        return get(getServerUrl() + "/actiongroups", new TypeToken<List<JsonActionGroup>>() {
        }.getType());
    }

    public void triggerActionGroup(int id) {
        Log.i(TAG, "Triggering action group " + id + ".");
        get(getServerUrl() + "/actiongroups/" + id + "/trigger", null);
    }

    public LatLng getServerLocation() {
        Log.i(TAG, "Fetching server location.");
        JsonServer server = get(getServerUrl() + "/server", JsonServer.class);
        return new LatLng(server.locationLatitude, server.locationLongitude);
    }

    public void registerClient(String id) {
        Log.i(TAG, "Registering client " + id + ".");
        post(getServerUrl() + "/clients", new JsonClient(id));
    }

    public void unregisterClient(String id) {
        Log.i(TAG, "Unregistering client " + id + ".");
        delete(getServerUrl() + "/clients/" + id);
    }

    public void setChannelValueForDevice(int deviceId, int channelId, int value) {
        Log.i(TAG, "Setting channel " + channelId + " for device " + deviceId + " to " + value + ".");
        post(getServerUrl() + "/devices/" + deviceId + "/channels/" + channelId, new JsonChannelValue(value));
    }

    private void post(String url, Object data) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_JSON, mGson.toJson(data)))
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            int statusCode = response.code();
            switch (statusCode) {
                case 200:
                case 201:
                    return;
                default:
                    throw new RuntimeException("Unhandled response code " + statusCode + ".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T get(String url, Type type) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            int statusCode = response.code();
            switch (statusCode) {
                case 200:
                    if (type == null) {
                        return null;
                    } else {
                        return mGson.fromJson(response.body().string(), type);
                    }
                default:
                    throw new RuntimeException("Unhandled response code " + statusCode + ".");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void delete(String url) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            int statusCode = response.code();
            switch (statusCode) {
                case 200:
                case 201:
                    return;
                default:
                    throw new RuntimeException("Unhandled response code " + statusCode + ".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class JsonServer {
        public double locationLatitude;
        public double locationLongitude;
        public String name;
    }

    public static enum DeviceType {
        @SerializedName("switch")
        SWITCH,

        @SerializedName("switchmeter")
        SWITCH_METER,

        @SerializedName("dimmer")
        DIMMER,

        @SerializedName("thermometer")
        THERMOMETER
    }

    public static class JsonDevice {
        public DeviceType type;
        public int deviceId;
        public String name;
        public String description;
        public String added;
        public boolean active;
        public List<JsonDeviceChannel> channels;
    }

    public static class JsonDeviceChannel {
        public int id;
        public String name;
        public Number lastValue;
    }

    public static class JsonActionGroup {
        public int actionGroupId;
        public String name;
    }

    private static class JsonClient {
        public String id;

        public JsonClient(String id) {
            this.id = id;
        }
    }

    private static class JsonChannelValue {
        public int value;

        public JsonChannelValue(int value) {
            this.value = value;
        }
    }
}

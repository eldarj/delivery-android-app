package com.eldar.fit.seminarski.helper;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.eldar.fit.seminarski.R;

import java.lang.reflect.Type;

public class MyApiRequest {
    // Content type
    public static String CONTENT_TYPE_JSON = "application/json";

    // Endpoints
    public static String ENDPOINT_LOCATIONS = "lokacije";
    public static String ENDPOINT_RESTORANI = "restorani";
    public static String ENDPOINT_RESTORANI_LIKE = "restorani/%1$d/like";
    public static String ENDPOINT_RESTORANI_UNLIKE = "restorani/%1$d/unlike";
    public static String ENDPOINT_RESTORANI_HRANA = "restorani/%1$d/hrana";
    public static String ENDPOINT_RESTORANI_KOMENTARI = "restorani/%1$d/komentari/subscribe";

    public static String ENDPOINT_NARUDZBE = "narudzbe";
    public static String ENDPOINT_NARUDZBE_CREATE = "narudzbe/create";
    public static String ENDPOINT_NARUDZBE_STATUS = "narudzbe/%1$d/status";
    public static String ENDPOINT_NARUDZBE_DELETE = "narudzbe/%1$d/delete";


    public static String ENDPOINT_USER_LOGIN_CHECK_AUTH = "auth";
    public static String ENDPOINT_USER_REGISTER_AUTH = "auth/register";
    public static String ENDPOINT_USER_UPDATE_AUTH = "auth/update";
    public static String ENDPOINT_USER_DELETE_AUTH = "auth/delete";
    public static String ENDPOINT_USER_UPLOAD_IMAGE = "auth/user/image";

    public static <T> AsyncTask request(final Activity activity, final String endpoint, final MyUrlConnection.HttpMethod httpMethod, final Object postObject, final MyAbstractRunnable<T> callback) {
        try {
            AsyncTask<Void, Void, MyApiResult> task = new AsyncTask<Void, Void, MyApiResult>() {

                @Override
                protected MyApiResult doInBackground(Void... voids) {
                    Log.i("Test", "doInBackground");
                    String jsonPostObject = postObject == null ? null : MyGson.build().toJson(postObject);

                    if (endpoint == "test") {
                        return MyUrlConnection.request("https://jsonplaceholder.typicode.com/todos/1", httpMethod, jsonPostObject, CONTENT_TYPE_JSON);
                    }

                    MyApiResult result = MyUrlConnection.request(MyConfig.apiBase + "/" + endpoint, httpMethod, jsonPostObject, CONTENT_TYPE_JSON);
                    return result;
                }

                @Override
                protected void onPostExecute(MyApiResult result) {
                    Log.i("Test", "postExecute");
                    View parentLayout = activity.findViewById(R.id.content) != null ? activity.findViewById(R.id.content) : activity.findViewById(R.id.fragmentContainer);
                    Snackbar snackbar = null;
                    if (result.isException) {

                        if (result.resultCode == 0) {
                            Log.i("Test", "asyncApiRequest - error server communication.");
                            snackbar = Snackbar.make(parentLayout, "Greška u komunikaciji sa serverom. ", Snackbar.LENGTH_LONG);
                            callback.error(result.resultCode, "Greška u komunikaciji sa serverom.");
                        } else {
                            Log.i("Test", "asyncApiRequest - error:" + result.errorMessage + " statuscode:" + result.resultCode);
                            snackbar = Snackbar.make(parentLayout, "Greška " + result.resultCode + ": " + result.errorMessage, Snackbar.LENGTH_LONG);
                            callback.error(result.resultCode, "Greška na serveru, provjerite podatke.");
                        }
                    } else {
                        Type genericType = callback.getGenericType();

                        try {
                            Log.i("Test", "asyncResultsJSON: " + result.value);
                            T mappedObj = MyGson.build().fromJson(result.value, genericType);
                            callback.run(mappedObj);
                        } catch (Exception e) {
                            Log.i("Test", "asyncApiRequest - get - Exception:" + e.getMessage());
                            snackbar = Snackbar.make(parentLayout, "Greška pri prikazivanju podataka. ", Snackbar.LENGTH_LONG);
                            callback.error(0, "Greška pri prikazivanju podataka.");
                        }
                    }
                    if (snackbar != null) {
                        snackbar.setAction("Učitaj ponovo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MyApiRequest.get(activity, endpoint, callback);
                            }
                        });
                        snackbar.show();
                    }
                }
            };

            task.execute();
            return task;
        } catch (Exception e) {
            Log.i("Test", "Async AsyncTask request exception-error..");
            e.printStackTrace();
        }
        return null;
    }

    public static <T> AsyncTask get(final Activity activity, final String endpoint, final MyAbstractRunnable<T> callback){
        return request(activity, endpoint, MyUrlConnection.HttpMethod.GET, null, callback);
    }

    public static <T> AsyncTask post(final Activity activity, final String endpoint, Object postObject, final MyAbstractRunnable<T> callback){
        return request(activity, endpoint, MyUrlConnection.HttpMethod.POST, postObject, callback);
    }
}

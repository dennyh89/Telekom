/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.modules.login;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.events.PDEEvent;
import de.telekom.pde.codelibrary.ui.events.PDEEventSource;
import de.telekom.pde.codelibrary.ui.events.PDEIEventSource;
import de.telekom.pde.codelibrary.ui.helpers.PDEDictionary;
import de.telekom.pde.codelibrary.ui.helpers.PDEString;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
// CAUTION
// This module is not approved by GIS! So you may only use this code in an app, if _you_ are getting it approved.
// So use it at you own risk only. We cannot guarantee anything about this code.
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------


//----------------------------------------------------------------------------------------------------------------------
//  PDEOneIDMModule
//----------------------------------------------------------------------------------------------------------------------


/**
 * @brief Handles OAuth authentication with OneIDM Server.
 *
 * The class implements two methods to request an access token from the server.
 * It is used by PDEOneIDMLoginScreenActivity - here the requestAccessToken with username and password is used.
 * In order to get a new access token if you already have a refresh token you use the requestAccessToken function which
 * take the refresh token as parameter.
 */
@SuppressWarnings("unused")
public class PDEOneIDMModule implements PDEIEventSource {

    // Events
    public final static String PDEOneIDMModuleEventToken = "PDEOneIDMModule.token";
    public final static String PDEOneIDMModuleEventError = "PDEOneIDMModule.error.returned";
    public final static String PDEOneIDMModuleEventErrorOAuth = "PDEOneIDMModule.error.OAuth";
    public final static String PDEOneIDMModuleEventTimeout = "PDEOneIDMModule.timeout";
    public final static String PDEOneIDMModuleEventCanceledByUser = "PDEOneIDMModule.cancelByUser";


    public final static String ResultDictionaryStatusCode = "statusCode";
    public final static String ResultDictionaryError = "error";
    public final static String ResultDictionaryErrorMessage = "error-message";

    /// @cond INTERNAL_DEFINE
    private final static String LOG_TAG = PDEOneIDMModule.class.getName();
    /// @endcond

    // live server
    private final static String DEFAULT_AUTH_URL = "https://logint3.idm.toon.sul.t-online.de/oauth2/tokens";
    // test server
    //private final static String DEFAULT_AUTH_URL = "https://accounts.login00.idm.ver.sul.t-online.de/oauth2/tokens";

    protected String mURL;
    protected String mClientID;
    //protected String mClientSecret;
    private boolean mRequestActive;
    private AsyncTask<String, Void, String> mTask;

    protected PDEEventSource mEventSource;

//----------------------------------------------------------------------------------------------------------------------
//  DownloadTokenTask innerclass
//----------------------------------------------------------------------------------------------------------------------

    /**
     * @brief Worker Task to download the token asyncronically.
     */
    private class DownloadTokenTask extends AsyncTask<String, Void, String> {
        final static String ACCESS_TOKEN_BY_USERNAME_PASSWORD = "AccessTokenByUsernamePassword";
        final static String ACCESS_TOKEN_BY_REFRESH_TOKEN = "AccessTokenByRefreshToken";


        /**
         * @brief AsyncTask worker function.
         *
         * downloadUsernamePasswordToken and downloadByRefreshToken do the real work here, depending on the request.
         *
         * @param urls all parameters
         */
        @Override
        protected String doInBackground(String... urls) {
            //Log.d(LOG_TAG, "DownloadTokenTask::doInBackground...");

            // params comes from the execute() call: params[0] is the type.
            if (PDEString.isEqual(urls[0], ACCESS_TOKEN_BY_USERNAME_PASSWORD)) {
                return downloadUsernamePasswordToken(urls[1], urls[2], urls[3], urls[4], urls[5], urls[6]);
            } else if (PDEString.isEqual(urls[0], ACCESS_TOKEN_BY_REFRESH_TOKEN)) {
                return downloadByRefreshToken(urls[1], urls[2], urls[3], urls[4], urls[5]);
            } else {
                return "not a know request type";
            }
        }

        /**
         * @brief onPostExecute function of AsyncTask.
         *
         * Is called when the doInBackground is finished. Resets the task
         */

        @Override
        protected void onPostExecute(String result) {
            //Log.d(LOG_TAG, "DownloadTokenTask::onPostExecute...");
            mRequestActive = false;
            mTask = null;
        }


        /**
         * @brief onCancelled function of the AsyncTask.
         *
         * Is called when the task is canceled. Resets the task and sends an cancel event to the listener.
         *
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTask = null;
            mRequestActive = false;

            PDEEvent event = new PDEEvent();
            event.setType(PDEOneIDMModuleEventCanceledByUser);
            event.setSender(PDEOneIDMModule.this);
            mEventSource.sendEvent(event);

            //Log.d(LOG_TAG, "DownloadTokenTask::onCancelled...");
        }

        //----- Token Download -------------------------------------------------------------------------------------------------


        /**
         * @brief Download the token specified by querystring from the oauthUrl.
         *
         * @param oauthUrl URL to query
         * @param queryString parameters to send
         */
        protected String downloadToken(String oauthUrl, String queryString) {
            HttpsURLConnection urlConnection = null;
            URL url = null;
            String contentAsString = "";
            PDEDictionary result = new PDEDictionary();
            int responseCode = -1;
            PDEEvent event = new PDEEvent();
            InputStream is;
            OutputStream outputStream;
            BufferedWriter writer;
            try {
                // create a URL object from string.
                url = new URL(oauthUrl);

                // otherwise each second urlConnection fails
                System.setProperty("http.keepAlive", "false");

                // set the connection attributes
                // we assume that we only support https connections (save connection)
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setFixedLengthStreamingMode(queryString.getBytes().length);

                // write parameter into the connection
                outputStream = urlConnection.getOutputStream();
                writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(queryString);
                writer.close();
                outputStream.close();

                // check if execution is canceled
                if (this.isCancelled()) {
                    System.setProperty("http.keepAlive", "true");
                    return null;
                }

                // start the connection
                urlConnection.connect();

                // check if execution is canceled
                if (this.isCancelled()) {
                    urlConnection.disconnect();
                    System.setProperty("http.keepAlive", "true");
                    return null;
                }

                responseCode = urlConnection.getResponseCode();

                result.put(ResultDictionaryStatusCode, responseCode);

                // get either the regular or the error return - we evaluate the content
                try {
                    // we read from input stream
                    is = urlConnection.getInputStream();
                } catch (IOException e) {
                    // otherwise we have to read from error stream
                    is = urlConnection.getErrorStream();
                }

                // check if execution is canceled
                if (this.isCancelled()) {
                    urlConnection.disconnect();
                    System.setProperty("http.keepAlive", "true");
                    return null;
                }

                // Convert the InputStream into a string, parse JSON and copy content to result dictionary
                contentAsString = readIt(is);

                // put the result into a dictionary which will be sent to the listeners
                fillJSONTokenIntoDict(new JSONTokener(contentAsString), result);

            } catch (MalformedURLException e) {
                event.setType(PDEOneIDMModuleEventError);
                result.put(ResultDictionaryError, "MalformedURLException");
                result.put(ResultDictionaryErrorMessage, e.getMessage());
            } catch (IOException e) {
                event.setType(PDEOneIDMModuleEventError);
                result.put(ResultDictionaryError, "IOException");
                result.put(ResultDictionaryErrorMessage, e.getMessage());
            } catch (JSONException e) {
                event.setType(PDEOneIDMModuleEventError);
                result.put(ResultDictionaryError, "JSONException");
                result.put(ResultDictionaryErrorMessage, e.getMessage());
            } finally {
                if (urlConnection != null)  {
                    urlConnection.disconnect();
                }
            }

            // set it back to the default value
            System.setProperty("http.keepAlive", "true");

            // check if execution is canceled
            if (this.isCancelled()) {
                urlConnection.disconnect();
                return null;
            }

            if (TextUtils.isEmpty(event.getType())) {
                if (responseCode == 200) {
                    // that is the only success case
                    event.setType(PDEOneIDMModuleEventToken);
                } else {
                    // otherwise there is an error
                    event.setType(PDEOneIDMModuleEventErrorOAuth);
                }
            }
            event.setSender(PDEOneIDMModule.this);
            event.setResult(result);

            // check if execution is canceled
            if (this.isCancelled()) {
                urlConnection.disconnect();
                return null;
            }

            // send event to listeners
            mEventSource.sendEvent(event);

            // return the received string - which is not evaluated in our case.
            return contentAsString;
        }


        /**
         * @brief Worker function for DownloadTokenTask - query token by username and password.
         *
         * @param oauthUrl server URL
         * @param grantType should be "password"
         * @param username credentials of the user
         * @param password credentails of the user
         * @param scope scope of the requested access token
         * @param clientId clientId
         */
        private String downloadUsernamePasswordToken (String oauthUrl, String grantType, String username, String password,
                                                      String scope, String clientId){
            List<NameValuePair> params;
            String queryString;

            // prepare the params
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("grant_type", grantType));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("scope", scope));
            params.add(new BasicNameValuePair("client_id", clientId));
            queryString = getQuery(params);

            // do the work in downloadToken
            return downloadToken(oauthUrl, queryString);
        }


        /**
         * @brief Worker function for DownloadTokenTask - query token by refresh token.
         *
         * @param oauthUrl server URL
         * @param grantType should be "refresh_token"
         * @param refreshToken supplied by client
         * @param scope scope of the requested access token
         * @param clientId clientId
         */
        private String downloadByRefreshToken (String oauthUrl, String grantType, String refreshToken, String scope,
                                               String clientId){
            List<NameValuePair> params;
            String queryString;

            // prepare the params
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("grant_type", grantType));
            params.add(new BasicNameValuePair("refresh_token", refreshToken));
            params.add(new BasicNameValuePair("scope", scope));
            params.add(new BasicNameValuePair("client_id", clientId));
            queryString = getQuery(params);

            // do the work in downloadToken
            return downloadToken(oauthUrl, queryString);
        }
    }

//----- Constructors ---------------------------------------------------------------------------------------------------


    /**
     * @brief Constructor.
     *
     * @param mClientID App client id - as issued by the OneIDM team
     * @param mURL HTTPS-URL to the server
     */
    public PDEOneIDMModule(String mClientID, String mURL) {
        this.mClientID = mClientID;
        this.mURL = mURL;

        init();
    }


    /**
     * @brief Constructor.
     *
     * Uses the default ULR.
     *
     * @param mClientID App client id - as issued by the OneIDM team
     */
    public PDEOneIDMModule(String mClientID) {
        this.mClientID = mClientID;
        this.mURL = DEFAULT_AUTH_URL;

        init();
    }


    /**
     * @brief Common init function.
     */
    private void init() {
        //init
        mEventSource = new PDEEventSource();

        mRequestActive = false;
        mTask = null;
    }


//----- public member functions ----------------------------------------------------------------------------------------


    /**
     * @brief Request access token by username and password.
     *
     * A active network connection is needed for this. And only one request at a time can be handled by an instance.
     *
     * @return true if the request was started, otherwise false
     * @throws IllegalArgumentException if one of the parameters is empty.
     */
    public boolean requestAccessToken(String username, String password, String scope) throws NetworkErrorException {

        String grantType = "password";

        // check parameters
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(scope)) {
            String message = "Empty argument supplied: ";
            boolean first = true;
            if(TextUtils.isEmpty(username)) {
                message += "username";
                first = false;
            }
            if (TextUtils.isEmpty(password)) {
                if (!first) {
                    message += ", ";
                }
                message += "password";
                first = false;
            }
            if (TextUtils.isEmpty(scope)) {
                if (!first) {
                    message += ", ";
                }
                // add the string scope to request a permanent token
                message += "scope";
            }
            throw new IllegalArgumentException(message);
        }

        // no parallel requests
        if (mRequestActive) return false;

        // check if a network connection is available
        ConnectivityManager connMgr = (ConnectivityManager)
                PDECodeLibrary.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            mTask = new DownloadTokenTask().execute(DownloadTokenTask.ACCESS_TOKEN_BY_USERNAME_PASSWORD,
                                                                mURL, grantType, username, password, scope, mClientID);
            mRequestActive = true;
        } else {
            // display error
            throw new NetworkErrorException("no network connection available");
        }

        return mRequestActive;
    }


    /**
     * @brief Request access token by refresh token.
     *
     * A active network connection is needed for this. And only one request at a time can be handled by an instance.
     *
     * @return true if the request was started, otherwise false
     * @throws IllegalArgumentException if one of the parameters is empty.
     */
    public boolean requestAccessToken(String refreshToken, String scope) throws NetworkErrorException {
        String grantType = "refresh_token";

        // check parameters
        if (TextUtils.isEmpty(refreshToken) || TextUtils.isEmpty(scope)) {
            String message = "Empty argument supplied: ";
            boolean first = true;
            if(TextUtils.isEmpty(refreshToken)) {
                message += "refresh";
                first = false;
            }
            if (TextUtils.isEmpty(scope)) {
                if (!first) {
                    message += ", ";
                }
                // add the string scope to request a permanent token
                message += "scope";
            }
            throw new IllegalArgumentException(message);
        }

        if (mRequestActive) return false;

        ConnectivityManager connMgr = (ConnectivityManager)
                PDECodeLibrary.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            mTask = new DownloadTokenTask().execute(DownloadTokenTask.ACCESS_TOKEN_BY_REFRESH_TOKEN, mURL, grantType, refreshToken, scope, mClientID);
            mRequestActive = true;
        } else {
            // display error
            throw new NetworkErrorException("no network connection available");
        }

        return mRequestActive;
    }


    /**
     * @brief Cancel the running task in order to stop the request.
     */
    public void cancelRequest(){
        if (mTask != null) {
            mTask.cancel(true);
        }
    }

//----- Event Handling -------------------------------------------------------------------------------------------------

    /**
     * @brief Get the eventSource which is responsible for sending PDEEvents events.
     *
     * Most of the events are coming form the PDEAgentController.
     * @return PDEEventSource
     */
    @Override
    public PDEEventSource getEventSource() {
        return mEventSource;
    }


    /**
     * @brief Add event Listener.
     *
     * PDEIEventSource Interface implementation.
     *
     * @param target    Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @return Object which can be used to remove this listener
     *
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName) {
        return mEventSource.addListener(target, methodName);
    }


    /**
     * @brief Add event Listener.
     *
     * PDEIEventSource Interface implementation.
     *
     * @param target    Object which will be called in case of an event.
     * @param methodName Function in the target object which will be called.
     *                   The method must accept one parameter of the type PDEEvent
     * @param eventMask PDEAgentController event mask.
     *                  Will be most of the time PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_SELECTED or
     *                  PDEAgentController.PDE_AGENT_CONTROLLER_EVENT_ACTION_WILL_BE_SELECTED
     * @return Object which can be used to remove this listener
     *
     * @see de.telekom.pde.codelibrary.ui.events.PDEEventSource#addListener
     */
    @Override
    public Object addListener(Object target, String methodName, String eventMask) {
        return mEventSource.addListener(target, methodName, eventMask);
    }

//----- Result evaluation ----------------------------------------------------------------------------------------------


    /**
     * @brief Extract JSON Data and copies it into the dictionary.
     *
     * @param tokener JSON Tokener which is initialized with the json string
     * @param dict Initialized (and maybe partly filled PDEDictionary) where the JSON values should be copied to.
     *
     * @throws JSONException
     */
    private void fillJSONTokenIntoDict(JSONTokener tokener, PDEDictionary dict) throws JSONException{
        // security
        if (dict == null) return;

        // call subfunction for each JSON object
        while (tokener.more()) {
            fillJSONObjectIntoDict((JSONObject) tokener.nextValue(), dict);
        }
    }


    /**
     * @brief Helper function for fillJSONTokenIntoDict.
     *
     * @param object JSON object extracted from the tokener
     * @param dict Destination for the key-value-pairs extracted from the JSON Object
     *
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private void fillJSONObjectIntoDict(JSONObject object, PDEDictionary dict) throws JSONException{
        // security
        if (dict == null) return;

        // iterate through the keys and copy the key-value-pairs to the dictionary
        for  (Iterator<String> keys = object.keys(); keys.hasNext(); ) {
            String key = keys.next();
            dict.put(key, object.getString(key));
        }

    }

    /**
     * @brief Reads an InputStream and converts it to a String.
     *
     * @param stream InputStream to read the string from
     *
     * @return String which was received by the InputStream
     *
     * @throws IOException
     */
    private String readIt(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }


    /**
     * Internal function to put the query parameters URLEncoded int one String
     * @param params list of name value pairs for the query string
     * @return query string
     */
    private String getQuery(List<NameValuePair> params)
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            try {
                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // UTF-8 should be an supportedEncoding...
            }
        }
        return result.toString();
    }
}

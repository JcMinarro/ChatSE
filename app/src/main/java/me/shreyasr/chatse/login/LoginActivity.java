package me.shreyasr.chatse.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shreyasr.chatse.App;
import me.shreyasr.chatse.ChatActivity;
import me.shreyasr.chatse.R;

public class LoginActivity extends AppCompatActivity {

    private static final String USER_AGENT
            = "Mozilla/5.0 (Windows NT 6.2; WOW64)"
            + "AppleWebKit/537.36 (KHTML, like Gecko)"
            + "Chrome/44.0.2403.155 Safari/537.36";

    private OkHttpClient client = new OkHttpClient();
    private CookieManager cookieManager = new CookieManager();

    @Bind(R.id.login_email) EditText emailView;
    @Bind(R.id.login_password) EditText passwordView;
    @Bind(R.id.login_progress) ProgressBar progressBar;
    @Bind(R.id.login_submit) Button loginButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        emailView.setText(App.getPrefs().getString(App.PREF_EMAIL, ""));
        passwordView.setText(App.getPrefs().getString("password", "")); // STOPSHIP
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_submit || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        client.networkInterceptors().add(new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request requestWithUserAgent = originalRequest.newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", USER_AGENT)
                        .build();
                return chain.proceed(requestWithUserAgent);
            }
        });

        attemptLogin();
    }

    @OnClick(R.id.login_submit) void attemptLogin() {
        loginButton.setEnabled(false);

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        View errorView = validateInputs();

        if (errorView != null) {
            errorView.requestFocus();
            loginButton.setEnabled(true);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new LoginAsyncTask().execute(emailView.getText().toString(), passwordView.getText().toString());
    }

    private View validateInputs() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        if (password.isEmpty()) {
            passwordView.setError(getString(R.string.err_blank_password));
            return passwordView;
        }

        if (email.isEmpty()) {
            emailView.setError(getString(R.string.err_blank_email));
            return emailView;
        }

        if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.err_invalid_email));
            return emailView;
        }

        App.getPrefs().edit().putString(App.PREF_EMAIL, email).apply();
        App.getPrefs().edit().putString("password", password).apply(); // STOPSHIP

        return null;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@"); //TODO Improve email prevalidation
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoginAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override protected Boolean doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            try {
//                for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) Log.e("tag", cookie.toString());
//
//                Request seLoginPageRequest = new Request.Builder()
//                        .url("https://openid.stackexchange.com/account/login/")
//                        .build();
//                Response seLoginPageResponse = client.newCall(seLoginPageRequest).execute();
//                String seFkey = getFkey(seLoginPageResponse);
//                Log.e("se login page", seLoginPageResponse.toString());
//
//                for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) Log.e("se login page", cookie.toString());
//
//                RequestBody seLoginRequestBody = new FormEncodingBuilder()
//                        .add("email", email)
//                        .add("password", password)
//                        .add("fkey", seFkey)
//                        .build();
//                Request seLoginRequest = new Request.Builder()
//                        .url("https://openid.stackexchange.com/account/login/submit/")
//                        .post(seLoginRequestBody)
//                        .build();
//                Response seLoginResponse = client.newCall(seLoginRequest).execute();
//                Log.e("se login", seLoginResponse.toString());
//                Log.e("se login", seLoginRequest.toString());
//
//                for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) Log.e("se login", cookie.toString());

                String soFkey = Jsoup.connect("http://stackoverflow.com/users/login/").userAgent(USER_AGENT).get()
                        .select("input[name=fkey]").attr("value");

//                Request soLoginPageRequest = new Request.Builder()
//                        .url("http://stackoverflow.com/users/login/")
//                        .build();
//                Response soLoginPageResponse = client.newCall(soLoginPageRequest).execute();
//                String soFkey = getFkey(soLoginPageResponse);
//                Log.e("so login page", soLoginPageResponse.toString());
//                for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) Log.e("so login page", cookie.toString());

                RequestBody soLoginRequestBody = new FormEncodingBuilder()
                        .add("email", email)
                        .add("password", password)
                        .add("fkey", soFkey)
                        .build();
                Request soLoginRequest = new Request.Builder()
                        .url("https://stackoverflow.com/users/login/")
                        .post(soLoginRequestBody)
                        .build();
                Response soLoginResponse = client.newCall(soLoginRequest).execute();
                Log.e("so login", soLoginResponse.toString());
//                Log.e("so login text", soLoginResponse.body().string());
                for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) Log.e("so login cookies: ", cookie.toString());

                Request chatPageRequest = new Request.Builder()
                        .url("http://chat.stackoverflow.com/")
                        .build();
                Response chatPageResponse = client.newCall(chatPageRequest).execute();
                for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) Log.e("chat page", cookie.toString());

                Document doc = Jsoup.parse(chatPageResponse.body().string());
                Elements fkeyElements = doc.select("input[name=fkey]");
                String chatFkey = fkeyElements.attr("value");

                Log.e("chat message chatfkey", chatFkey);

                RequestBody newMessageRequestBoy = new FormEncodingBuilder()
                        .add("text", "HELLO FROM ANDROID")
                        .add("fkey", chatFkey)
                        .build();
                Request newMessageRequest = new Request.Builder()
                        .url("http://chat.stackoverflow.com/chats/85048/messages/new/")
                        .post(newMessageRequestBoy)
                        .build();
                Response newMessageResponse = client.newCall(newMessageRequest).execute();
                Log.e("chat message", newMessageResponse.toString());
                Log.e("chat message", newMessageResponse.body().string());

                for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) Log.e("chat message cookie", cookie.toString());
                return true;
            } catch (IOException e) {
                Log.e(e.getClass().getSimpleName(), e.getMessage(), e);
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (success) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                LoginActivity.this.finish();
            } else {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(false);
                Toast.makeText(LoginActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();
            }
        }
    }
}

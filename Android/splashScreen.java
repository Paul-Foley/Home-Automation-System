package com.paulfoleyblogs.paul.homeseccontrol;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Switch;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.security.KeyStore;
import java.util.UUID;


/**
 * Created by pcfoley on 22/03/2016.
 */
public class splashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;

    static final String LOG_TAG = com.paulfoleyblogs.paul.homeseccontrol.lights.class.getCanonicalName();
    private static final String CUSTOMER_SPECIFIC_ENDPOINT_PREFIX = "AH5PU35LC0GJH";
    private static final String COGNITO_POOL_ID = "eu-west-1:1045b285-5cba-47db-a4bf-48d4debf3b40";
    private static final String AWS_IOT_POLICY_NAME = "Light2-Policy";
    private static final Regions MY_REGION = Regions.EU_WEST_1;
    private static final String KEYSTORE_NAME = "iot_keystore";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String CERTIFICATE_ID = "default";

    Switch aSwitch;
    Switch aSwitch1;
    Switch aSwitch2;
    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;
    String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;
    KeyStore clientKeyStore = null;
    String certificateId;
    CognitoCachingCredentialsProvider credentialsProvider;
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Intent i = new Intent(splashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();

                clientId = UUID.randomUUID().toString();
                credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        COGNITO_POOL_ID,
                        MY_REGION
                );

                Region region = Region.getRegion(MY_REGION);
                mqttManager = new AWSIotMqttManager(clientId, region, CUSTOMER_SPECIFIC_ENDPOINT_PREFIX);
                mqttManager.setKeepAlive(10);
                AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic", "Android client lost connection", AWSIotMqttQos.QOS0);
                mqttManager.setMqttLastWillAndTestament(lwt);
                mIotAndroidClient = new AWSIotClient(credentialsProvider);
                mIotAndroidClient.setRegion(region);
                keystorePath = getFilesDir().getPath();
                keystoreName = KEYSTORE_NAME;
                keystorePassword = KEYSTORE_PASSWORD;
                certificateId = CERTIFICATE_ID;

                try {
                    if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                        if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                                keystoreName, keystorePassword)) {
                            Log.i(LOG_TAG, "Certificate " + certificateId
                                    + " found in keystore - using for MQTT.");
                            clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                    keystorePath, keystoreName, keystorePassword);
                        } else {
                            Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                        }
                    } else {
                        Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
                }
                if (clientKeyStore == null) {
                    Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                                        new CreateKeysAndCertificateRequest();
                                createKeysAndCertificateRequest.setSetAsActive(true);
                                final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                                createKeysAndCertificateResult =
                                        mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                                Log.i(LOG_TAG,
                                        "Cert ID: " +
                                                createKeysAndCertificateResult.getCertificateId() +
                                                " created.");
                                AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                                        createKeysAndCertificateResult.getCertificatePem(),
                                        createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                                        keystorePath, keystoreName, keystorePassword);
                                clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                        keystorePath, keystoreName, keystorePassword);
                                AttachPrincipalPolicyRequest policyAttachRequest =
                                        new AttachPrincipalPolicyRequest();
                                policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                                policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                                        .getCertificateArn());
                                mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            } catch (Exception e) {
                                Log.e(LOG_TAG,
                                        "Exception occurred when generating new private key and certificate.",
                                        e);
                            }
                        }
                    }).start();
                }

                Log.d(LOG_TAG, "clientId = " + clientId);

                try {
                    mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                        @Override
                        public void onStatusChanged(final AWSIotMqttClientStatus status,
                                                    final Throwable throwable) {
                            Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    });
                    final String topic = "$aws/things/Light2/shadow/update";
                    final String msg = "{\"state\": {\"reported\": {\"status\": 0 }}}";
                    ;

                    try {
                        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Publish error.", e);
                    }

                } catch (final Exception e) {
                    Log.e(LOG_TAG, "Connection error.", e);
                }
            }
        }, SPLASH_TIME_OUT);
    }
}

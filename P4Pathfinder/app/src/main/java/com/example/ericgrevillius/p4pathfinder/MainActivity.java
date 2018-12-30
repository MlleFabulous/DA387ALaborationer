package com.example.ericgrevillius.p4pathfinder;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String ANDROID_KEYSTORE_TAG = "AndroidKeyStore";
    private static final String KEY_NAME = "KeyName";
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private ImageView imageViewFingerprint;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponents(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintHandler fingerprintHandler = new FingerprintHandler();
            if (!checkFingerprint()) {
                imageViewFingerprint.setImageResource(R.drawable.ic_not_fingerprint_black_48dp);
            } else {
                try {
                    generateKey();
                    Cipher cipher = generateCipher();
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    try {
                        CancellationSignal cancellationSignal = new CancellationSignal();
                        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, fingerprintHandler, null);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
            }
        } else {
            imageViewFingerprint.setImageResource(R.drawable.ic_not_fingerprint_black_48dp);
        }
    }

    private void initializeComponents(Bundle bundle) {
        imageViewFingerprint = findViewById(R.id.sign_in_fingerprint_image_view);
        editTextUsername = findViewById(R.id.sign_in_username_edit_text);
        editTextPassword = findViewById(R.id.sign_in_password_edit_text);
        buttonRegister = findViewById(R.id.sign_in_register_button);
        buttonSignIn = findViewById(R.id.sign_in_sign_in_button);
        if (bundle != null) {
            //TODO: add saveOnInstanceState-bundle.
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkFingerprint() {
        if (!fingerprintManager.isHardwareDetected()) {
            return false;
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            return false;
        }
        return keyguardManager.isKeyguardSecure();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_TAG);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE_TAG);

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new FingerprintException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Cipher generateCipher() throws FingerprintException {
        try {
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnrecoverableKeyException | KeyStoreException e) {
            throw new FingerprintException(e);
        }
    }

    private class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int viewID = view.getId();
            if (viewID == buttonRegister.getId()){
                //Register
            } else if (viewID == buttonSignIn.getId()) {
                //Sign In
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
        }
    }

    private class FingerprintException extends Throwable {
        public FingerprintException(String message) {
            super(message);
        }

        public FingerprintException(Throwable cause) {
            super(cause);
        }
    }
}

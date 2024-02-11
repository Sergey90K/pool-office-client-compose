package com.example.poolofficeclientcompose

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.poolofficeclientcompose.ui.PoolOfficeApp
import com.example.poolofficeclientcompose.ui.theme.PoolOfficeClientComposeTheme

class MainActivity : ComponentActivity() {
    private var cancellationSignal: CancellationSignal? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PoolOfficeClientComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PoolOfficeApp(context = this, launchBiometric = { launchBiometric() })
                }
            }
        }
    }

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback =
        @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.authentication_succeeded), Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == 11) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.sorry_but_you_do_not_have_a_password_set_on_your_phone_please_install_a_biometric_passcode_on_your_phone),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.authentication_error_code, errorCode.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                    this@MainActivity.finish()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBiometricSupport(): Boolean {
        val keyGuardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyGuardManager.isDeviceSecure) {
            return true
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun launchBiometric() {
        if (checkBiometricSupport()) {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .apply {
                    setTitle(getString(R.string.authentication_for_pool_office_client))
                    setSubtitle(getString(R.string.please_login_to_get_access))
                    setDescription(getString(R.string.you_must_be_logged_in_to_control_the_smart_home_system))
                    setConfirmationRequired(false)
                    setNegativeButton(
                        getString(R.string.sorry_you_will_not_be_able_to_continue_without_authorisation),
                        mainExecutor,
                        { _, _ ->
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.we_re_sorry_but_the_application_will_be_closed),
                                Toast.LENGTH_SHORT
                            ).show()
                            this@MainActivity.finish()
                        })
                }.build()

            biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authenticationCallback
            )
        }
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Toast.makeText(
                this,
                getString(R.string.authentication_cancelled_signal), Toast.LENGTH_SHORT
            ).show()
        }

        return cancellationSignal as CancellationSignal
    }

}
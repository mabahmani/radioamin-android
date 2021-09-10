package ir.mab.radioamin.ui.splash

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivitySplashBinding
import ir.mab.radioamin.ui.BaseActivity
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity
import ir.mab.radioamin.util.AppConstants.RequestCode.ONE_TAP_SIGN_IN_REQUEST_CODE
import ir.mab.radioamin.vm.remote.UserViewModel
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val userViewModel:UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.handlers = MyHandlers()
    }

    private fun oneTapSignInWithPreviousAccount() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.sweb_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .setAutoSelectEnabled(true)
            .build()


        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this@SplashActivity) { result ->
                try {

                    onTapSignInResultLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())

                } catch (e: IntentSender.SendIntentException) {
                    Timber.e(e)
                }
            }
            .addOnFailureListener(this@SplashActivity) {
                oneTapSignInWithOtherAccounts()
            }
    }

    private fun oneTapSignInWithOtherAccounts() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.sweb_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()


        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this@SplashActivity) { result ->
                try {
                    onTapSignInResultLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())

                } catch (e: IntentSender.SendIntentException) {
                    Timber.e(e)
                }
            }
            .addOnFailureListener(this@SplashActivity) {
                Timber.d("Try Sign in With email/password")
            }
    }

    inner class MyHandlers {
        fun onClickDeviceFilesOnly(view: View) {
            val intent = Intent(this@SplashActivity, DeviceFilesActivity::class.java)
            startActivity(intent)
            finish()
        }

        fun onClickSignIn(view: View) {
            oneTapSignInWithPreviousAccount()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            ONE_TAP_SIGN_IN_REQUEST_CODE ->{
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password

                    when {
                        idToken != null -> {
                            userViewModel.loginByGoogle(idToken).observe(this@SplashActivity, {
                                Timber.d("loginByGoogle %s", it.toString())
                            })
                        }
                        password != null -> {

                        }
                        else -> {

                        }
                    }
                }catch (ex: Exception){
                    Timber.e(ex)
                }
            }
        }
    }



    private var onTapSignInResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val username = credential.id
                val password = credential.password

                when {
                    idToken != null -> {
                        userViewModel.loginByGoogle(idToken).observe(this@SplashActivity, {
                            Timber.d("loginByGoogle %s", it.toString())
                        })
                    }
                    password != null -> {

                    }
                    else -> {

                    }
                }
            }catch (ex: Exception){
                Timber.e(ex)
            }
        }
    }
}
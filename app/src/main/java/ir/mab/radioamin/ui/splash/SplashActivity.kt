package ir.mab.radioamin.ui.splash

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.databinding.ActivitySplashBinding
import ir.mab.radioamin.ui.BaseActivity
import ir.mab.radioamin.ui.deviceonly.DeviceFilesActivity
import ir.mab.radioamin.ui.main.MainActivity
import ir.mab.radioamin.util.*
import ir.mab.radioamin.vm.remote.UserViewModel
import ir.mab.radioamin.vo.enums.ErrorStatus
import ir.mab.radioamin.vo.generic.Status
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

    @Inject lateinit var sharedPreferences: SharedPreferences
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val userViewModel:UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.handlers = MyHandlers()

        if (userLogin()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun userLogin(): Boolean {
        return sharedPreferences.getBoolean(AppConstants.Prefs.IS_LOGIN, false)
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
                toast(getString(R.string.no_google_account))
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
                        loginByGoogle(idToken)
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

    private fun loginByGoogle(idToken: String) {
        userViewModel.loginByGoogle(idToken).observe(this@SplashActivity, {
            Timber.d("loginByGoogle %s", it.toString())
            when(it.status){
                Status.LOADING ->{
                    binding.showProgress = true
                }
                Status.SUCCESS ->{
                    binding.showProgress = false
                    sharedPreferences.edit {
                        putBoolean(AppConstants.Prefs.IS_LOGIN , true)
                        putString(AppConstants.Prefs.ACCESS_TOKEN , it.data?.data?.accessToken)
                        putString(AppConstants.Prefs.REFRESH_TOKEN , it.data?.data?.refreshToken)
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                Status.ERROR ->{
                    binding.showProgress = false
                    when(it.errorStatus){
                        ErrorStatus.NETWORK_CONNECTION_ERROR ->{
                            networkErrorToast()
                        }
                        ErrorStatus.HTTP_EXCEPTION ->{
                            errorToast(it.errorData.toString())
                        }
                        ErrorStatus.IO_EXCEPTION ->{
                            errorToast(it.message.toString())
                        }
                    }
                }
            }
        })
    }
}
package com.example.running.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    /**
     * Initializes the Google sign-in flow.
     * @return Intent to start the Google sign-in activity.
     */
    fun initGoogleLogin(): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    // Uncomment the following method if you want to add Facebook login
    // fun initFacebookLogin(): Intent {
    //     val providers = arrayListOf(
    //         AuthUI.IdpConfig.FacebookBuilder().build()
    //     )
    //     return AuthUI.getInstance()
    //         .createSignInIntentBuilder()
    //         .setAvailableProviders(providers)
    //         .build()
    // }
}

package com.example.running.login

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.running.R
import com.example.running.ui.activities.MainActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"

    @Inject
    lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        this.onSignInResult(result)
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val btnSignup = view.findViewById<View>(R.id.btnSignup)
        val btnForgetPassword = view.findViewById<View>(R.id.btnForgetPassword)
        val edEmail = view.findViewById<EditText>(R.id.edEmail)
        val edPassword = view.findViewById<EditText>(R.id.edPassword)
        val btnLogin = view.findViewById<View>(R.id.btnLogin)
        val googleSignInButton = view.findViewById<View>(R.id.googleSignInButton)

        btnSignup.setOnClickListener {
            navigateToCreateAccount()
        }

        btnForgetPassword.setOnClickListener {
            navigateToForgetPassword()
        }

        edEmail.setOnClickListener {
            edEmail.error = null
        }

        edPassword.setOnClickListener {
            edPassword.error = null
        }

        btnLogin.setOnClickListener {
            validateEmailAndPassword(edEmail, edPassword)
        }

        googleSignInButton.setOnClickListener {
            signInLauncher.launch(viewModel.initGoogleLogin())
        }
    }

    private fun validateEmailAndPassword(edEmail: EditText, edPassword: EditText) {
        when {
            edEmail.text.toString().trim().isEmpty() -> edEmail.error = "Email must not be empty"
            edPassword.text.toString().trim().isEmpty() -> edPassword.error = "Password must not be empty"
            else -> {
                edEmail.error = null
                edPassword.error = null
                initEmailLogin(edEmail.text.toString().trim(), edPassword.text.toString().trim())
            }
        }
    }

    private fun initEmailLogin(email: String, password: String) {
        context?.let { ctx ->
            val processingDialog = MaterialAlertDialogBuilder(ctx)
                .setView(R.layout.layout_loading)
                .setBackground(ResourcesCompat.getDrawable(resources, R.drawable.bg_transparent, null))
                .create()
            processingDialog.show()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    processingDialog.dismiss()
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(ctx, "Login Error: " + task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = auth.currentUser
            updateUI(user)
        } else {
            Log.w(TAG, "Signin failed")
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        currentUser?.let {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    private fun navigateToCreateAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                try {
                    findNavController().navigate(R.id.login_create)
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to create account: ${e.message}", e)
                }
            }
        }
    }

    private fun navigateToForgetPassword() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                try {
                    findNavController().navigate(R.id.login_forget)
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to create account: ${e.message}", e)
                }
            }
        }
    }
}

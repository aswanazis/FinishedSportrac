package com.example.running.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.running.R
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
    }

    private lateinit var edName: EditText
    private lateinit var edEmail: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var googleSignInButton: CardView
    private lateinit var alreadyHaveAnAccount: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edName = view.findViewById(R.id.edName)
        edEmail = view.findViewById(R.id.edEmail)
        edPassword = view.findViewById(R.id.edPassword)
        btnSignUp = view.findViewById(R.id.btnSignUp)
        googleSignInButton = view.findViewById(R.id.googleSignInButton)
        alreadyHaveAnAccount = view.findViewById(R.id.alreadyHaveAnAccount)

        initUi()
    }

    private fun initUi() {
        alreadyHaveAnAccount.setOnClickListener {
            findNavController().popBackStack()
        }
        googleSignInButton.setOnClickListener {
            signInLauncher.launch(viewModel.initGoogleLogin())
        }

        btnSignUp.setOnClickListener {
            val fullName = edName.text.toString().trim()
            val email = edEmail.text.toString().trim()
            val password = edPassword.text.toString().trim()

            if (validateInput(fullName, email, password)) {
                initEmailSignUp(fullName, email, password)
            }
        }
    }

    private fun validateInput(fullName: String, email: String, password: String): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.error = "Invalid email"
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            edPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (fullName.isEmpty()) {
            edName.error = "Name must not be empty"
            return false
        }
        return true
    }

    private fun initEmailSignUp(fullName: String, email: String, password: String) {
        val processingDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.layout_loading)
            .setBackground(ResourcesCompat.getDrawable(resources, R.drawable.bg_transparent, null))
            .create()

        processingDialog.show()

        lifecycleScope.launch {
            try {
                val authResult = withContext(Dispatchers.IO) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
                }

                val user = authResult.user
                user?.let {
                    val updateProfile = UserProfileChangeRequest.Builder().setDisplayName(fullName).build()
                    it.updateProfile(updateProfile).await()

                    Toast.makeText(requireContext(), "Sign up successful!", Toast.LENGTH_SHORT).show()

                    saveNameToSharedPreferences(fullName)

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    requireActivity().finish()
                }
                processingDialog.dismiss()
            } catch (e: Exception) {
                processingDialog.dismiss()
                Toast.makeText(requireContext(), "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNameToSharedPreferences(fullName: String) {
        val sharedPreferences = requireContext().getSharedPreferences("com.example.running.PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("KEY_NAME", fullName)
        editor.apply()
    }

}

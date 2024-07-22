package com.example.running.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.running.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordFragment : Fragment() {

    private lateinit var edEmail: EditText
    private lateinit var btnBack: AppCompatImageButton
    private lateinit var btnGoToLogin: AppCompatImageButton
    private lateinit var btnSendResetLink: AppCompatImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edEmail = view.findViewById<EditText>(R.id.edEmail)
        val btnBack = view.findViewById<View>(R.id.btnBack)
        val btnGoToLogin = view.findViewById<View>(R.id.btnGoToLogin)
        val btnSendResetLink = view.findViewById<View>(R.id.btnSendResetLink)



        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnGoToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        edEmail.setOnClickListener {
            edEmail.error = null
        }

        btnSendResetLink.setOnClickListener {
            val email = edEmail.text.toString().trim()
            if (email.isEmpty()) {
                edEmail.error = "Empty"
            } else {
                sendResetLink(email)
            }
        }
    }

    private fun sendResetLink(emailAddress: String) {
        val processingDialog = context?.let {
            MaterialAlertDialogBuilder(it).setView(R.layout.layout_loading)
                .setBackground(ResourcesCompat.getDrawable(resources, R.drawable.bg_transparent, null))
                .create()
        }
        processingDialog?.setCancelable(false)
        processingDialog?.show()
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                processingDialog?.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(context, "Reset link sent to your given email. Check your email inbox.", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }
}

package com.example.running.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.running.R
import com.example.running.extras.Constants.KEY_NAME
import com.example.running.extras.Constants.KEY_WEIGHT
import com.example.running.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etName)
        val etWeight = view.findViewById<EditText>(R.id.etWeight)
        val btnApplyChanges = view.findViewById<Button>(R.id.btnApplyChanges)
        val btnLogout = view.findViewById<Button>(R.id.btnlogout)

        loadNameFromSharedPreferences(etName)

        btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPreferences(etName, etWeight, view)
            if (success) {
                Snackbar.make(view, "Saved Changes", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun loadNameFromSharedPreferences(etName: EditText) {
        val sharedPreferences = requireContext().getSharedPreferences("com.example.running.PREFERENCES", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("KEY_NAME", "")
        etName.setText(name)
    }

    private fun applyChangesToSharedPreferences(etName: EditText, etWeight: EditText, view: View): Boolean {
        val nameText = etName.text.toString()
        val weightText = etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }
        val sharedPreferences = requireContext().getSharedPreferences("com.example.running.PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("KEY_NAME", nameText)
        editor.putFloat("KEY_WEIGHT", weightText.toFloat())
        editor.apply()
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar?.title = "Let's go $nameText"
        return true
    }

    private fun logoutUser() {
        val sharedPreferences = requireContext().getSharedPreferences("com.example.running.PREFERENCES", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
    }


}

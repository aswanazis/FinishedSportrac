package com.example.running.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.running.R
import com.example.running.databinding.FragmentSetupBinding
import com.example.running.extras.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    private lateinit var binding: FragmentSetupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupBinding.bind(view)

        if (!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState, navOptions
            )
        }
        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPreferences()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all Details", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePersonalDataToSharedPreferences(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPreferences.edit()
            .putString(Constants.KEY_NAME, name)
            .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        val toolbarText = "Let's go, $name"
        toolbar?.title = toolbarText

        return true
    }

}

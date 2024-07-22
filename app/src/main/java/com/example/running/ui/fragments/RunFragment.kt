package com.example.running.ui.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.running.R
import com.example.running.adapters.RunAdapter
import com.example.running.db.Run
import com.example.running.extras.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.running.extras.SortType
import com.example.running.extras.TrackingUtility
import com.example.running.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter
    private lateinit var rvRuns: RecyclerView
    private lateinit var spFilter: Spinner
    private lateinit var fab: FloatingActionButton
    private lateinit var share: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_run, container, false)
    }

    companion object {
        private const val TAG = "RunFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRuns = view.findViewById(R.id.rvRuns)
        spFilter = view.findViewById(R.id.spFilter)
        fab = view.findViewById(R.id.fab)
        share = view.findViewById(R.id.share)

        requestPermissions()
        setupRecyclerView()

        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFilter.adapter = adapter

        spFilter.setSelection(0)

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

        share.setOnClickListener {
            shareMostRecentRun()
        }
    }

    private fun setupRecyclerView() {
        runAdapter = RunAdapter()
        rvRuns.layoutManager = LinearLayoutManager(requireContext())
        rvRuns.adapter = runAdapter
    }

    private fun requestPermissions() {
        if (TrackingUtility.hashLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun shareMostRecentRun() {
        viewModel.runs.observe(viewLifecycleOwner, Observer { runs ->
            val mostRecentRun = runs.maxByOrNull { it.timestamp }
            mostRecentRun?.let { run ->
                run.imageUrl?.let { imageUrl ->
                    shareTrackingResult(run, imageUrl)
                }
            }
        })
    }

    private fun shareTrackingResult(run: Run, imageUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        val localFile = File(requireContext().filesDir, "shared_run_image.png")

        storageRef.getFile(localFile).addOnSuccessListener {
            shareImage(run, localFile)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to download image: ${exception.message}")
        }
    }

    private fun shareImage(run: Run, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val runDetails = """
            Date: ${TrackingUtility.formatDate(run.timestamp)}
            Avg Speed: ${run.avgSpeedInKMH} km/h
            Distance: ${run.distanceInMeters / 1000f} km
            Time: ${TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)}
            Calories Burned: ${run.caloriesBurned} kcal
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, runDetails)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share your run"))
    }
}

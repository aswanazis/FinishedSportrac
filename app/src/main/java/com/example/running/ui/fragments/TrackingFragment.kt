package com.example.running.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.running.R
import com.example.running.db.Run
import com.example.running.extras.Constants.ACTION_PAUSE_SERVICE
import com.example.running.extras.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.running.extras.Constants.ACTION_STOP_SERVICE
import com.example.running.extras.Constants.MAP_ZOOM
import com.example.running.extras.Constants.POLYLINE_COLOR
import com.example.running.extras.Constants.POLYLINE_WIDTH
import com.example.running.extras.TrackingUtility
import com.example.running.services.Polyline
import com.example.running.services.TrackingService
import com.example.running.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.round

const val CANCEL_TRACKING_DIALOG_TAG = "CancelDialog"

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var map: GoogleMap? = null
    private var curTimeInMillis = 0L
    private var menu: Menu? = null

    private var weight: Float = 70f

    private lateinit var mapView: MapView

    override val lifecycle: Lifecycle
        get() = super.lifecycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tracking, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnToggleRun).setOnClickListener {
            toggleRun()
        }

        if (savedInstanceState != null) {
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG
            ) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }

        view.findViewById<View>(R.id.btnFinishRun).setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }

        mapView.getMapAsync { googleMap ->
            map = googleMap
            addAllPolylines()
        }

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            view?.findViewById<View>(R.id.tvTimer)?.let {
                (it as TextView).text = formattedTime
            }
        })
    }

    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog() {
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }

    private fun stopRun() {
        view?.findViewById<View>(R.id.tvTimer)?.let {
            (it as TextView).text = "00:00:00:00"
        }
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && curTimeInMillis > 0L) {
            view?.findViewById<View>(R.id.btnToggleRun)?.let {
                (it as Button).text = "Start"
            }
            view?.findViewById<View>(R.id.btnFinishRun)?.visibility = View.VISIBLE
        } else if (isTracking) {
            view?.findViewById<View>(R.id.btnToggleRun)?.let {
                (it as Button).text = "Stop"
            }
            menu?.getItem(0)?.isVisible = true
            view?.findViewById<View>(R.id.btnFinishRun)?.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            val storageRef = FirebaseStorage.getInstance().reference
            val imageUrl = bmp?.let { bitmap ->
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val imageData = baos.toByteArray()

                val imageRef = storageRef.child("imageurl/${System.currentTimeMillis()}.png")

                val uploadTask = imageRef.putBytes(imageData)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        downloadUri?.let { uri ->
                            val imageUrlString = uri.toString()

                            var distanceInMeters = 0
                            for (polyline in pathPoints) {
                                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
                            }

                            val avgSpeed = round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f

                            val dateTimestamp = Calendar.getInstance().timeInMillis
                            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()

                            val run = Run(
                                timestamp = dateTimestamp,
                                timeInMillis = curTimeInMillis,
                                caloriesBurned = caloriesBurned,
                                avgSpeedInKMH = avgSpeed,
                                distanceInMeters = distanceInMeters,
                                imageUrl = imageUrlString
                            )

                            lifecycleScope.launch {
                                try {
                                    viewModel.insertRun(run)

                                    Snackbar.make(
                                        requireActivity().findViewById<View>(R.id.rootView),
                                        "Run saved successfully",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                    shareTrackingResult(imageUrlString) // Trigger share here
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error saving run: ${e.message}")
                                    Snackbar.make(
                                        requireActivity().findViewById<View>(R.id.rootView),
                                        "Error saving run: ${e.message}",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }

                            stopRun()
                        }
                    } else {
                        // Handle unsuccessful uploads
                        Log.e(TAG, "Image upload task was not successful: ${task.exception}")
                        Snackbar.make(
                            requireActivity().findViewById<View>(R.id.rootView),
                            "Image upload failed: ${task.exception?.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun shareTrackingResult(imageUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        val localFile = File(requireContext().cacheDir, "shared_run_image.png")

        storageRef.getFile(localFile).addOnSuccessListener {
            // Image downloaded successfully
            shareImage(localFile)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to download image: ${exception.message}")
            Snackbar.make(
                requireActivity().findViewById<View>(R.id.rootView),
                "Failed to download image for sharing.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun shareImage(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, getTrackingResultText())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share your run"))
    }

    private fun getTrackingResultText(): String {
        // Customize this text to include relevant tracking information
        return "Here is my tracking result!"
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    fun getMapView(): View {
        return mapView
    }

    private fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).apply {
            this.action = action
            requireContext().startService(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

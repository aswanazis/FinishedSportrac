package com.example.running.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.running.R
import com.example.running.extras.TrackingUtility
import com.example.running.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()

    private lateinit var tvTotalTime: TextView
    private lateinit var tvTotalDistance: TextView
    private lateinit var tvAverageSpeed: TextView
    private lateinit var tvTotalCalories: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTotalTime = view.findViewById(R.id.tvTotalTime)
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance)
        tvAverageSpeed = view.findViewById(R.id.tvAverageSpeed)
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories)

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer { totalTime ->
            totalTime?.let {
                val formattedTime = TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text = formattedTime
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer { distance ->
            distance?.let {
                val km = distance / 1000f
                val roundedDistance = round(km * 10f) / 10f
                val distanceString = "${roundedDistance}km"
                tvTotalDistance.text = distanceString
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer { speed ->
            speed?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km/hr"
                tvAverageSpeed.text = avgSpeedString
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer { calories ->
            calories?.let {
                val totalCalories = "${it}kcal"
                tvTotalCalories.text = totalCalories
            }
        })
    }
}

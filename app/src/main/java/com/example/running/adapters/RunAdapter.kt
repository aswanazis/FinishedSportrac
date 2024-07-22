package com.example.running.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.running.R
import com.example.running.db.Run
import com.example.running.extras.TrackingUtility
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivRunImage = itemView.findViewById<ImageView>(R.id.ivRunImage)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        private val tvAvgSpeed = itemView.findViewById<TextView>(R.id.tvAvgSpeed)
        private val tvDistance = itemView.findViewById<TextView>(R.id.tvDistance)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
        private val tvCalories = itemView.findViewById<TextView>(R.id.tvCalories)

        fun bind(run: Run) {
            run.imageUrl?.let { imageUrl ->
                val into = Glide.with(itemView.context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(ivRunImage)
                into
            } ?: ivRunImage.setImageResource(R.drawable.ic_google)


            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            tvAvgSpeed.text = "${run.avgSpeedInKMH}km/h"
            tvDistance.text = "${run.distanceInMeters / 1000f}km"
            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
            tvCalories.text = "${run.caloriesBurned}kcal"
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Run>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return RunViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

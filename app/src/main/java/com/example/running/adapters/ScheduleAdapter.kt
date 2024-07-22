package com.example.running.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.running.R
import com.example.running.db.Schedule
import com.example.running.ui.fragments.EditScheduleDialogFragment
import com.example.running.ui.fragments.ScheduleFragment
import com.example.running.viewmodel.ScheduleViewModel

class ScheduleAdapter(
    private val fragment: ScheduleFragment,
    private val viewModel: ScheduleViewModel
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var schedule = emptyList<Schedule>()


    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_view)
        val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val current = schedule[position]
        holder.textView.text = String.format("%02d:%02d", current.hour, current.minute)

        holder.btnEdit.setOnClickListener {
            val dialog = EditScheduleDialogFragment(current, viewModel)
            dialog.setTargetFragment(fragment, 0)
            dialog.show(fragment.parentFragmentManager, "EditScheduleDialog")
        }

        holder.btnDelete.setOnClickListener {
            (fragment as ScheduleFragment).cancelAlarm(current)
            current.id?.let { it1 -> viewModel.deleteById(it1) }
        }
    }

    override fun getItemCount() = schedule.size

    internal fun setSchedules(schedules: List<Schedule>) {
        this.schedule = schedules
        notifyDataSetChanged()
    }
}
package com.example.running.ui.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.running.R
import com.example.running.db.Schedule
import com.example.running.viewmodel.ScheduleViewModel

class EditScheduleDialogFragment(
    private val schedule: Schedule,
    private val viewModel: ScheduleViewModel
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etHour = view.findViewById<EditText>(R.id.et_hour)
        val etMinute = view.findViewById<EditText>(R.id.et_minute)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        etHour.setText(String.format("%02d", schedule.hour))
        etMinute.setText(String.format("%02d", schedule.minute))

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                etHour.setText(String.format("%02d", hourOfDay))
                etMinute.setText(String.format("%02d", minute))
                Log.d("EditScheduleDialog", "Time picked: $hourOfDay:$minute")
            },
            schedule.hour,
            schedule.minute,
            DateFormat.is24HourFormat(requireContext())
        )

        etHour.setOnClickListener {
            Log.d("EditScheduleDialog", "etHour clicked")
            timePickerDialog.updateTime(schedule.hour, schedule.minute)
            timePickerDialog.show()
        }

        etMinute.setOnClickListener {
            Log.d("EditScheduleDialog", "etMinute clicked")
            timePickerDialog.updateTime(schedule.hour, schedule.minute)
            timePickerDialog.show()
        }

        btnSave.setOnClickListener {
            val newHour = etHour.text.toString().toInt()
            val newMinute = etMinute.text.toString().toInt()
            Log.d("EditScheduleDialog", "Saving new time: $newHour:$newMinute for schedule id: ${schedule.id}")

            val updatedSchedule = Schedule(schedule.id, newHour, newMinute)
            viewModel.update(updatedSchedule)

            (targetFragment as? ScheduleFragment)?.setAlarm(updatedSchedule)

            Log.d("EditScheduleDialog", "Schedule updated to: $newHour:$newMinute")
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}

package com.lee.dateplanner.timetable.timesheet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.jakewharton.rxbinding4.view.clicks
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.DeleteCheckDialogBinding
import com.lee.dateplanner.timetable.TimeTableFragment
import com.lee.dateplanner.timetable.time.room.Timetable

class DeleteCheckDialog (private val owner: TimeTableFragment, private var timeTable: Timetable, private var position: Int =0, private var deleteType: String)
    : Dialog(owner.requireContext()) {
    private lateinit var binding: DeleteCheckDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // inflate 전에 선언
        binding = DeleteCheckDialogBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        setUpUI()
        setUpListener()
    }

    private fun setUpUI(){
        binding.dialogMessage.text = owner.getString(R.string.dialogMessageToDelete)
    }
    private fun setUpListener(){
        binding.okBtn.clicks().subscribe{ // 예 버튼
            if(deleteType == owner.getString(R.string.deleteTypeTimeSheet)) {
                owner.timetableAdapter?.timeSheetAdapter?.dialogCallBack(true, position, timeTable)
            }else
                owner.timetableAdapter?.dialogCallBack(true,timeTable)
            dismiss() // 종료
        }
        binding.cancelBtn.clicks().subscribe{  // 아니오 버튼
            dismiss() // 종료
        }
    }
}
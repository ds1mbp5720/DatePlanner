package com.lee.dateplanner.timetable.timesheet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.lee.dateplanner.databinding.DeleteTimesheetDialogBinding
import com.lee.dateplanner.timetable.TimeTableFragment

class DeleteTimeSheetDialog (private val owner: TimeTableFragment, private var id: Int,private var position: Int)
    : Dialog(owner.requireContext()) {
    private lateinit var binding: DeleteTimesheetDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // inflate 전에 선언
        binding = DeleteTimesheetDialogBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        setUpUI()
        setUpListener()
    }

    private fun setUpUI(){
        binding.dialogMessage.text = "정말로 지우시겠습니까?"
    }
    private fun setUpListener(){
        binding.okBtn.setOnClickListener{ // 예 버튼
            owner.timetableAdapter?.timeSheetAdapter?.dialogCallBack(true,position)
            dismiss() // 종료
        }
        binding.cancelBtn.setOnClickListener{  // 아니오 버튼
            owner.timetableAdapter?.timeSheetAdapter?.dialogCallBack(false,position)
            dismiss() // 종료
        }
    }

}
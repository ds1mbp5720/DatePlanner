package com.lee.dateplanner.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.lee.dateplanner.R
import com.lee.dateplanner.BR
import com.lee.dateplanner.base.BaseDialogFragment
import com.lee.dateplanner.databinding.MessageDialogBinding

class MessageDialog(
    private val msg: String,
    private val rightBtn: String,
    private val leftBtn: String = "",
    private val isCancel : Boolean = true,
    private val title : String = "",
    private val isCenter :Boolean = true
) :
    BaseDialogFragment<MessageDialogBinding, MessageViewModel>() {

    override val layoutId: Int = R.layout.message_dialog
    override val viewModel: MessageViewModel by viewModels()
    private var leftClickAction = {}
    private var rightClickAction = {}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.setVariable(BR.msg, msg)
        dataBinding.setVariable(BR.title , title)
        dataBinding.twDialogTitle.isVisible = !title.isNullOrEmpty()
        if(!isCenter)
            dataBinding.twDialogMsg.gravity = Gravity.START
        else
            dataBinding.twDialogMsg.gravity = Gravity.CENTER
        dataBinding.txtDialogRight.text = rightBtn
        if (leftBtn.isNotEmpty()) {
            dataBinding.twDialogLeft.text = leftBtn
            dataBinding.groupBtnOne.isVisible = true
        }
        if(!isCancel) isCancelable = isCancel
    }

    override fun initObserve() {
        viewModel.leftClick.observe(this) {
            leftClickAction.invoke()
            dismiss()
        }
        viewModel.rightClick.observe(this) {
            rightClickAction.invoke()
            dismiss()
        }
    }

    fun onLeftBtn(action: () -> (Unit) = {}): MessageDialog {
        leftClickAction = action
        return this
    }

    fun onRightBtn(action: () -> (Unit) = {}): MessageDialog {
        rightClickAction = action
        return this
    }

}
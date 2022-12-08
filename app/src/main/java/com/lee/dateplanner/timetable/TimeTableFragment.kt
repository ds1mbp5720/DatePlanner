package com.lee.dateplanner.timetable

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.lee.dateplanner.MainActivity
import com.lee.dateplanner.R
import com.lee.dateplanner.databinding.TimetablelistFragmentLayoutBinding
import com.lee.dateplanner.map.POIMapFragment
import com.lee.dateplanner.timetable.timesheet.TimeSheet
import com.lee.dateplanner.timetable.time.adapter.TimetableRecyclerAdapter
import com.lee.dateplanner.timetable.time.room.Timetable

/**
 * 저장된 시간계획 제공 fragment
 */
class TimeTableFragment:Fragment() {
    companion object{
        fun newInstance() = TimeTableFragment()
    }
    private val viewModel: TimetableViewModel by viewModels()  // 뷰모델
    private var timetableAdapter: TimetableRecyclerAdapter? = null  // 시간계획 recyclerview adapter
    private lateinit var binding: TimetablelistFragmentLayoutBinding
    private var tableCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TimetablelistFragmentLayoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*val viewModel: TimetableViewModel = ViewModelProvider(this, TimeTableViewModelFactory(
            TimetableRepository(context as Application))
        ).get(TimetableViewModel::class.java)*/

        listenerSetup()
        observerSetup()
        uiSetup()
    }

    /**
     * view의 입력, 버튼 셋팅
     */
    //@SuppressLint("SetTextI18n")
    private fun listenerSetup(){
        // 계획 추가 버튼 클릭시
        binding.addBtn.setOnClickListener {
            var timeSheetList =  mutableListOf<TimeSheet>()

            /**
             * 결과 출력 확인용 hard coding data 들
             */
            var emptyTimeSheet1 = TimeSheet("000공연","13:30","서울시 00동 00","공연","37.5143225723","127.062831022")
            var emptyTimeSheet2 = TimeSheet("000카페","16:30","서울시 00동 00","커피","37.510404219518","127.06429358258")
            var emptyTimeSheet3 = TimeSheet("000식당","17:30","서울시 00동 00","밥","37.51486885062181","127.05880695418199")
            var emptyTimeSheet4 = TimeSheet("메가박스","19:30","서울 강남구 삼성동 15","영화","37.5130779481089","127.058215118259")
            timeSheetList.add(emptyTimeSheet1)
            timeSheetList.add(emptyTimeSheet2)
            timeSheetList.add(emptyTimeSheet3)
            timeSheetList.add(emptyTimeSheet4)

            /**
             * 계획 추가 버튼을 dialog 로 하여 선택한 날짜를 바로 가져와서 변수에 넣기
             */
            var date = "00.00"
            // room db 추가
            //viewModel.insertTimeTable(Timetable(tableCount,timeSheetList ,date))
            viewModel.insertTimeTable(Timetable(timeSheetList ,date))
            tableCount++
        }
    }

    /**
     * 옵저버 셋팅
     */
    @SuppressLint("SetTextI18n")
    private fun observerSetup(){
        viewModel.getAllTimetables()?.observe(viewLifecycleOwner){ timetable ->
            timetable?.let {
                timetableAdapter?.setTimetableList(it)
            }
        }
    }

    /**
     * ui 셋팅 함수
     */
    private fun uiSetup(){
        with(binding.allTimeTable){
            timetableAdapter = TimetableRecyclerAdapter(viewModel,this@TimeTableFragment)
            adapter = timetableAdapter
        }
    }
    fun dialogCallBack(select: Boolean, id: Int, position: Int){
        if(select){
            // 타임시트 리스트에서 삭제 후 해당 타임테이블 업데이트 하기
            findTimetable(id,position)
        }else{
            Log.e(TAG,"취소")
        }
    }
    private fun findTimetable(id: Int, position: Int){
        viewModel.findTimetable(id)
        viewModel.getSearchResults().observe(viewLifecycleOwner){ timetable ->
            timetable?.let {
                deleteTimeSheet(it[0],id,position)
                //fragment 갱신
                val ft = activity?.supportFragmentManager?.beginTransaction()
                ft!!.replace(R.id.tabContent,TimeTableFragment()).commit()
            }
        }

    }
    private fun deleteTimeSheet(timeTable : Timetable, id:Int, position: Int){
        val timeSheetList = timeTable.timeSheetList as ArrayList<TimeSheet>?
        timeSheetList?.removeAt(position)
        if(timeTable != null){
            timeTable.timeSheetList = timeSheetList // 새로 추가된 list 로 교체
            //추가한 timesheet 업데이트
            timeTable.timeSheetList?.let { it1 ->
                viewModel.updateTimetable(it1,id)
            }
        }
    }

    /**
     * dialogCallBack 함수의 observe 가 삭제할때마다 횟수가 중첩되어 여러번 실행되어 한번만하는 함수
     */
//    fun <T> LiveData<T>.(owner: LifecycleOwner, observer: (T) -> Unit) {
//        var firstObservation = true
//
//        observe(owner, object: Observer<T>
//        {
//            override fun onChanged(value: T) {
//                if(firstObservation)
//                {
//                    firstObservation = false
//                }
//                else
//                {
//                    removeObserver(this)
//                    observer(value)
//                }
//            }
//        })
//    }

}
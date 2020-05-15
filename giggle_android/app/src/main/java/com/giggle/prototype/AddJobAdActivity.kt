package com.giggle.prototype

import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_jobad.*
import java.lang.Integer.parseInt
import java.util.*


class AddJobAdActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_jobad)

        // 현재 날짜 받기
        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        // 현재 날짜로 초기화
        datePicker.init(year,month,day, DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->  })
        starttp.setIs24HourView(true)
        finishtp.setIs24HourView(true)

        //초기화 버튼
        btReset.setOnClickListener({
            resetText()
        })

        //등록 버튼
        btRegister.setOnClickListener() {
            val shopname: String = edShopName.text.toString()//가게 이름
            val shopposition: String = edPosition.text.toString()//가게 위치
            val businessinfo: String = edBusinessInfo.text.toString()//업무 내용
            val priorityreq: String = edPriorityReq.text.toString()//우대 요건
            val hourlypay = parseInt(edHourlyPay.text.toString())//시급
            val age3:String = edAge1.text.toString()
            val age4:String = edAge2.text.toString()
            val age1:Int//최소 나이
            val age2:Int//최대 나이
            val year = datePicker.year
            val month = datePicker.month+1
            val day = datePicker.dayOfMonth
            val date = year*10000 + month*100 + day//알바 날짜
            val stHour = starttp.hour
            val stMinute = starttp.minute
            var stTime = ""//알바 시작 시간
            val fnHour = finishtp.hour
            val fnMinute = finishtp.minute
            var fnTime = ""//알바 종료 시간
            var sex = ""//성별

            if(age3.isEmpty()&&age4.isEmpty()) {
                age1 = 0
                age2 = 0
            }
            else if(age3.isEmpty()&&age4.isNotEmpty()){
                age1 = 0
                age2 = parseInt(age4)
            }
            else if(age4.isEmpty()&&age3.isNotEmpty()){
                age1 = parseInt(age3)
                age2 = 0
            }
            else {
                age1 = parseInt(age3)
                age2 = parseInt(age4)
            }

            if(shopname.isEmpty())
            Toast.makeText(this,"가게 이름을 입력해주세요.", Toast.LENGTH_LONG).show()
            if(shopposition.isEmpty())
                Toast.makeText(this,"가게 위치를 입력해주세요.", Toast.LENGTH_LONG).show()
            if(businessinfo.isEmpty())
                Toast.makeText(this,"업무 내용을 입력해주세요.", Toast.LENGTH_LONG).show()
            if(hourlypay<8590)
                Toast.makeText(this,"최저 시급은 8590원 입니다", Toast.LENGTH_LONG).show()
            if(stHour*60+stMinute>fnHour*60+fnMinute)
                Toast.makeText(this,"아르바이트 시작시간과 종료시간을 확인해주세요.", Toast.LENGTH_LONG).show()
            if(age2!=0&&age1>age2)
                Toast.makeText(this,"최소나이와 최대나이를 확인해주세요.", Toast.LENGTH_LONG).show()

            if(stHour<10 && stMinute<10)
                stTime = "0$stHour:0$stMinute"
            else if(stHour<10 && stMinute>=10)
                stTime = "0$stHour:$stMinute"
            else if(stHour>=10 && stMinute<10)
                stTime = "$stHour:0$stMinute"
            else
                stTime = "$stHour:$stMinute"

            if(fnHour<10 && fnMinute<10)
                fnTime = "0$fnHour:0$fnMinute"
            else if(fnHour<10 && fnMinute>=10)
                fnTime = "0$fnHour:$fnMinute"
            else if(fnHour>=10 && fnMinute<10)
                fnTime = "$fnHour:0$fnMinute"
            else
                fnTime = "$fnHour:$fnMinute"

            if (btMan.isChecked){
                sex = btMan.text.toString()
            }
            else if(btWomen.isChecked){
                sex = btWomen.text.toString()
            }

            //DB에 저장
            jobAddb(shopname,shopposition,businessinfo,priorityreq,hourlypay,age1,age2,date,stTime,fnTime,sex)
        }
    }

    fun resetText(){
        edShopName.setText(null)
        edPosition.setText(null)
        edHourlyPay.setText(null)
        edBusinessInfo.setText(null)
        btMan.setChecked(false)
        btWomen.setChecked(false)
        edAge1.setText(null)
        edAge2.setText(null)
        edPriorityReq.setText(null)
        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        datePicker.init(year,month,day, DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->  })

    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun jobAddb(
        shopname:String,
        shopposition:String,
        businessinfo:String,
        priorityreq:String,
        hourlypay:Int,
        age1:Int,
        age2:Int,
        date: Int,
        stTime:String,
        fnTime:String,
        sex:String) {
        val db = FirebaseFirestore.getInstance()
        val jobad = JobAd(shopname,shopposition,businessinfo,priorityreq,hourlypay,age1,age2,sex,date,stTime,fnTime)
        db.collection("jobads").document(shopname).set(jobad)
    }
}
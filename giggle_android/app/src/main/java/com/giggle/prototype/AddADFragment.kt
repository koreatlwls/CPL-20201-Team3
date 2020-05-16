package com.giggle.prototype

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_add_ad.*
import java.util.*

class AddADFragment : Fragment() {

    val PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? =null //가게 사진1
    var photoUri1: Uri? =null //가게 사진2
    var photoUri2: Uri? =null //가게 사진3
    var photoUri3: Uri? =null //가게 사진4
    var photoUri4: Uri? =null //가게 사진5
    var photoCnt:Int =0
    var storage: FirebaseStorage? = null
    private var auth: FirebaseAuth? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return  inflater.inflate(R.layout.fragment_add_ad, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view,savedInstanceState)

        //현재 날짜 받아오기
        var c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        //인증
        auth = FirebaseAuth.getInstance()

        //firestorage
        storage = FirebaseStorage.getInstance()
        //사진을 클릭했을때
        add_photo.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
        add_photo1.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
        add_photo2.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
        add_photo3.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
        add_photo4.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

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
            val hourlypay = Integer.parseInt(edHourlyPay.text.toString())//시급
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

            //입력값 확인
            if(age3.isEmpty()&&age4.isEmpty()) {
                age1 = 0
                age2 = 0
            }
            else if(age3.isEmpty()&&age4.isNotEmpty()){
                age1 = 0
                age2 = Integer.parseInt(age4)
            }
            else if(age4.isEmpty()&&age3.isNotEmpty()){
                age1 = Integer.parseInt(age3)
                age2 = 0
            }
            else {
                age1 = Integer.parseInt(age3)
                age2 = Integer.parseInt(age4)
            }
            if(shopname.isEmpty())
                Toast.makeText(activity,"가게이름을 입력해주세요.", Toast.LENGTH_LONG).show()
            if(shopposition.isEmpty())
                Toast.makeText(activity,"가게위치를 입력해주세요.", Toast.LENGTH_LONG).show()
            if(businessinfo.isEmpty())
                Toast.makeText(activity,"업무내용을 입력해주세요.", Toast.LENGTH_LONG).show()
            if(hourlypay<8590)
                Toast.makeText(activity,"최저시급을 확인해주세요.", Toast.LENGTH_LONG).show()
            if(stHour*60+stMinute>fnHour*60+fnMinute)
                Toast.makeText(activity,"시작시간과 종료시간을 확인해주세요.", Toast.LENGTH_LONG).show()
            if(age2!=0&&age1>age2)
                Toast.makeText(activity,"최소나이와 최대나이를 확인해주세요.", Toast.LENGTH_LONG).show()
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
            if(shopname.isNotEmpty()) {
                jobAddb(
                    shopname,
                    shopposition,
                    businessinfo,
                    priorityreq,
                    hourlypay,
                    age1,
                    age2,
                    date,
                    stTime,
                    fnTime,
                    sex
                )
                currentUpload() //사진 등록
            }
        }
    }
    override fun onActivityResult(requestCode:Int, resultCode: Int, data: Intent?){

      if(requestCode == PICK_IMAGE_FROM_ALBUM)  {
          if(photoCnt==0 &&resultCode == Activity.RESULT_OK){
              println(data?.data)
              photoUri = data?.data
              add_photo.setImageURI(data?.data)
              photoCnt++
          }
          else if(photoCnt==1 &&resultCode == Activity.RESULT_OK){
              println(data?.data)
              photoUri1 = data?.data
              add_photo1.setImageURI(data?.data)
              photoCnt++
          }
          else if(photoCnt==2 &&resultCode == Activity.RESULT_OK){
              println(data?.data)
              photoUri2 = data?.data
              add_photo2.setImageURI(data?.data)
              photoCnt++
          }
          else if(photoCnt==3 &&resultCode == Activity.RESULT_OK){
              println(data?.data)
              photoUri3 = data?.data
              add_photo3.setImageURI(data?.data)
              photoCnt++
          }
          else if(photoCnt==4 &&resultCode == Activity.RESULT_OK){
              println(data?.data)
              photoUri4 = data?.data
              add_photo4.setImageURI(data?.data)
          }
      }
    }
    fun currentUpload(){
        val shopname: String = edShopName.text.toString()
        val imageFileName = shopname +"_1.png"
        val imageFileName1 = shopname +"_2.png"
        val imageFileName2 = shopname +"_3.png"
        val imageFileName3 = shopname +"_4.png"
        val imageFileName4 = shopname +"_5.png"
        if(photoUri!=null){
            val storageRef = storage?.reference?.child("shopimages/"+shopname)?.child(imageFileName)
            storageRef?.putFile(photoUri!!)?.addOnSuccessListener{taskSnapshot ->
                Toast.makeText(activity,"업로드 성공",Toast.LENGTH_SHORT).show()
            }
        }
        if(photoUri1!=null){
            val storageRef1 = storage?.reference?.child("shopimages/"+shopname)?.child(imageFileName1)
            storageRef1?.putFile(photoUri1!!)?.addOnSuccessListener{taskSnapshot ->
                Toast.makeText(activity,"업로드 성공",Toast.LENGTH_SHORT).show()
            }
        }
        if(photoUri2!=null){
            val storageRef2 = storage?.reference?.child("shopimages/"+shopname)?.child(imageFileName2)
            storageRef2?.putFile(photoUri2!!)?.addOnSuccessListener{taskSnapshot ->
                Toast.makeText(activity,"업로드 성공",Toast.LENGTH_SHORT).show()
            }
        }
       if(photoUri3!=null){
            val storageRef3 = storage?.reference?.child("shopimages/"+shopname)?.child(imageFileName3)
            storageRef3?.putFile(photoUri3!!)?.addOnSuccessListener{taskSnapshot ->
                Toast.makeText(activity,"업로드 성공",Toast.LENGTH_SHORT).show()
            }
        }
        if(photoUri4!=null){
            val storageRef4 = storage?.reference?.child("shopimages")?.child(imageFileName4)
            storageRef4?.putFile(photoUri4!!)?.addOnSuccessListener{taskSnapshot ->
                Toast.makeText(activity,"업로드 성공",Toast.LENGTH_SHORT).show()
            }
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
        add_photo.setImageURI(null)
        add_photo1.setImageURI(null)
        add_photo2.setImageURI(null)
        add_photo3.setImageURI(null)
        add_photo4.setImageURI(null)
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
        db.collection("jobads").document(shopname).set(jobad) //DB에 shopname을 기준으로 저장
    }

    companion object {
        fun newInstance(): AddADFragment = AddADFragment()

    }
}
package com.giggle.prototype

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_meminfo.*
import kotlinx.android.synthetic.main.fragment_add_ad.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import java.io.IOException

class MemberInfoActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var mMap:GoogleMap
    private var isFabOpen=false
    val markerOptions:MarkerOptions= MarkerOptions()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest // 위치 요청
    lateinit var locationCallback: MyLocationCallBack // 내부 클래스, 위치 변경 후 지도에 표시.
    val REQUEST_ACCESS_FINE_LOCATION = 1000
    private var Location_finded:String="location"
    private lateinit var latlng:LatLng
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onMapReady(googleMap: GoogleMap) {
      mMap=googleMap
        val Seoul=LatLng(37.0,126.9)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Seoul))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meminfo)
        latlng= LatLng(37.0,126.9)
        val mapFragment=supportFragmentManager.findFragmentById(R.id.memMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationInit()
        loadInfo()
        btn_search2.setOnClickListener{searchLocation()}
        btn_MyLocation2.setOnClickListener{OnMyLocationButtonClick()}
        btregister.setOnClickListener{
            val name = edMemName.text.toString()
            var sex = ""
            var age = 0
            val position = txLocResult.text.toString()
            var number = ""
            if(edMemPhone1.text.isNotEmpty()&&edMemPhone2.text.isNotEmpty()&&edMemPhone3.text.isNotEmpty()){
                number = edMemPhone1.text.toString()+"-"+edMemPhone2.text.toString()+"-"+edMemPhone3.text.toString()
                println(number)
            }
            if(edMemAge.text.isNotEmpty()){
                age = Integer.parseInt(edMemAge.text.toString())
            }
            if (radioMan.isChecked){
                sex = radioMan.text.toString()
            }
            else if(radioWomen.isChecked){
                sex = radioWomen.text.toString()
            }
            if(name.isEmpty()||position.isEmpty()||age<1||sex.isEmpty()||number.isEmpty()){
                edMemName.setBackgroundResource(R.drawable.red_edittext)
                edMemAge.setBackgroundResource(R.drawable.red_edittext)
                edMemPosition.setBackgroundResource(R.drawable.red_edittext)
                edMemPhone1.setBackgroundResource(R.drawable.red_edittext)
                edMemPhone2.setBackgroundResource(R.drawable.red_edittext)
                edMemPhone3.setBackgroundResource(R.drawable.red_edittext)
                Toast.makeText(this, "모든 항목을 채워주세요.", Toast.LENGTH_LONG).show()
            }
            if(name.isNotEmpty()&&position.isNotEmpty()&&age>0&&sex.isNotEmpty()){
                val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_AppCompat_Light_Dialog))
                builder.setTitle("등록")
                builder.setMessage("등록하시겠습니까?")
                builder.setPositiveButton("확인"){_,_->
                    val db = FirebaseFirestore.getInstance()
                    val member = members(name,age,sex,position, user?.uid,number,latlng.latitude,latlng.longitude)
                    if (user != null) {
                        var map = mutableMapOf<String,Any>()
                        map["uid"] =user.uid.toString()
                        db.collection("members").document(user.uid).set(member)
                        db.collection("recruit_shop").document(user.uid).set(map)
                    } //DB에 uid기준으로 멤버정보 저장
                    Toast.makeText(this,"등록 성공",Toast.LENGTH_SHORT).show()
                    resetText()//초기화
                }
                builder.setNegativeButton("취소"){_,_->

                }
                builder.show()
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION // 위치에 대한 권한 요청
            )
            != PackageManager.PERMISSION_GRANTED
// 사용자 권한 체크로
// 외부 저장소 읽기가 허용되지 않았다면
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) { // 허용되지 않았다면 다시 확인.
                alert(
                    "위치 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다.",

                    "권한이 필요한 이유"
                ) {
                    yesButton {
                        // 권한 허용
                        ActivityCompat.requestPermissions(
                            this@MemberInfoActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_ACCESS_FINE_LOCATION
                        )
                    }
                    noButton {
                        // 권한 비허용
                    }
                }.show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            addLocationListener()
        }
    }

    @SuppressLint("MissingPermission")
    // 위험 권한 사용시 요청 코드가 호출되어야 하는데,
    // 없어서 발생됨. 요청 코드는 따로 처리 했음.
    fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        //위치 권한을 요청해야 함.
        // 액티비티가 잠깐 쉴 때,
        // 자신의 위치를 확인하고, 갱신된 정보를 요청
    }

    fun removeLocationLister(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        // 어플이 종료되면 지도 요청 해제.
    }
    fun resetText(){
        edMemAge.setText(null)
        edMemName.setText(null)
        edMemPosition.setText(null)
        radioMan.setChecked(false)
        radioWomen.setChecked(false)
        edMemPhone1.setText(null)
        edMemPhone2.setText(null)
        edMemPhone3.setText(null)
        edMemName.setBackgroundResource(R.drawable.border_dark)
        edMemAge.setBackgroundResource(R.drawable.border_dark)
        edMemPosition.setBackgroundResource(R.drawable.border_dark)
        edMemPhone1.setBackgroundResource(R.drawable.border_dark)
        edMemPhone2.setBackgroundResource(R.drawable.border_dark)
        edMemPhone3.setBackgroundResource(R.drawable.border_dark)
    }

    fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        // 현재 사용자 위치를 저장.
        locationCallback = MyLocationCallBack() // 내부 클래스 조작용 객체 생성
        locationRequest = LocationRequest() // 위치 요청.

        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        // 위치 요청의 우선순위 = 높은 정확도 우선.
        locationRequest.interval = 10000 // 내 위치 지도 전달 간격
        locationRequest.fastestInterval = 5000 // 지도 갱신 간격.

    }

    fun OnMyLocationButtonClick(){
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback, null
                )
                var addressList:List<Address>?=null
                val geoCoder=Geocoder(this)
                addressList=geoCoder.getFromLocation(latlng.latitude,latlng.longitude,1)
                Location_finded=addressList!![0].getAddressLine(0)
                txLocResult.setText(Location_finded)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latlng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,17f))
    }
    inner class MyLocationCallBack : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            val location = p0?.lastLocation

            location?.run {
                latlng = LatLng(latitude, longitude) // 위도 경도 좌표 전달.


            }
        }
    }

    override fun onResume() {
        super.onResume()
        addLocationListener()
    }

    override fun onPause() {
        super.onPause()
    }

    fun searchLocation(){
        lateinit var location:String
        location=edMemPosition.text.toString()
        var addressList:List<Address>?=null

        if(location==""){
            Toast.makeText(this,"provide location",Toast.LENGTH_SHORT).show()
        }
        else{
            val geoCoder= Geocoder(this)
            try{
                addressList=geoCoder.getFromLocationName(location,1)

            }catch(e: IOException){
                e.printStackTrace()
            }
            if(addressList!!.size==1) {
                val address = addressList!![0]
                latlng = LatLng(address.latitude, address.longitude)
                Location_finded = address.getAddressLine(0)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latlng).title(location))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17f))
                Toast.makeText(
                    this,
                    address.latitude.toString() + " " + address.longitude,
                    Toast.LENGTH_LONG
                ).show()
                txLocResult.setText(Location_finded)
            }
            else
            {
                Toast.makeText(this,"해당하는 위치 정보가 없습니다.",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun loadInfo(){
        val db = FirebaseFirestore.getInstance()

        db.collection("members").whereEqualTo("uid",user?.uid).get().addOnSuccessListener { result-> for(document in result){
            edMemName.setText(document.data["name"].toString())
            edMemAge.setText(document.data["age"].toString())
            if(document.data["sex"].toString()=="남자")
                radioMan.isChecked=true
            else
                radioWomen.isChecked=true
            txLocResult.text = document.data["position"].toString()
            val str=document.data["phonenumber"].toString()
            val (phone1,phone2,phone3) = str.split('-')
            edMemPhone1.setText(phone1)
            edMemPhone2.setText(phone2)
            edMemPhone3.setText(phone3)
        }
        }
    }
}
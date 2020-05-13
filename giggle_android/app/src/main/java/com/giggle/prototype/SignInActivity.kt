package com.giggle.prototype

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenu
import java.io.IOException
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var editText:EditText
    private lateinit var mMap: GoogleMap
    //mMap은 앱에서 볼 지도를 가리킴.
    //지도 조작을 위한 객체임.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //위치값 얻어오기 객체
    lateinit var locationRequest: LocationRequest
    //위치 요청
    lateinit var locationCallback: MyLocationCallback
    //내부 클래스, 위치 변경 후 지도에 표시

    val PERMISSIONS= arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
    val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var lntlng:LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.search)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        //as를 통해 형변환.
        mapFragment.getMapAsync(this)


        //Async 비동기(타이밍을 맞추지 않고 처리)
        //전화기, 무전기.

        if (hasPermissions()) {
            locationInit()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }

        // FIXME
        // btn_MyLocation.setOnClickListener{OnMyLocationButtonClick()}
        // btn_MyPage.setOnClickListener{OnMypageButtonClick()}

    }


    fun searchLocation(view:View){
        val locationSearch:EditText=findViewById(R.id.search)
        lateinit var location:String
        location=locationSearch.text.toString()
        var addressList:List<Address>?=null

        if(location==""){
            Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
        }
        else{
            val geoCoder= Geocoder(this)
            try{
                addressList=geoCoder.getFromLocationName(location,1)
            }catch(e:IOException){
                e.printStackTrace()
            }
            val address=addressList!![0]
            val latLng=LatLng(address.latitude,address.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))
            Toast.makeText(applicationContext,address.latitude.toString()+" "+address.longitude,Toast.LENGTH_LONG).show()
        }
    }


 fun OnMyLocationButtonClick(){
    when{
        hasPermissions()->{
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback, null
            )
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(lntlng))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lntlng,17f))
        }
       else ->{
           Toast.makeText(applicationContext,"위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
       }
    }
}
    fun OnMypageButtonClick(){
        val nextIntent= Intent(this,MyPageActivity::class.java)
        startActivity(nextIntent)
    }
    private fun hasPermissions():Boolean{
        for(permission in PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    // 권한이 승인 됐다면
                    addLocationListener()
                } else {
                    // 권한이 거부 됐다면
                    toast("권한 거부 됨")
                }
                return
            }
        }
    }
    fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        //현재 사용자 위치를 저장.
        locationCallback = MyLocationCallback()
        //내부 클래스 조작용 객체 생성
        locationRequest = LocationRequest()
        //위치 요청
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //위치 요청의 우선순위 = 높은 정확도 우선.
        locationRequest.interval = 10000
        //내 위치 지도 전달 간격
        locationRequest.fastestInterval = 5000
        //지도 갱신 간격
    }


    override fun onMapReady(googleMap: GoogleMap) {
        //지도가 준비되었다면 호출.
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0) //위도 경도, 변수에 저장
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //지도에 표시를 하고 제목을 추가.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //마커 위치로 지도 이동  // 위치가 변경이 된다면 따라서 움직여라.
    }

    override fun onResume() {//잠깐 쉴 때
        super.onResume()
        addLocationListener()
    }

    override fun onPause() {
        super.onPause()
        removeLocationListener()
    }

    fun removeLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }//어플이 종료되면 지도 요청 해제

    @SuppressLint("MissingPermission")
    //위험 권한 사용 시 요청 코드가 호출 되어야 하는데
    //없어서 발생됨. 요청 코드는 따로 처리 했음
    fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback, null
        ) //위치 권한을 요청해야 함.
        //액티비티가 잠깐 쉴 때
        //자신의 위치를 확인하고 갱신된 정보를 요청

    }


    inner class MyLocationCallback : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            val location = p0?.lastLocation
            //위도 경도를 지도 서버에 전달하면~
            //위치에 대한 지도 결과를 받아와서 저장.

            location?.run {
                //location이 null이 아닐 때 아래 메소드를 구동하겠다.
               lntlng = LatLng(latitude, longitude)
                //위도 경도 좌표 전달
                //지도에 애니메이션 효과로 카메라 이동.
                //좌표위치로 이동하면서 배율은 17(0~19)

                Log.d(
                    "MapActivity"
                    , "위도: $latitude, 경도 : $longitude"
                )

            }
        }
    }
}


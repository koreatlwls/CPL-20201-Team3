package com.giggle.prototype

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import android.content.pm.PackageManager
import android.location.Location
import android.Manifest
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    // 런타임에서 권한이 필요한 퍼미션 목록
    val PERMISSION = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    //퍼미션 승인 요청시 사용하는 요청 코드
    val REQUEST_PERMISSION_CODE = 1

    //기본맵 줌 레벨
    val DEFAULT_ZOOM_LEVEL = 17f

    //현재 위치를 가져올 수 없는 경우 서울 시청의 위치를 지도로 보여주기 위해서 서울 시청의 위치를 변수로 선언
    //LatLng클래스는 위도와 경도를 가지는 클래스이다.
    val CITY_HALL = LatLng(37.5662952, 126.97794509999994)

    //구글맵 객체를 참조할 멤버 변수
    var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //맵뮤에 onCreate함수 호출
        mapView.onCreate(savedInstanceState)

        //앱이 실행될 때 권한이 있는지 체크하는 함수
        if (hasPermissions()) {
            //권한이 있는 경우 맵 초기화
            initMap()
        } else {
            //권한이 없는 경우 권한을 요청한다.
            ActivityCompat.requestPermissions(this, PERMISSION, REQUEST_PERMISSION_CODE)
        }

        //현재위치 클릭 버튼 이벤트 리스너 설정
        myLocationButton.setOnClickListener { onMyLocationButtonClick() }
    }

    //퍼미션 확인하는 함수
    private fun hasPermissions(): Boolean {
        for (permission in PERMISSION) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    //맵 초기화 하는 함수
    @SuppressLint("MissingPermission")
    fun initMap() {
        //맵 뷰에서 구글 맵을 불러오는 함수, 컬백 함수에서 구글 맵 객체가 전달된다.
        mapView.getMapAsync {

            //구글맵 멤버 변수에 구글맵 객체 저장
            googleMap = it
            //현재 위치로 이동 버튼 비활성화
            it.uiSettings.isMyLocationButtonEnabled = false

            //위치 사용 권한이 있는 경우
            when {
                hasPermissions() -> {
                    //현재 위치 표시 활성화
                    it.isMyLocationEnabled = true
                    //현재 위치로 카메라 이동
                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            getMyLocation(),
                            DEFAULT_ZOOM_LEVEL
                        )
                    )
                }
                else -> {
                    //권한이 없으면 서울시청의 위치로 이동
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
                }
            }
        }
    }

    //내 위치를 구하는 함수
    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {
        //위치를 측정하는 프로바이더를 GPS센서로 측정한다.
        val locationProvider: String = LocationManager.GPS_PROVIDER
        //위치 서비스 객체를 불러온다.
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //마지막으로 업데이트 된 위치를 가져온다.
        val lastKnownLocation: Location = locationManager.getLastKnownLocation(locationProvider)
        //위도 경도 객체로 반환한다.
        return LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
    }

    //버튼을 눌렀을때 자신의 위치로 이동하는 함수이다.
    fun onMyLocationButtonClick() {
        when {
            hasPermissions() -> {
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        getMyLocation(),
                        DEFAULT_ZOOM_LEVEL
                    )
                )
            }
            else -> Toast.makeText(applicationContext, "위치사용권한 설정을 동의해 주세요", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //하단 맵뷰의 라이프 사이클 함수 호출을 위한 코드들이다.
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}


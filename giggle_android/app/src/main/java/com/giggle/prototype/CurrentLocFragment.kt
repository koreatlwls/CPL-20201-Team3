package com.giggle.prototype

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_current_loc.*


class CurrentLocFragment : Fragment(),OnMapReadyCallback{

    private lateinit var rootView:View
    private lateinit var mContext:FragmentActivity
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //위치값 얻어오기 객체
    lateinit var locationRequest: LocationRequest
    //위치 요청
    lateinit var locationCallback: MyLocationCallback
    val PERMISSIONS= arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var lntlng:LatLng


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView=inflater.inflate(R.layout.fragment_current_loc,container,false)
        mapView=rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        mContext=FragmentActivity()

        return rootView
    }

    fun CurrentLocFragment() {}

    companion object {
        fun newInstance(): CurrentLocFragment = CurrentLocFragment()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        //지도가 준비되었다면 호출.MapsInitializer.initialize(mContext)
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0) //위도 경도, 변수에 저장
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //지도에 표시를 하고 제목을 추가.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        //마커 위치로 지도 이동  // 위치가 변경이 된다면 따라서 움직여라.
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
                Toast.makeText(mContext,"위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun hasPermissions():Boolean{
        for(permission in PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(mContext,permission)!= PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
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
    fun removeLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }//어플이 종료되면 지도 요청 해제

    fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(mContext)
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

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
       mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }
}
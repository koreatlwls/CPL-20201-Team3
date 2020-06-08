package com.giggle.prototype

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.fragment_cur_loc_seeker.*
import kotlinx.android.synthetic.main.fragment_current_loc.*
import kotlinx.android.synthetic.main.memdetail.*
import java.io.IOException


class CurLocSeekerFragment: Fragment(),OnMapReadyCallback,ClusterManager.OnClusterItemClickListener<ShopItem>,ClusterManager.OnClusterClickListener<ShopItem> {
    private lateinit var rootView: View
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mContext: FragmentActivity
    private var auth: FirebaseAuth? = null
    private var isFabOpen = false

    //위치값 얻어오기 객체
    lateinit var locationRequest: LocationRequest

    //위치 요청
    lateinit var locationCallback: MyLocationCallback
    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var lntlng: LatLng
    private var jobadarray = mutableListOf<JobAd>()
    private lateinit var mClusterManager: ClusterManager<ShopItem>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_cur_loc_seeker, container, false)
        mapView = rootView.findViewById(R.id.mapViewSeeker)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return rootView
    }

    override fun onAttach(activity: Activity) {
        mContext = activity as FragmentActivity
        super.onAttach(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        btn_MyLocation_Seeker.setOnClickListener { OnMyLocationButtonClick() }
        btn_search_Seeker.setOnClickListener { searchLocation() }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpClusterer() {
        mClusterManager = ClusterManager<ShopItem>(mContext, mMap)
        mMap.setOnCameraIdleListener(mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)
        mClusterManager.setOnClusterItemClickListener(this)
        mClusterManager.setOnClusterClickListener(this)
    }

    private fun addItems(item: ShopItem) {
        mClusterManager.addItem(item)
    }

    companion object {
        fun newInstance(): CurrentLocFragment = CurrentLocFragment()
    }
    @Override
    override fun onMapReady(googleMap: GoogleMap) {
        //지도가 준비되었다면 호출.MapsInitializer.initialize(mContext)

        mMap = googleMap
        locationInit()
        val db = FirebaseFirestore.getInstance()
        setUpClusterer()
        lntlng=LatLng(32.0,130.0)
        db.collection("members").whereEqualTo("uid",auth?.uid).get()
            .addOnSuccessListener { result->
                for(document in result){
                    lntlng=LatLng(document.data["latitude"] as Double,document.data["longtitude"] as Double)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lntlng, 14f))
        }
        }
        db.collection("jobads").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val latitude = document.data["latitude"] as Double
                    val longtitude = document.data["longtitude"] as Double
                    val latlng = LatLng(latitude, longtitude)
                    val shopItem = ShopItem(
                        latlng,
                        document.data["shopname"].toString(),
                        document.data["fn"].toString()
                    )
                    addItems(shopItem)
                }
            }
        fun addLocationListener() {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
            //위치 권한을 요청해야 함.
            // 액티비티가 잠깐 쉴 때,
            // 자신의 위치를 확인하고, 갱신된 정보를 요청
        }
        //지도에 표시를 하고 제목을 추가.


        //마커 위치로 지도 이동  // 위치가 변경이 된다면 따라서 움직여라.
    }

    fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        //위치 권한을 요청해야 함.
        // 액티비티가 잠깐 쉴 때,
        // 자신의 위치를 확인하고, 갱신된 정보를 요청
    }

    fun OnMyLocationButtonClick() {
        when {
            hasPermissions() -> {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback, null
                )
                mMap.addMarker(MarkerOptions().position(lntlng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lntlng, 17f))
            }
            else -> {
                Toast.makeText(mContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun searchLocation() {
        lateinit var location: String
        location = txsearch.text.toString()
        var addressList: List<Address>? = null
        if (location == "") {
            Toast.makeText(mContext, "provide location", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(mContext)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val address = addressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MapsInitializer.initialize(mContext)
    }

    private fun hasPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
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

    override fun onClusterItemClick(p0: ShopItem?): Boolean {
        val ingshopname = p0?.title
        val nextIntent = Intent(mContext, AdDetailApply::class.java)
        nextIntent.putExtra("name", p0?.title)
        startActivity(nextIntent)
        return true
    }

    override fun onClusterClick(p0: Cluster<ShopItem>?): Boolean {
        val latlng=LatLng(p0?.position!!.latitude,p0!!.position.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
        return true
    }

}
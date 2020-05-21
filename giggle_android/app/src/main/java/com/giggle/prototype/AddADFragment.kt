package com.giggle.prototype

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
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
import kotlinx.android.synthetic.main.fragment_add_ad.*
import kotlinx.android.synthetic.main.fragment_current_loc.*
import java.io.IOException
import java.util.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class AddADFragment : Fragment(),OnMapReadyCallback {
    private lateinit var rootView:View
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mContext:FragmentActivity
    private var Location_finded:String="location"
    //위치값 얻어오기 객체
    lateinit var locationRequest: LocationRequest
    //위치 요청
    lateinit var locationCallback: MyLocationCallback
    val PERMISSIONS= arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var lntlng:LatLng
    val PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? =null //가게 사진1
    var photoUri1: Uri? =null //가게 사진2
    var photoUri2: Uri? =null //가게 사진3
    var photoUri3: Uri? =null //가게 사진4
    var photoUri4: Uri? =null //가게 사진5
    var photoCnt:Int =0
    var storage: FirebaseStorage? = null
    private var auth: FirebaseAuth? = null


    //위치값 얻어오기 객체


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView=inflater.inflate(R.layout.fragment_add_ad,container,false)
        mapView=rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        lntlng=LatLng(39.0, 39.0)

        return rootView
    }
    override fun onAttach(activity: Activity) {
        mContext = activity as FragmentActivity
        super.onAttach(activity)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        //지도가 준비되었다면 호출.MapsInitializer.initialize(mContext)

        mMap = googleMap
        locationInit()

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
                var addressList:List<Address>?=null
                val geoCoder=Geocoder(mContext)
                addressList=geoCoder.getFromLocation(lntlng.latitude,lntlng.longitude,1)
                Location_finded=addressList!![0].getAddressLine(0)
                Location_View.setText(Location_finded)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(lntlng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lntlng,17f))

            }
            else ->{
                Toast.makeText(mContext,"위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun OnSearchButtonClick(){
        searchLocation()
    }

    fun searchLocation(){


        lateinit var location:String
        location=edPosition.text.toString()
        var addressList:List<Address>?=null

        if(location==""){
            Toast.makeText(mContext,"provide location",Toast.LENGTH_SHORT).show()
        }
        else{
            val geoCoder= Geocoder(mContext)
            try{
                addressList=geoCoder.getFromLocationName(location,1)

            }catch(e: IOException){
                e.printStackTrace()
            }
            if(addressList!!.size==1) {
                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                Location_finded = address.getAddressLine(0)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng).title(location))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                Toast.makeText(
                    mContext,
                    address.latitude.toString() + " " + address.longitude,
                    Toast.LENGTH_LONG
                ).show()
                Location_View.setText(Location_finded)
            }
            else
            {
                Toast.makeText(mContext,"해당하는 위치 정보가 없습니다.",Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MapsInitializer.initialize(mContext)
    }
    private fun hasPermissions():Boolean{
        for(permission in PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(mContext,permission)!= PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }
    private fun requestPermissions(){


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
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view,savedInstanceState)
        btn_MyLocation1.setOnClickListener{OnMyLocationButtonClick()}
        btn_search1.setOnClickListener{OnSearchButtonClick()}
        //알바 시작 종료 numberpicker세팅
        val hourArray :Array<String> = arrayOf("01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24")
        val minuteArray :Array<String> =arrayOf("00","05","10","15","20","25","30","35","40","45","50","55")
        stDay.minValue=1
        stDay.maxValue=31
        stHour.minValue=1
        stHour.maxValue=24
        stHour.displayedValues = hourArray
        stMinute.minValue=1
        stMinute.maxValue=12
        stMinute.displayedValues = minuteArray
        fnDay.minValue=1
        fnDay.maxValue=31
        fnHour.minValue=1
        fnHour.maxValue=24
        fnHour.displayedValues = hourArray
        fnMinute.minValue=1
        fnMinute.maxValue=12
        fnMinute.displayedValues = minuteArray

        //오늘날짜로 초기화
        val instance = Calendar.getInstance()
        val day = Integer.parseInt(instance.get(Calendar.DAY_OF_MONTH).toString())
        stDay.value = day+1
        fnDay.value = day+1

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

        //초기화 버튼
        btReset.setOnClickListener({
            resetText()
        })

        //등록 버튼
        btRegister.setOnClickListener() {
            val shopname: String = edShopName.text.toString()//가게 이름
            val shopposition: String = Location_View.text.toString()
            val businessinfo: String = edBusinessInfo.text.toString()//업무 내용
            val priorityreq: String = edPriorityReq.text.toString()//우대 요건
            var hourlypay = 0 //시급
            var age1:Int = 0 //최소 나이
            var age2:Int = 0 //최대 나이
            var numperson:Int = 0
            var sex = ""//성별
            val stday = stDay.value //시작 날짜
            val sthour = stHour.value//시작 시간
            val stminute = stMinute.value*5//시작 분
            val fnday = fnDay.value//종료 날짜
            val fnhour = fnHour.value//종료 시간
            val fnminute = fnMinute.value*5//종료 분
            val st:String  = stday.toString() + "일" + sthour.toString() + "시" + stminute.toString() + "분"//알바시작
            val fn:String  = fnday.toString() + "일" + fnhour.toString() + "시" + fnminute.toString() + "분"//알바종료

            //입력값 확인
            if(edPerson.text.isNotEmpty()){
                numperson = Integer.parseInt(edPerson.text.toString())
            }
            if(edAge1.text.isNotEmpty()){
                age1 = Integer.parseInt(edAge1.text.toString())
            }
            if(edAge2.text.isNotEmpty()){
                age2 = Integer.parseInt(edAge2.text.toString())
            }
            if(edHourlyPay.text.isNotEmpty()){
                hourlypay = Integer.parseInt(edHourlyPay.text.toString())
            }
            if(shopname.isEmpty()||shopposition.isEmpty()||businessinfo.isEmpty()){
                edShopName.setBackgroundResource(R.drawable.red_edittext)
                edPosition.setBackgroundResource(R.drawable.red_edittext)
                edBusinessInfo.setBackgroundResource(R.drawable.red_edittext)
                Toast.makeText(mContext,"주요 항목들을 입력해주세요.", Toast.LENGTH_LONG).show()
            }
            if(hourlypay<8590){
                edHourlyPay.setBackgroundResource(R.drawable.red_edittext)
                Toast.makeText(mContext,"올해의 최저시급은 8590원 입니다.", Toast.LENGTH_LONG).show()
            }
            if(age2!=0&&age1>age2)
                Toast.makeText(mContext,"최소나이와 최대나이를 확인해주세요.", Toast.LENGTH_LONG).show()
            if (btMan.isChecked){
                sex = btMan.text.toString()
            }
            else if(btWomen.isChecked){
                sex = btWomen.text.toString()
            }

            //DB에 저장
            if(shopname.isNotEmpty()&&shopposition.isNotEmpty()&&businessinfo.isNotEmpty()&&hourlypay>8590) {
                jobAddb(
                    shopname,
                    shopposition,
                    businessinfo,
                    priorityreq,
                    hourlypay,
                    age1,
                    age2,
                    st,
                    fn,
                    sex,
                    numperson
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
    @RequiresApi(Build.VERSION_CODES.M)
    fun resetText(){
        edShopName.setText(null)
        edPosition.setText(null)
        edHourlyPay.setText(null)
        edBusinessInfo.setText(null)
        btMan.setChecked(false)
        btWomen.setChecked(false)
        edPriorityReq.setText(null)
        add_photo.setImageResource(android.R.drawable.ic_menu_crop)
        add_photo1.setImageResource(android.R.drawable.ic_menu_crop)
        add_photo2.setImageResource(android.R.drawable.ic_menu_crop)
        add_photo3.setImageResource(android.R.drawable.ic_menu_crop)
        add_photo4.setImageResource(android.R.drawable.ic_menu_crop)
        val instance = Calendar.getInstance()
        val day = Integer.parseInt(instance.get(Calendar.DAY_OF_MONTH).toString())
        stDay.value = day+1
        fnDay.value = day+1
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
        st:String,
        fn:String,
        sex:String,
        numperson:Int) {
        val db = FirebaseFirestore.getInstance()
        val jobad = JobAd(shopname,shopposition,businessinfo,priorityreq,hourlypay,age1,age2,sex,st,fn,numperson)
        db.collection("jobads").document(shopname).set(jobad) //DB에 shopname을 기준으로 저장
    }

    companion object {
        fun newInstance(): AddADFragment = AddADFragment()
    }



}
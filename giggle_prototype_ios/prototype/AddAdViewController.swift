//
//  AddAdViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/24.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import MobileCoreServices
import CoreLocation
import Firebase

class AddAdViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, CLLocationManagerDelegate {
    static var childView: UIViewController!
    static var drawed: Int!
    var errorDetected: Int!
    let queue = DispatchQueue(label: "AddAdViewController")
    
    @IBOutlet weak var scrollView: UIScrollView!
    
    //상점 정보 관련
    @IBOutlet weak var nameTextField: UITextField!
    @IBOutlet weak var typeTextField: UITextField!
    
    //ImageView 관련
    let empty_image = UIImage(named: "empty_profile_image.jpg")
    let imagePicker: UIImagePickerController! = UIImagePickerController()
    var captureImage: UIImage!
    var flagImageSave = false
    var currentImageViewIndex = 0
    var selectedImageViewIndex = 0
    @IBOutlet var shopImageCollection: [UIImageView]!
    var imageURLs = [URL]()
    
    //위치 관련
    @IBOutlet weak var inputTextField: UITextField!
    var latitude: Double!
    var longitude: Double!
    @IBOutlet weak var rangeTextField: UITextField!
    @IBOutlet weak var availableLabel: UILabel!
    var availableCount: Int!
    var tokens: [String]!
    var emails: [String]!
    
    //모집 분야&인원 필드 추가 관련
    @IBOutlet weak var inputFieldStepper: UIStepper!
    @IBOutlet weak var inputFieldStackView: UIStackView!
    @IBOutlet weak var parentInputFieldStackView: UIStackView!
    var currentInputFieldCount = 1.0
    var minimumInputFieldCount = 1.0
    
    //근무 정보 관련
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var adTitleTextField: UITextField!
    @IBOutlet weak var startPicker: UIDatePicker!
    @IBOutlet weak var endPicker: UIDatePicker!
    @IBOutlet weak var wageTextField: UITextField!
    @IBOutlet weak var workDetailTextView: UITextView!
    var hour = 0
    var minute = 0
    @IBOutlet weak var expectedTotalWageLabel: UILabel!
    
    //기타 우대사항 관련
    @IBOutlet weak var genderSegmentedControl: UISegmentedControl!
    @IBOutlet weak var minAgeTextField: UITextField!
    @IBOutlet weak var maxAgeTextField: UITextField!
    @IBOutlet weak var preferenceTextView: UITextView!
    @IBOutlet weak var ageSegmentedControl: UISegmentedControl!
    
    // 광고 등록 관련
    @IBOutlet weak var addButton: UIButton!
    @IBOutlet weak var initButton: UIButton!
    
    var tempDocID: String!
    var notificationType: String!
    
    @IBAction func searchAvailablePeople(_ sender: UIButton) {
        availableCount = 0
        let db = Firestore.firestore()
        tokens = [String]()
        emails = [String]()
        db.collection("UserData").whereField("member_state", isEqualTo: 1).getDocuments() {
            (querySnapshot, err) in
            let userCount = querySnapshot!.documents.count
            for index in 0..<userCount {
                let data = querySnapshot!.documents[index].data()
                if (data["email"] as! String) == LoginViewController.user.email {
                    continue
                }
                let user_lat = data["lat"] as! Double
                let user_lng = data["lng"] as! Double
                if self.latitude == nil || self.longitude == nil {
                    CommonFuncs.showToast(message: "위치를 설정해주세요.", view: self.view)
                    continue
                }
                if user_lat == -1 && user_lng == -1 {
                    continue
                }
                let dist = self.calcDistBetweenTwoPoint(lat1: self.latitude, lng1: self.longitude, lat2: data["lat"] as! Double, lng2: data["lng"] as! Double)
                let range_text: String = self.rangeTextField.text!
                if range_text == "" {
                    CommonFuncs.showToast(message: "구인범위를 설정해주세요.", view: self.view)
                    continue
                }
                let range_double = Double(range_text)
                if dist.isLessThanOrEqualTo(range_double ?? -1) {
                    self.availableCount += 1
                    self.tokens.append(data["fcmToken"] as! String)
                    self.emails.append(data["email"] as! String)
                }
            }
            self.updateAvailableLabel()
        }
    }
    
    func updateAvailableLabel() {
        availableLabel.text = "\(String(availableCount))명의 구직자가 범위 내에 있습니다."
    }
    
    func calcDistBetweenTwoPoint(lat1: Double, lng1: Double, lat2: Double, lng2: Double) -> Double {
        return acos(cos(degree2radian(90-lat1)) * cos(degree2radian(90-lat2)) + sin(degree2radian(90-lat1)) * sin(degree2radian(90-lat2)) * cos(degree2radian(lng1-lng2))) * 6378.137
    }
    
    func degree2radian(_ degree: Double) -> Double {
        return degree * .pi / 180
    }
    
    @IBAction func setCurrentLocation(_ sender: UIButton) {
        latitude = AAV_MapContainerViewController.mapView.camera.target.latitude
        longitude = AAV_MapContainerViewController.mapView.camera.target.longitude
        CommonFuncs.showToast(message: "지정한 위치로 설정되었습니다.", view: self.view)
    }
    
    @IBAction func addAd(_ sender: UIButton) {
        errorDetected = 0
        
        let warning = UIAlertController(title: "필수 항목 미입력", message: "", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .default)
        warning.addAction(okAction)
        if isTextFieldEmpty(nameTextField) {
            warning.message = "상점 이름 항목을 입력해주세요."
            errorDetected = 1
        }
        else if isTextFieldEmpty(typeTextField) {
            warning.message = "상점 업종 항목을 입력해주세요."
            errorDetected = 1
        }
        else if currentImageViewIndex == 0 {
            warning.message = "상점 이미지를 1개 이상 업로드해주세요."
            errorDetected = 1
        }
        else if latitude == nil || longitude == nil {
            warning.message = "상점 위치를 설정해주세요."
            errorDetected = 1
        }
        else if isTextFieldEmpty(rangeTextField) {
            warning.message = "구인범위를 설정해주세요."
            errorDetected = 1
        }
        else {
            for index in 0..<Int(currentInputFieldCount) {
                if isTextFieldEmpty(parentInputFieldStackView.subviews[index+1].subviews[0].subviews[1] as! UITextField) {
                    warning.message = "모집 분야를 입력해주세요."
                    errorDetected = 1
                    break
                }
                else if isTextFieldEmpty(parentInputFieldStackView.subviews[index+1].subviews[1].subviews[1] as! UITextField) {
                    warning.message = "모집 인원을 입력해주세요."
                    errorDetected = 1
                    break
                }
            }
            
            if errorDetected == 0 {
                let curDate = Date()
                let startTime = startPicker.date
                let endTime = endPicker.date
                if isTextFieldEmpty(adTitleTextField) {
                    warning.message = "광고 제목을 입력해주세요."
                    errorDetected = 1
                }
                else {
                    if Int(startTime.timeIntervalSince(curDate)) <= 0 {
                        warning.message = "시작 시각이 현재 시각보다 늦습니다. 시작 시각을 재설정해주세요."
                        errorDetected = 1
                    }
                    else if Int(endTime.timeIntervalSince(curDate)) <= 0 {
                        warning.message = "종료 시각이 현재 시각보다 늦습니다. 종료 시각을 재설정해주세요."
                        errorDetected = 1
                    }
                    if Int(endTime.timeIntervalSince(startTime)) <= 0 {
                        warning.message = "종료 시각이 시작 시각보다 빠릅니다. 종료 시각을 재설정해주세요."
                        errorDetected = 1
                    }
                }
            }
            
            if errorDetected == 0 {
                if isTextFieldEmpty(wageTextField) {
                    warning.message = "시급을 입력해주세요."
                    errorDetected = 1
                }
                else if isTextViewEmpty(workDetailTextView) {
                    warning.message = "근무 내용을 입력해주세요."
                    errorDetected = 1
                }
                else if ageSegmentedControl.selectedSegmentIndex == 0 {
                    if isTextFieldEmpty(minAgeTextField) {
                        warning.message = "최소 나이를 입력해주세요."
                        errorDetected = 1
                    }
                    else if isTextFieldEmpty(maxAgeTextField) {
                        warning.message = "최대 나이를 입력해주세요."
                        errorDetected = 1
                    }
                    else if (Int(minAgeTextField.text ?? "0") ?? 0) <= 0 {
                        warning.message = "최소 나이가 비정상적으로 설정되었습니다. 다시 입력해주세요."
                        errorDetected = 1
                    }
                    else if (Int(maxAgeTextField.text ?? "-1") ?? -1) == -1 {
                        warning.message = "최대 나이가 비정상적으로 설정되었습니다. 다시 입력해주세요."
                        errorDetected = 1
                    }
                    else if (Int(minAgeTextField.text ?? "0") ?? 0) > (Int(maxAgeTextField.text ?? "0") ?? 0) {
                        warning.message = "최대 나이가 최소 나이보다 작게 설정되었습니다. 다시 입력해주세요."
                        errorDetected = 1
                    }
                }
            }
        }
        
        if errorDetected == 1 {
            self.present(warning, animated: true, completion: nil)
        }
        else {
            let checkAlert = UIAlertController(title: "광고 등록", message: "이상의 내용으로 광고를 등록하시겠습니까?", preferredStyle: .alert)
            let yesAction = UIAlertAction(title: "등록", style: .default) {
                (action) in
                //저장 데이터 가공
                self.queue.sync {
                    var minAge: Int!
                    var maxAge: Int!
                    if self.ageSegmentedControl.selectedSegmentIndex == 1 {
                        minAge = -1
                        maxAge = -1
                    }
                    else {
                        minAge = Int(self.minAgeTextField.text!)!
                        maxAge = Int(self.maxAgeTextField.text!)!
                    }
                    let range = Int(self.rangeTextField.text!)!
                    let dateformatter = DateFormatter()
                    dateformatter.dateFormat = "yyyyMMdd HH:mm"
                    //DB 저장 작업
                    let db = Firestore.firestore()
                    var ref: DocumentReference? = nil
                    ref = db.collection("AdData").addDocument(data: [
                        "Uploader": LoginViewController.user.email!,
                        "state": 0,
                        "name": self.nameTextField.text!,
                        "type": self.typeTextField.text!,
                        "latitude": self.latitude!,
                        "longitude": self.longitude!,
                        "range": range,
                        "adTitle": self.adTitleTextField.text!,
                        "startTime": dateformatter.string(from: self.startPicker.date),
                        "endTime": dateformatter.string(from: self.endPicker.date),
                        "wage": Int(self.wageTextField.text!)!,
                        "workDetail": self.workDetailTextView.text!,
                        "preferGender": self.genderSegmentedControl.selectedSegmentIndex,
                        "preferMinAge": minAge!,
                        "preferMaxAge": maxAge!,
                        "preferInfo": self.preferenceTextView.text!,
                        "fieldCount": self.currentInputFieldCount,
                        "imageCount": self.currentImageViewIndex
                    ]) { err in
                        if let err = err {
                            print("Error adding document: \(err)")
                        } else {
                            print("Document added with ID: \(ref!.documentID)")
                        }
                    }
                    self.tempDocID = ref!.documentID
                    for index in 0..<Int(self.currentInputFieldCount) {
                        db.collection("AdData").document(ref!.documentID).updateData([
                            "field"+String(index+1): (self.parentInputFieldStackView.subviews[index+1].subviews[0].subviews[1] as! UITextField).text!,
                            "numberOfPeople"+String(index+1): Int((self.parentInputFieldStackView.subviews[index+1].subviews[1].subviews[1] as! UITextField).text!)!
                        ])
                    }
                    let storage = Storage.storage()
                    let storageRef = storage.reference()
                    for index in 0..<self.currentImageViewIndex {
                        let imageRef = storageRef.child("Ad/" + ref!.documentID + "/shop_image_" + String(index+1) + ".jpg")
                        let metadata = StorageMetadata()
                        metadata.contentType = "image/jpeg"
                        
                        let uploadTask = imageRef.putFile(from: self.imageURLs[index], metadata: metadata)
                        uploadTask.observe(.failure) { snapshot in
                            if let error = snapshot.error as NSError? {
                                switch (StorageErrorCode(rawValue: error.code)!) {
                                case .objectNotFound:
                                    print("object Not Found")
                                    break
                                case .unauthorized:
                                    print("unauthorized")
                                    break
                                case .cancelled:
                                    print("cancelled")
                                    break
                                case .unknown:
                                    print("unknown")
                                    break
                                default:
                                    print("default: retry to upload")
                                    break
                                }
                            }
                        }
                    }
                }
                self.queue.sync {
                    self.sendPost()
                    self.initForm()
                }
            }
            let noAction = UIAlertAction(title: "취소", style: .default) {
                (action) in
            }
            checkAlert.addAction(yesAction)
            checkAlert.addAction(noAction)
            self.present(checkAlert, animated: true, completion: nil)
        }
    }
    
    @IBAction func locationSearch(_ sender: UIButton) {
        let vc = AAV_MapContainerViewController(nibName: "AAV_MapContainerViewController", bundle: nil)
        vc.searchLocation(address: inputTextField.text ?? "")
        latitude = AAV_MapContainerViewController.mapView.camera.target.latitude
        longitude = AAV_MapContainerViewController.mapView.camera.target.longitude
    }
    
    @IBAction func adjustInputField(_ sender: UIStepper) {
        if sender.value > currentInputFieldCount {
            do {
                let newInputFieldStackView = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(NSKeyedArchiver.archivedData(withRootObject: inputFieldStackView!, requiringSecureCoding: false)) as! UIStackView
                let index = parentInputFieldStackView.subviews.count
                parentInputFieldStackView.insertArrangedSubview(newInputFieldStackView, at: index)
                (parentInputFieldStackView.subviews[index].subviews[0].subviews[1] as! UITextField).text = ""
                (parentInputFieldStackView.subviews[index].subviews[1].subviews[1] as! UITextField).text = ""
                (parentInputFieldStackView.subviews[index].subviews[0].subviews[1] as! UITextField).delegate = self
                (parentInputFieldStackView.subviews[index].subviews[1].subviews[1] as! UITextField).delegate = self
                CommonFuncs.setTextFieldUI(textField: (parentInputFieldStackView.subviews[index].subviews[0].subviews[1] as! UITextField), offset: -5)
                CommonFuncs.setTextFieldUI(textField: (parentInputFieldStackView.subviews[index].subviews[1].subviews[1] as! UITextField), offset: -5)
                currentInputFieldCount = sender.value
            } catch {
                print(error)
            }
        }
        else {
            parentInputFieldStackView.subviews[parentInputFieldStackView.subviews.count - 1].removeFromSuperview()
            currentInputFieldCount = sender.value
        }
    }
    
    @IBAction func setAgeInput(_ sender: UISegmentedControl) {
        if sender.selectedSegmentIndex == 0 {
            minAgeTextField.isUserInteractionEnabled = true
            minAgeTextField.backgroundColor = UIColor.white
            maxAgeTextField.isUserInteractionEnabled = true
            maxAgeTextField.backgroundColor = UIColor.white
        }
        else {
            minAgeTextField.isUserInteractionEnabled = false
            minAgeTextField.backgroundColor = UIColor.lightGray
            maxAgeTextField.isUserInteractionEnabled = false
            maxAgeTextField.backgroundColor = UIColor.lightGray
        }
    }
    
    @objc func touchImageView_0() {
        touchImageView_actual(index: 0)
    }
    
    @objc func touchImageView_1() {
        touchImageView_actual(index: 1)
    }
    
    @objc func touchImageView_2() {
        touchImageView_actual(index: 2)
    }
    
    func touchImageView_actual(index: Int) {
        selectedImageViewIndex = index
        print(selectedImageViewIndex)
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "사진 보기", style: .default, handler: { (action) in
            self.performSegue(withIdentifier: "CallImageViewSegue", sender: nil)
        }))
        alert.addAction(UIAlertAction(title: "사진 변경하기", style: .default, handler: { (action) in
            self.flagImageSave = false
            
            self.imagePicker.delegate = self
            self.imagePicker.sourceType = .photoLibrary
            self.imagePicker.mediaTypes = [kUTTypeImage as String]
            self.imagePicker.allowsEditing = true
            
            self.present(self.imagePicker, animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "취소", style: .destructive, handler: nil))
        present(alert, animated: true)
    }
    
    @IBAction func checkInitForm(_ sender: UIButton) {
        let check = UIAlertController(title: "입력 내용 초기화", message: "입력한 내용을 초기화하겠습니까?", preferredStyle: .alert)
        let yesAction = UIAlertAction(title: "초기화", style: .default, handler: { (action) in
            self.initForm()
            CommonFuncs.showToast(message: "모든 입력 항목을 초기화했습니다.", view: self.view)
        })
        let noAction = UIAlertAction(title: "취소", style: .default, handler: { (action) in })
        check.addAction(yesAction)
        check.addAction(noAction)
        self.present(check, animated: true, completion: nil)
    }
    
    private func initForm()
    {
        self.nameTextField.text = ""
        self.typeTextField.text = ""
        for index in 0..<self.currentImageViewIndex {
            self.shopImageCollection[index].image = self.empty_image
        }
        self.currentImageViewIndex = 0
        self.selectedImageViewIndex = 0
        self.imageURLs.removeAll()
        self.latitude = nil
        self.longitude = nil
        self.rangeTextField.text = ""
        (self.parentInputFieldStackView.subviews[1].subviews[0].subviews[1] as! UITextField).text = ""
        (self.parentInputFieldStackView.subviews[1].subviews[1].subviews[1] as! UITextField).text = ""
        if self.currentInputFieldCount >= 2 {
            for _ in 2...Int(self.currentInputFieldCount) {
                self.parentInputFieldStackView.subviews[2].removeFromSuperview()
            }
        }
        self.currentInputFieldCount = 1.0
        self.adTitleTextField.text = ""
        self.startPicker.date = Date()
        self.endPicker.date = Date()
        self.wageTextField.text = ""
        self.workDetailTextView.text = ""
        self.genderSegmentedControl.selectedSegmentIndex = 0
        self.minAgeTextField.text = ""
        self.maxAgeTextField.text = ""
        self.ageSegmentedControl.selectedSegmentIndex = 0
        self.setAgeInput(self.ageSegmentedControl)
        self.preferenceTextView.text = ""
        self.inputTextField.text = ""
    }
    
    private func isTextFieldEmpty(_ textField: UITextField) -> Bool
    {
        if (textField.text ?? "") == "" {
            return true
        }
        return false
    }
    
    private func isTextViewEmpty(_ textView: UITextView) -> Bool
    {
        if (textView.text ?? "") == "" {
            return true
        }
        return false
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        let mediaType = info[UIImagePickerController.InfoKey.mediaType] as! NSString
        
        if mediaType.isEqual(to: kUTTypeImage as NSString as String) {
            captureImage = (info[.originalImage] as! UIImage)
            
            let imageURL = info[.imageURL] as? URL
            if (currentImageViewIndex < 3) {
                imageURLs.append(imageURL!)
                shopImageCollection[currentImageViewIndex].image = captureImage
                currentImageViewIndex += 1
            } else {
                shopImageCollection[selectedImageViewIndex].image = captureImage
                imageURLs[selectedImageViewIndex] = imageURL!
            }
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        print("view will appear")
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("view did load")
        //ImageView Gesture 등록
        for index in 0..<shopImageCollection.count {
            shopImageCollection[index].image = empty_image
            shopImageCollection[index].isUserInteractionEnabled = true
        }
        let tapGesture_0 = UITapGestureRecognizer(target: self, action: #selector(touchImageView_0))
        shopImageCollection[0].addGestureRecognizer(tapGesture_0)
        let tapGesture_1 = UITapGestureRecognizer(target: self, action: #selector(touchImageView_1))
        shopImageCollection[1].addGestureRecognizer(tapGesture_1)
        let tapGesture_2 = UITapGestureRecognizer(target: self, action: #selector(touchImageView_2))
        shopImageCollection[2].addGestureRecognizer(tapGesture_2)
        
        //Stepper 최소값 설정
        inputFieldStepper.minimumValue = minimumInputFieldCount
        
        //TextView UI 설정
        workDetailTextView.layer.cornerRadius = 6
        workDetailTextView.layer.borderWidth = 1
        workDetailTextView.layer.borderColor = UIColor.lightGray.cgColor
        
        preferenceTextView.layer.cornerRadius = 6
        preferenceTextView.layer.borderWidth = 1
        preferenceTextView.layer.borderColor = UIColor.lightGray.cgColor
        
        // 버튼 UI 설정
        addButton.layer.cornerRadius = 6
        initButton.layer.cornerRadius = 6
        
        //TextField Delegate 설정
        nameTextField.delegate = self
        typeTextField.delegate = self
        (self.parentInputFieldStackView.subviews[1].subviews[0].subviews[1] as! UITextField).delegate = self
        (self.parentInputFieldStackView.subviews[1].subviews[1].subviews[1] as! UITextField).delegate = self
        wageTextField.delegate = self
        minAgeTextField.delegate = self
        maxAgeTextField.delegate = self
        inputTextField.delegate = self
        rangeTextField.delegate = self
        
        //TextView Delegate 설정
        workDetailTextView.delegate = self
        preferenceTextView.delegate = self
        
        addKeyboardNotification()
        
        //ScrollView Gesture 추가
        let singleTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(hideKeyboard))
        scrollView.addGestureRecognizer(singleTapGestureRecognizer)
        
        AddAdViewController.childView = self.children[0]
        
        //DatePicker 설정
        startPicker.minimumDate = Date()
        endPicker.minimumDate = Date()
        
        availableLabel.numberOfLines = 0
        availableLabel.lineBreakMode = .byWordWrapping
        
        AddAdViewController.drawed = 0
        
        startPicker.addTarget(self, action: #selector(datePickerValueChanged), for: .valueChanged)
        endPicker.addTarget(self, action: #selector(datePickerValueChanged), for: .valueChanged)
    }
    
    @objc func datePickerValueChanged() {
        let sec = endPicker.date.timeIntervalSince(startPicker.date)
        print(sec)
        var sec_int = Int(sec)
        print(sec_int)
        hour = sec_int / 3600
        sec_int %= 3600
        minute = sec_int/60
        timeLabel.text = "\(String(hour)) 시간 \(String(minute)) 분"
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
     
        print("view did layout subviews")
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if AddAdViewController.drawed == 0 {
            //TextField UI 설정
            CommonFuncs.setTextFieldUI(textField: nameTextField, offset: 10)
            CommonFuncs.setTextFieldUI(textField: typeTextField, offset: 10)
            CommonFuncs.setTextFieldUI(textField: inputFieldStackView.subviews[0].subviews[1] as! UITextField, offset: 10)
            CommonFuncs.setTextFieldUI(textField: inputFieldStackView.subviews[1].subviews[1] as! UITextField, offset: 10)
            CommonFuncs.setTextFieldUI(textField: wageTextField, offset: 0)
            wageTextField.textAlignment = .right
            CommonFuncs.setTextFieldUI(textField: adTitleTextField, offset: 10)
            CommonFuncs.setTextFieldUI(textField: minAgeTextField, offset: 0)
            CommonFuncs.setTextFieldUI(textField: maxAgeTextField, offset: 0)
            CommonFuncs.setTextFieldUI(textField: inputTextField, offset: 8)
            CommonFuncs.setTextFieldUI(textField: rangeTextField, offset: 2)
            AddAdViewController.drawed = 1
        }
    }
    
    @objc private func hideKeyboard(_ sender: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }
    
    func sendPost() {
        let db = Firestore.firestore()
        for index in 0..<tokens.count {
            //db 등록
            let email = emails[index]
            print(email)
            db.collection("UserData").whereField("email", isEqualTo: email).getDocuments {
                (querySnapshot, err) in
                let document = querySnapshot?.documents[0]
                db.collection("UserData").document(document?.documentID ?? "").collection("ReceivedAd").addDocument(data: [
                    "docID": self.tempDocID ?? "",
                    "state": 0
                ])
            }
            //message 전송
            let token = tokens[index]
            notificationType = "add"
            let param = [
                "to": token,
                "data": [
                    "adTitle": adTitleTextField.text,
                    "wage": wageTextField.text,
                    "detail": workDetailTextView.text,
                    "docID": tempDocID,
                    "type": notificationType
                ]
                ] as [String : Any] as [String : Any]
            let paramData = try! JSONSerialization.data(withJSONObject: param, options: [])
            
            let url = URL(string: "https://fcm.googleapis.com/fcm/send")
            var request = URLRequest(url: url!)
            request.httpMethod = "POST"
            let db = Firestore.firestore()
            var serverKey = ""
            db.collection("ServerKey").getDocuments() {
                (querySnapshot, err) in
                let document = querySnapshot?.documents[0]
                let data = document?.data()
                serverKey = data!["serverKey"] as! String
                
                request.allHTTPHeaderFields = [
                    "Content-Type": "application/json",
                    "Authorization": "key=\(serverKey)"
                ]
                request.httpBody = paramData
                
                let task = URLSession.shared.dataTask(with: request) {
                    (data, response, error) in
                    guard let data = data, error == nil else {
                        print("error=\(String(describing: error))")
                        return
                    }
                    
                    if let httpStatus = response as? HTTPURLResponse, httpStatus.statusCode != 200 {
                        print("statusCode should be 200, but is \(httpStatus.statusCode)")
                        print("response = \(String(describing: response))")
                    }
                    
                    let responseString = String(data: data, encoding: .utf8)
                    print("responseString = \(String(describing: responseString))")
                }
                
                task.resume()
            }
        }
    }
    
    private func addKeyboardNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc private func keyboardWillShow(_ notification: Notification) {
        if let keyboardFrame: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            let contentInsets = UIEdgeInsets(top: 0.0, left: 0.0, bottom: keyboardFrame.cgRectValue.height - 80, right: 0.0)
            scrollView.contentInset = contentInsets
            scrollView.scrollIndicatorInsets = contentInsets
        }
    }
    
    @objc private func keyboardWillHide(_ notification: Notification) {
        let contentInsets = UIEdgeInsets.zero
        scrollView.contentInset = contentInsets
        scrollView.scrollIndicatorInsets = contentInsets
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.scrollView.endEditing(true)
    }
}

extension AddAdViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        
        return true
    }
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        var scrollViewYOffset = 0
        if textField == nameTextField {
            scrollViewYOffset = 0
        }
        else if textField == typeTextField {
            scrollViewYOffset = 50
        }
        else if textField == wageTextField {
            scrollViewYOffset = 630 + (Int(self.currentInputFieldCount)-1)*100
        }
        else if textField == minAgeTextField || textField == maxAgeTextField {
            scrollViewYOffset = 981 + (Int(self.currentInputFieldCount)-1)*100
        }
        else if textField == inputTextField {
            scrollViewYOffset = 1270 + (Int(self.currentInputFieldCount)-1)*100
        }
        else if textField == rangeTextField {
            scrollViewYOffset = 1300 + (Int(self.currentInputFieldCount)-1)*100
        }
        else {
            for index in 0..<Int(self.currentInputFieldCount) {
                print(index)
                if textField == (self.parentInputFieldStackView.subviews[index+1].subviews[0].subviews[1] as! UITextField) {
                    scrollViewYOffset = 340 + index*100
                    break
                }
                else if textField ==
                    (self.parentInputFieldStackView.subviews[index+1].subviews[1].subviews[1] as! UITextField) {
                    scrollViewYOffset = 390 + index*100
                    break
                }
            }
        }
        scrollView.setContentOffset(CGPoint(x: 0, y: scrollViewYOffset), animated: true)
        return true
    }
    
    @IBAction func calculateExpectedWage () {
        if let wage_str = wageTextField.text, let wage_int = Int(wage_str) {
                var expected = hour*wage_int
                expected += wage_int / 60 * minute
                expectedTotalWageLabel.text = "예상 총 급여 : \(String(expected)) 원"
        }
    }
}

extension AddAdViewController: UITextViewDelegate {
    func textViewShouldBeginEditing(_ textView: UITextView) -> Bool {
        var scrollViewYOffset = 0
        if textView == workDetailTextView {
            scrollViewYOffset = 725 + (Int(self.currentInputFieldCount)-1)*100
        }
        else if textView == preferenceTextView {
            scrollViewYOffset = 1076 + (Int(self.currentInputFieldCount)-1)*100
        }
        scrollView.setContentOffset(CGPoint(x: 0, y: scrollViewYOffset), animated: true)
        return true
    }
}

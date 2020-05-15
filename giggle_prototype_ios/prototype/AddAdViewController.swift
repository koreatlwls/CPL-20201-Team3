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
    var errorDetected: Int!
    
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
    
    //모집 분야&인원 필드 추가 관련
    @IBOutlet weak var inputFieldStepper: UIStepper!
    @IBOutlet weak var inputFieldStackView: UIStackView!
    @IBOutlet weak var parentInputFieldStackView: UIStackView!
    var currentInputFieldCount = 1.0
    var minimumInputFieldCount = 1.0
    
    //근무 정보 관련
    @IBOutlet weak var startDatePicker: UIDatePicker!
    @IBOutlet weak var endDatePicker: UIDatePicker!
    @IBOutlet weak var wageTextField: UITextField!
    @IBOutlet weak var workInfoTextView: UITextView!
    
    //기타 우대사항 관련
    @IBOutlet weak var genderSegmentedControl: UISegmentedControl!
    @IBOutlet weak var minAgeTextField: UITextField!
    @IBOutlet weak var maxAgeTextField: UITextField!
    @IBOutlet weak var preferenceTextView: UITextView!
    @IBOutlet weak var ageSegmentedControl: UISegmentedControl!
    
    // 광고 등록 관련
    @IBOutlet weak var addButton: UIButton!
    @IBOutlet weak var initButton: UIButton!
    
    
    @IBAction func setCurrentLocation(_ sender: UIButton) {
        latitude = AAV_MapContainerViewController.mapView.camera.target.latitude
        longitude = AAV_MapContainerViewController.mapView.camera.target.longitude
        showToast(message: "지정한 위치로 설정되었습니다.")
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
                let dateformatter = DateFormatter()
                dateformatter.dateStyle = .short
                dateformatter.timeStyle = .short
                
                let curDate = Date()
                let startDate = startDatePicker.date
                let endDate = endDatePicker.date
                
                if Int(startDate.timeIntervalSince(curDate)) <= 0 {
                    warning.message = "시작 시각이 현재 시각보다 늦습니다. 시작 시각을 재설정해주세요."
                    errorDetected = 1
                }
                else if Int(endDate.timeIntervalSince(curDate)) <= 0 {
                    warning.message = "종료 시각이 현재 시각보다 늦습니다. 종료 시각을 재설정해주세요."
                    errorDetected = 1
                }
                else if Int(endDate.timeIntervalSince(startDate)) <= 0 {
                    warning.message = "종료 시각이 시작 시각보다 빠릅니다. 종료 시각을 재설정해주세요."
                    errorDetected = 1
                }
            }
            
            if errorDetected == 0 {
                if isTextFieldEmpty(wageTextField) {
                    warning.message = "시급을 입력해주세요."
                    errorDetected = 1
                }
                else if isTextViewEmpty(workInfoTextView) {
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
                let dateformatter = DateFormatter()
                dateformatter.dateStyle = .short
                dateformatter.timeStyle = .short
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
                    "startDate": dateformatter.string(from: self.startDatePicker.date),
                    "endDate": dateformatter.string(from: self.endDatePicker.date),
                    "wage": Int(self.wageTextField.text!)!,
                    "workInfo": self.workInfoTextView.text!,
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
                let content = UNMutableNotificationContent()
                content.title = "구인 광고 알림"
                content.subtitle = "근처의 가게에서 구인 광고 알림이 도착했습니다."
                content.body = ""
                content.badge = 1
                
                self.initForm()
                
                let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 5, repeats: false)
                
                let request = UNNotificationRequest(identifier: "NotifyNewAd", content: content, trigger: trigger)
                
                UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
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
                
                parentInputFieldStackView.insertArrangedSubview(newInputFieldStackView, at: parentInputFieldStackView.subviews.count)
                (parentInputFieldStackView.subviews[parentInputFieldStackView.subviews.count-1].subviews[0].subviews[1] as! UITextField).text = ""
                (parentInputFieldStackView.subviews[parentInputFieldStackView.subviews.count-1].subviews[1].subviews[1] as! UITextField).text = ""
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
            self.showToast(message: "모든 입력 항목을 초기화했습니다.")
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
        self.startDatePicker.date = Date()
        self.endDatePicker.date = Date()
        self.wageTextField.text = ""
        self.workInfoTextView.text = ""
        self.genderSegmentedControl.selectedSegmentIndex = 0
        self.minAgeTextField.text = ""
        self.maxAgeTextField.text = ""
        self.ageSegmentedControl.selectedSegmentIndex = 0
        self.setAgeInput(self.ageSegmentedControl)
        self.preferenceTextView.text = ""
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
        super.viewWillAppear(true)
        
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
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
        workInfoTextView.layer.cornerRadius = 6
        workInfoTextView.layer.borderWidth = 1
        workInfoTextView.layer.borderColor = UIColor.black.cgColor
        
        preferenceTextView.layer.cornerRadius = 6
        preferenceTextView.layer.borderWidth = 1
        preferenceTextView.layer.borderColor = UIColor.black.cgColor
        
        // 버튼 UI 설정
        addButton.layer.cornerRadius = 6
        initButton.layer.cornerRadius = 6
        
        AddAdViewController.childView = self.children[0]
    }
    
    func showToast(message: String) {
        let width_variable: CGFloat = 10
        let toastLabel = UILabel(frame: CGRect(x: width_variable, y: self.view.frame.size.height-100, width: view.frame.size.width-2*width_variable, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center;
        toastLabel.font = UIFont(name: "Montserrat-Light", size: 12.0)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.clipsToBounds = true
        toastLabel.layer.cornerRadius = 6
        self.view.addSubview(toastLabel)
        UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: { toastLabel.alpha = 0.0 }, completion: {(isCompleted) in toastLabel.removeFromSuperview()})
    }
}

//
//  OngoingAdViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/06/02.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class UploadedAdListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UIPickerViewDataSource, UIPickerViewDelegate, UITextFieldDelegate {
    
    var adsResult: [Ad]!
    @IBOutlet weak var uploadedAdTableView: UITableView!
    @IBOutlet weak var statePickerView: UIPickerView!
    @IBOutlet weak var dateTextField: UITextField!
    @IBOutlet weak var searchButton: UIButton!
    
    var selected = "전체"
    var dateString: String!
    
    let states = ["전체", "진행중", "마감"]
    
    @IBAction func adSearch(_ sender: UIButton) {
        //0 : 채용 진행중
        //1 : 채용 마감
        if dateTextField.text?.count == 8 || dateTextField.text?.count == 0 {
            switch selected {
            case "전체":
                if dateTextField.text == "" {
                    updateStatedAds(inp_state: -1)
                }
                else {
                    updateStatedAdsByDate(inp_state: -1, date: dateTextField.text ?? "")
                }
            case "진행중":
                if dateTextField.text == "" {
                    updateStatedAds(inp_state: 0)
                }
                else {
                    updateStatedAdsByDate(inp_state: 0, date: dateTextField.text ?? "")
                }
            case "마감":
                if dateTextField.text == "" {
                    updateStatedAds(inp_state: 1)
                }
                else {
                    updateStatedAdsByDate(inp_state: 1, date: dateTextField.text ?? "")
                }
            default:
                break
            }
        }
        else {
            CommonFuncs.showToast(message: "날짜 형식이 맞지 않습니다.", view: self.view)
        }
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentCharacterCount = textField.text?.count ?? 0
        if range.length + range.location > currentCharacterCount {
            return false
        }
        let newLength = currentCharacterCount + string.count - range.length
        return newLength <= 8
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return states.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return states[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        selected = states[row]
    }
    
    func checkApplicants() {
        self.performSegue(withIdentifier: "checkApplicantsSegue", sender: nil)
    }
    
    func finishAd() {
        let finish_alert = UIAlertController(title: "광고 마감", message: "해당 광고의 구인을 마감하시겠습니까?", preferredStyle: .alert)
        let ok_action = UIAlertAction(title: "확인", style: .default, handler: {
            (action) in
            let db = Firestore.firestore()
            ShowAdViewController.selectedAd.state = 1
            db.collection("AdData").document(ShowAdViewController.selectedAd.docID).updateData(["state": 1])
            let complete_alert = UIAlertController(title: "광고 마감", message: "해당 광고의 구인을 마감하였습니다.", preferredStyle: .alert)
            let complete_ok_action = UIAlertAction(title: "확인", style: .default, handler: nil)
            complete_alert.addAction(complete_ok_action)
            self.present(complete_alert, animated: true, completion: nil)
        })
        let no_action = UIAlertAction(title: "취소", style: .cancel, handler: nil)
        finish_alert.addAction(ok_action)
        finish_alert.addAction(no_action)
        
        self.present(finish_alert, animated: true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.backgroundColor = .clear
        navigationItem.title = "구인 중인 광고 목록"
        
        uploadedAdTableView.dataSource = self
        uploadedAdTableView.delegate = self
        
        adsResult = [Ad]()
        
        statePickerView.dataSource = self
        statePickerView.delegate = self
        
        dateTextField.placeholder = "ex) 20200604"
        dateTextField.delegate = self
        CommonFuncs.setTextFieldUI(textField: dateTextField, offset: 10)
        
        updateStatedAds(inp_state: -1)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("tableView : ", adsResult.count)
        return adsResult.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = uploadedAdTableView.dequeueReusableCell(withIdentifier: "AppliedAdCell", for: indexPath) as! AppliedAdCell
        cell.adTitleLabel.text = "\(adsResult[indexPath.row].adTitle ?? "")"
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy/MM/dd HH:mm"
        cell.shopStartLabel.text = "\(dateFormatter.string(for: adsResult[indexPath.row].startTime) ?? "") ~"
        cell.shopEndLabel.text = "\(dateFormatter.string(from: adsResult[indexPath.row].endTime))"
        cell.shopWageLabel.text = "\(adsResult[indexPath.row].wage ?? 0)원"
        switch adsResult[indexPath.row].state {
        case 0: cell.stateLabel.text = "모집 중"
            break
        case 1: cell.stateLabel.text = "모집 완료"
            break
        default:
            break
        }
        cell.shopImageView.image = adsResult[indexPath.row].images[0]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        ShowAdViewController.selectedAd = adsResult[indexPath.row]
        
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "구인 광고 보기", style: .default, handler: { (action) in
            self.performSegue(withIdentifier: "adDetailSegue", sender: nil)
        }))
        alert.addAction(UIAlertAction(title: "지원자 확인하기", style: .default, handler: { (action) in
            self.checkApplicants()
        }))
        alert.addAction(UIAlertAction(title: "광고 마감하기", style: .default, handler: { (action) in
            if self.adsResult[indexPath.row].state == 1 {
                CommonFuncs.showToast(message: "이미 마감 된 광고 입니다.", view: self.view)
            }
            else {
                self.finishAd()
            }
        }))
        alert.addAction(UIAlertAction(title: "취소", style: .destructive, handler: nil))
        self.present(alert, animated: true)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func updateStatedAdsByDate(inp_state: Int, date: String) {
        adsResult.removeAll()
        self.uploadedAdTableView.reloadData()
        let db = Firestore.firestore()
        db.collection("AdData").whereField("Uploader", isEqualTo:  LoginViewController.user.email ?? "").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let docID = document?.documentID
                if inp_state == -1 {
                    self.updateAdsByDate(docID: docID ?? "", date: date) {
                        self.uploadedAdTableView.reloadData()
                    }
                }
                else {
                    let data = document?.data()
                    let state = data!["state"] as! Int
                    if state == inp_state {
                        self.updateAdsByDate(docID: docID ?? "", date: date) {
                            self.uploadedAdTableView.reloadData()
                        }
                    }
                }
            }
        }
    }
    
    func updateStatedAds(inp_state: Int) {
        adsResult.removeAll()
        self.uploadedAdTableView.reloadData()
        let db = Firestore.firestore()
        db.collection("AdData").whereField("Uploader", isEqualTo:  LoginViewController.user.email ?? "").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let docID = document?.documentID
                if inp_state == -1 {
                    self.updateAds(docID: docID ?? "") {
                        self.uploadedAdTableView.reloadData()
                    }
                }
                else {
                    let data = document?.data()
                    let state = data!["state"] as! Int
                    if state == inp_state {
                        self.updateAds(docID: docID ?? "") {
                            self.uploadedAdTableView.reloadData()
                        }
                    }
                }
            }
        }
    }
    
    func updateAds(docID: String, success: @escaping ()->()) {
        let db = Firestore.firestore()
        db.collection("AdData").document(docID).getDocument() {
            (document, err) in
            let data = document?.data()
            let startTimeString = data!["startTime"] as! String
            let endTimeString = data!["endTime"] as! String
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyyMMdd HH:mm"
            let startTime = dateFormatter.date(from: startTimeString)
            let endTime = dateFormatter.date(from: endTimeString)
            let ad = Ad.init(email: data!["Uploader"] as! String, name: data!["name"] as! String, type: data!["type"] as! String, lat: data!["latitude"] as! Double, lng: data!["longitude"] as! Double, range: data!["range"] as! Int, title: data!["adTitle"] as? String, start: startTime!, end: endTime!, wage: data!["wage"] as! Int, workDetail: data!["workDetail"] as! String, preferGender: data!["preferGender"] as! Int, preferMinAge: data!["preferMinAge"] as! Int, preferMaxAge: data!["preferMaxAge"] as! Int, preferInfo: data!["preferInfo"] as! String)
            //분야/인원
            let fieldCount = data!["fieldCount"] as! Int
            ad.recruitFieldArr = [String]()
            ad.recruitNumOfPeopleArr = [Int]()
            for fcount in 0..<fieldCount {
                ad.recruitFieldArr.append(data!["field"+String(fcount+1)] as! String)
                ad.recruitNumOfPeopleArr.append(data!["numberOfPeople"+String(fcount+1)] as! Int)
            }
            ad.state = data!["state"] as? Int
            //첫번째 이미지 불러오기
            ad.imageCount = data!["imageCount"] as? Int
            ad.docID = document?.documentID
            ad.images = [UIImage]()
            let storage = Storage.storage()
            let storageRef = storage.reference()
            let imageRef = storageRef.child("Ad/" + ad.docID + "/shop_image_1.jpg")
            imageRef.downloadURL(completion: {(url, error) in
                if error != nil {
                    ad.images.append(UIImage(named: "empty_profile_image.jpg")!)
                } else {
                    if let imageURL = url {
                        let urlContents = try? Data(contentsOf: imageURL)
                        if let imageData = urlContents {
                            ad.images.append(UIImage(data: imageData)!)
                        }
                    }
                    self.adsResult.append(ad)
                    success()
                }
            })
        }
    }
    
    func updateAdsByDate(docID: String, date: String, success: @escaping ()->()) {
        let db = Firestore.firestore()
        db.collection("AdData").document(docID).getDocument() {
            (document, err) in
            let data = document?.data()
            let startTimeString = data!["startTime"] as! String
            if startTimeString.hasPrefix(date) {
                let endTimeString = data!["endTime"] as! String
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "yyyyMMdd HH:mm"
                let startTime = dateFormatter.date(from: startTimeString)
                let endTime = dateFormatter.date(from: endTimeString)
                let ad = Ad.init(email: data!["Uploader"] as! String, name: data!["name"] as! String, type: data!["type"] as! String, lat: data!["latitude"] as! Double, lng: data!["longitude"] as! Double, range: data!["range"] as! Int, title: data!["adTitle"] as? String, start: startTime!, end: endTime!, wage: data!["wage"] as! Int, workDetail: data!["workDetail"] as! String, preferGender: data!["preferGender"] as! Int, preferMinAge: data!["preferMinAge"] as! Int, preferMaxAge: data!["preferMaxAge"] as! Int, preferInfo: data!["preferInfo"] as! String)
                ad.state = data!["state"] as? Int
                //분야/인원
                let fieldCount = data!["fieldCount"] as! Int
                ad.recruitFieldArr = [String]()
                ad.recruitNumOfPeopleArr = [Int]()
                for fcount in 0..<fieldCount {
                    ad.recruitFieldArr.append(data!["field"+String(fcount+1)] as! String)
                    ad.recruitNumOfPeopleArr.append(data!["numberOfPeople"+String(fcount+1)] as! Int)
                }
                //첫번째 이미지 불러오기
                ad.imageCount = data!["imageCount"] as? Int
                ad.docID = document?.documentID
                ad.images = [UIImage]()
                let storage = Storage.storage()
                let storageRef = storage.reference()
                let imageRef = storageRef.child("Ad/" + ad.docID + "/shop_image_1.jpg")
                imageRef.downloadURL(completion: {(url, error) in
                    if error != nil {
                        ad.images.append(UIImage(named: "empty_profile_image.jpg")!)
                    } else {
                        if let imageURL = url {
                            let urlContents = try? Data(contentsOf: imageURL)
                            if let imageData = urlContents {
                                ad.images.append(UIImage(data: imageData)!)
                            }
                        }
                        self.adsResult.append(ad)
                        success()
                    }
                })
            }
        }
    }
}

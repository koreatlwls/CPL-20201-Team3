//
//  PartTimeListViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/31.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class PartTimeListViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UIPickerViewDataSource, UIPickerViewDelegate, UITextFieldDelegate {
    
    var adsResult: [Ad]!
    var selected = "전체"
    var dateString: String!
    
    @IBOutlet weak var appliedAdTableView: UITableView!
    @IBOutlet weak var statePickerView: UIPickerView!
    @IBOutlet weak var dateTextField: UITextField!
    @IBOutlet weak var searchButton: UIButton!
    
    @IBAction func adSearch(_ sender: UIButton) {
        //0 : 미지원
        //1 : 지원(채용 대기)
        //2 : 채용 완료
        //3 : 채용 거절
        switch selected {
        case "전체":
            if dateTextField.text == "" {
                updateAllAppliedAds()
            }
            else {
                if dateTextField.text?.count != 8 {
                    CommonFuncs.showToast(message: "날짜 형식이 맞지 않습니다.", view: self.view)
                }
                else {
                    updateAllAppliedAdsByDate(date: dateTextField.text ?? "")
                }
            }
            break
        case "채용 완료":
            updateStatedAppliedAds(inp_state: 2)
            break
        case "채용 거절":
            updateStatedAppliedAds(inp_state: 3)
            break
        case "채용 대기":
            updateStatedAppliedAds(inp_state: 1)
            break
        default:
            break
        }
    }
    
    let states = ["전체", "채용 완료", "채용 거절", "채용 대기"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.backgroundColor = .clear
        navigationItem.title = "아르바이트 내역"
        
        appliedAdTableView.dataSource = self
        appliedAdTableView.delegate = self
        
        adsResult = [Ad]()
        updateAllAppliedAds()
        
        statePickerView.dataSource = self
        statePickerView.delegate = self
        
        dateTextField.placeholder = "ex) 20200604"
        dateTextField.delegate = self
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentCharacterCount = textField.text?.count ?? 0
        if range.length + range.location > currentCharacterCount {
            return false
        }
        let newLength = currentCharacterCount + string.count - range.length
        return newLength <= 8
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("tableView : ", adsResult.count)
        return adsResult.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = appliedAdTableView.dequeueReusableCell(withIdentifier: "AppliedAdCell", for: indexPath) as! AppliedAdCell
        cell.adTitleLabel.text = "\(adsResult[indexPath.row].adTitle ?? "")"
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy년 MM월 dd일"
        cell.shopDayLabel.text = "\(dateFormatter.string(for: adsResult[indexPath.row].workDay) ?? "")"
        dateFormatter.dateFormat = "HH:mm"
        cell.shopTimeLabel.text = "\(dateFormatter.string(for: adsResult[indexPath.row].startTime) ?? "") ~ \(dateFormatter.string(from: adsResult[indexPath.row].endTime))"
        cell.shopWageLabel.text = "\(adsResult[indexPath.row].wage ?? 0)원"
        cell.shopImageView.image = adsResult[indexPath.row].images[0]
        
        switch adsResult[indexPath.row].applyState {
        case 1: cell.stateLabel.text = "채용 대기중"
            break
        case 2: cell.stateLabel.text = "채용 완료"
            break
        case 3: cell.stateLabel.text = "채용 거절"
            break
        default:
            break
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        ShowAdViewController.selectedAd = adsResult[indexPath.row]
        performSegue(withIdentifier: "adDetailSegue", sender: nil)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
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
    
    func updateAllAppliedAds() {
        adsResult.removeAll()
        self.appliedAdTableView.reloadData()
        let db = Firestore.firestore()
        db.collection("UserData").document(LoginViewController.user.docID).collection("ReceivedAd").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let data = document?.data()
                let state = data!["state"] as! Int
                if state != 0 {
                    let docID = data!["docID"] as! String
                    self.updateAds(docID: docID, inp_state: state) {
                        self.appliedAdTableView.reloadData()
                    }
                }
            }
        }
    }
    
    func updateAllAppliedAdsByDate(date: String) {
        adsResult.removeAll()
        self.appliedAdTableView.reloadData()
        let db = Firestore.firestore()
        db.collection("UserData").document(LoginViewController.user.docID).collection("ReceivedAd").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let data = document?.data()
                let state = data!["state"] as! Int
                if state != 0 {
                    let docID = data!["docID"] as! String
                    self.updateAdsByDate(docID: docID, inp_state: state, date: date) {
                        self.appliedAdTableView.reloadData()
                    }
                }
            }
        }
    }
    
    func updateStatedAppliedAds(inp_state: Int) {
        adsResult.removeAll()
        self.appliedAdTableView.reloadData()
        let db = Firestore.firestore()
        db.collection("UserData").document(LoginViewController.user.docID).collection("ReceivedAd").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let data = document?.data()
                let state = data!["state"] as! Int
                if state == inp_state {
                    let docID = data!["docID"] as! String
                    self.updateAds(docID: docID, inp_state: state) {
                        self.appliedAdTableView.reloadData()
                    }
                }
            }
        }
    }
    
    func updateAds(docID: String, inp_state: Int, success: @escaping ()->()) {
        let db = Firestore.firestore()
        db.collection("AdData").document(docID).getDocument() {
            (document, err) in
            let data = document?.data()
            let workDayString = data!["workDay"] as! String
            let startTimeString = data!["startTime"] as! String
            let endTimeString = data!["endTime"] as! String
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyyMMdd"
            let workDay = dateFormatter.date(from: workDayString)
            dateFormatter.dateFormat = "HH:mm"
            let startTime = dateFormatter.date(from: startTimeString)
            let endTime = dateFormatter.date(from: endTimeString)
            let ad = Ad.init(email: data!["Uploader"] as! String, name: data!["name"] as! String, type: data!["type"] as! String, lat: data!["latitude"] as! Double, lng: data!["longitude"] as! Double, range: data!["range"] as! Int, title: data!["adTitle"] as? String, day: workDay!, start: startTime!, end: endTime!, wage: data!["wage"] as! Int, workDetail: data!["workDetail"] as! String, preferGender: data!["preferGender"] as! Int, preferMinAge: data!["preferMinAge"] as! Int, preferMaxAge: data!["preferMaxAge"] as! Int, preferInfo: data!["preferInfo"] as! String)
            ad.applyState = inp_state
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
    
    func updateAdsByDate(docID: String, inp_state: Int, date: String, success: @escaping ()->()) {
        let db = Firestore.firestore()
        db.collection("AdData").document(docID).getDocument() {
            (document, err) in
            let data = document?.data()
            let workDayString = data!["workDay"] as! String
            if workDayString == date {
                let startTimeString = data!["startTime"] as! String
                let endTimeString = data!["endTime"] as! String
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "yyyyMMdd"
                let workDay = dateFormatter.date(from: workDayString)
                dateFormatter.dateFormat = "HH:mm"
                let startTime = dateFormatter.date(from: startTimeString)
                let endTime = dateFormatter.date(from: endTimeString)
                let ad = Ad.init(email: data!["Uploader"] as! String, name: data!["name"] as! String, type: data!["type"] as! String, lat: data!["latitude"] as! Double, lng: data!["longitude"] as! Double, range: data!["range"] as! Int, title: data!["adTitle"] as? String, day: workDay!, start: startTime!, end: endTime!, wage: data!["wage"] as! Int, workDetail: data!["workDetail"] as! String, preferGender: data!["preferGender"] as! Int, preferMinAge: data!["preferMinAge"] as! Int, preferMaxAge: data!["preferMaxAge"] as! Int, preferInfo: data!["preferInfo"] as! String)
                ad.applyState = inp_state
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

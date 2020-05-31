//
//  AppliedAdViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/31.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class AppliedAdViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    var appliedAds: [Ad]!
    var adsState: [Int]!
    
    @IBOutlet weak var appliedAdTableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationItem.title = "지원한 광고 목록"
        
        appliedAdTableView.dataSource = self
        appliedAdTableView.delegate = self
        
        appliedAds = [Ad]()
        adsState = [Int]()
        updateAppliedAds()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("tableView : ", appliedAds.count)
        return appliedAds.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = appliedAdTableView.dequeueReusableCell(withIdentifier: "AppliedAdCell", for: indexPath) as! AppliedAdCell
        cell.adTitleLabel.text = "\(appliedAds[indexPath.row].adTitle ?? "")"
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        cell.shopDayLabel.text = "\(dateFormatter.string(for: appliedAds[indexPath.row].workDay) ?? "")"
        dateFormatter.dateFormat = "HH:mm"
        cell.shopTimeLabel.text = "\(dateFormatter.string(for: appliedAds[indexPath.row].startTime) ?? "") ~ \(dateFormatter.string(from: appliedAds[indexPath.row].endTime))"
        cell.shopWageLabel.text = "\(appliedAds[indexPath.row].wage ?? 0)원"
        cell.shopImageView.image = appliedAds[indexPath.row].images[0]
        switch adsState[indexPath.row] {
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
        ShowAdViewController.selectedAd = appliedAds[indexPath.row]
        performSegue(withIdentifier: "adDetailSegue", sender: nil)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func updateAppliedAds() {
        let db = Firestore.firestore()
        db.collection("UserData").document(LoginViewController.user.docID).collection("ReceivedAd").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let data = document?.data()
                let state = data!["state"] as! Int
                if state != 0 {
                    self.adsState.append(state)
                    let docID = data!["docID"] as! String
                    self.updateAds(docID: docID) {
                        self.appliedAdTableView.reloadData()
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
            let workDayString = data!["workDay"] as! String
            let startTimeString = data!["startTime"] as! String
            let endTimeString = data!["endTime"] as! String
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd"
            let workDay = dateFormatter.date(from: workDayString)
            dateFormatter.dateFormat = "HH:mm"
            let startTime = dateFormatter.date(from: startTimeString)
            let endTime = dateFormatter.date(from: endTimeString)
            let ad = Ad.init(email: data!["Uploader"] as! String, name: data!["name"] as! String, type: data!["type"] as! String, lat: data!["latitude"] as! Double, lng: data!["longitude"] as! Double, range: data!["range"] as! Int, title: data!["adTitle"] as? String, day: workDay!, start: startTime!, end: endTime!, wage: data!["wage"] as! Int, workDetail: data!["workDetail"] as! String, preferGender: data!["preferGender"] as! Int, preferMinAge: data!["preferMinAge"] as! Int, preferMaxAge: data!["preferMaxAge"] as! Int, preferInfo: data!["preferInfo"] as! String)
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
                    self.appliedAds.append(ad)
                    success()
                }
            })
        }
    }
}

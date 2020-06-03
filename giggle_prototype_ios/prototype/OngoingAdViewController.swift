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

class OngoingAdViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    var ongoingAds: [Ad]!
    @IBOutlet weak var OngoingAdTableView: UITableView!
    
    func checkApplicants() {
        self.performSegue(withIdentifier: "checkApplicantsSegue", sender: nil)
    }
    
    func finishAd() {
        let finish_alert = UIAlertController(title: "광고 마감", message: "해당 광고의 구인을 마감하시겠습니까?", preferredStyle: .alert)
        let ok_action = UIAlertAction(title: "확인", style: .default, handler: {
            (action) in
            let db = Firestore.firestore()
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
        
        OngoingAdTableView.dataSource = self
        OngoingAdTableView.delegate = self
        
        ongoingAds = [Ad]()
        updateOngoingAds {
            self.OngoingAdTableView.reloadData()
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("tableView : ", ongoingAds.count)
        return ongoingAds.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = OngoingAdTableView.dequeueReusableCell(withIdentifier: "OngoingAdCell", for: indexPath) as! OngoingAdCell
        cell.adTitleLabel.text = "\(ongoingAds[indexPath.row].adTitle ?? "")"
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        cell.shopDayLabel.text = "\(dateFormatter.string(for: ongoingAds[indexPath.row].workDay) ?? "")"
        dateFormatter.dateFormat = "HH:mm"
        cell.shopTimeLabel.text = "\(dateFormatter.string(for: ongoingAds[indexPath.row].startTime) ?? "") ~ \(dateFormatter.string(from: ongoingAds[indexPath.row].endTime))"
        cell.shopWageLabel.text = "\(ongoingAds[indexPath.row].wage ?? 0)원"
        cell.shopImageView.image = ongoingAds[indexPath.row].images[0]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        ShowAdViewController.selectedAd = ongoingAds[indexPath.row]
        
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "구인 광고 보기", style: .default, handler: { (action) in
            self.performSegue(withIdentifier: "adDetailSegue", sender: nil)
        }))
        alert.addAction(UIAlertAction(title: "지원자 확인하기", style: .default, handler: { (action) in
            self.checkApplicants()
        }))
        alert.addAction(UIAlertAction(title: "광고 마감하기", style: .default, handler: { (action) in
            self.finishAd()
        }))
        alert.addAction(UIAlertAction(title: "취소", style: .destructive, handler: nil))
        self.present(alert, animated: true)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func updateOngoingAds(success: @escaping ()->()) {
        print("update ongoing ads")
        let db = Firestore.firestore()
        db.collection("AdData").whereField("Uploader", isEqualTo:  LoginViewController.user.email ?? "").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let data = document?.data()
                let state = data!["state"] as! Int
                if state == 0 {
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
                            self.ongoingAds.append(ad)
                            success()
                        }
                    })
                }
            }
        }
    }
}

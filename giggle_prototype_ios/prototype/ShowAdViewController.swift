//
//  ShowAdViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/24.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase
import SDWebImage

class ShowAdViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var AdTableView: UITableView!
    private var refreshControl = UIRefreshControl()
    var ads = [Ad]()
    static var selectedAd: Ad!
    let queue = DispatchQueue(label: "ShowAdViewController")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("ShowAdViewController : view did load")
        
        AdTableView.dataSource = self
        AdTableView.delegate = self
        
        updateAds {
            self.AdTableView.reloadData()
        }

        //refresh 설정
        if #available(iOS 10.0, *) {
            AdTableView.refreshControl = refreshControl
        } else {
            AdTableView.addSubview(refreshControl)
        }
        refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        print("ShowAdViewController : view will appear")
        navigationController?.isNavigationBarHidden = true
        navigationItem.hidesBackButton = true
    }
    
    @objc func refresh() {
        updateAds {
            self.AdTableView.reloadData()
            self.refreshControl.endRefreshing()
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("tableView : ", ads.count)
        return ads.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = AdTableView.dequeueReusableCell(withIdentifier: "AdDetailCell", for: indexPath) as! AdDetailCell
        cell.adTitleLabel.text = "\(ads[indexPath.row].adTitle ?? "")"
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy년 MM월 dd일"
        cell.shopDayLabel.text = "\(dateFormatter.string(for: ads[indexPath.row].workDay) ?? "")"
        dateFormatter.dateFormat = "HH:mm"
        cell.shopTimeLabel.text = "\(dateFormatter.string(for: ads[indexPath.row].startTime) ?? "") ~ \(dateFormatter.string(from: ads[indexPath.row].endTime))"
        cell.shopWageLabel.text = "\(ads[indexPath.row].wage ?? 0)원"
        cell.shopImageView.image = ads[indexPath.row].images[0]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        ShowAdViewController.selectedAd = ads[indexPath.row]
        performSegue(withIdentifier: "adDetailSegue", sender: nil)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    private func updateAds(success: @escaping ()->()) {
        ads.removeAll()
        let db = Firestore.firestore()
        if LoginViewController.user.adsID.count == 0 {
            success()
        }
        else {
            for index in 0..<LoginViewController.user.adsID.count {
                db.collection("AdData").document(LoginViewController.user.adsID[index]).getDocument() {
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
                    //분야/인원
                    let fieldCount = data!["fieldCount"] as! Int
                    ad.recruitFieldArr = [String]()
                    ad.recruitNumOfPeopleArr = [Int]()
                    for fcount in 0..<fieldCount {
                        ad.recruitFieldArr.append(data!["field"+String(fcount+1)] as! String)
                        ad.recruitNumOfPeopleArr.append(data!["numberOfPeople"+String(fcount+1)] as! Int)
                    }
                    //첫번째 이미지 불러오기
                    DispatchQueue.main.async {
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
                                self.ads.append(ad)
                                print("updateAds : ", self.ads.count)
                                success()
                            }
                        })
                    }
                }
            }
        }
    }
}

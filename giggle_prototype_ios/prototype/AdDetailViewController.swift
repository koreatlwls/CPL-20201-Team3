//
//  AdDetailViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/22.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase
import SDWebImage

class AdDetailViewController: UIViewController, UIScrollViewDelegate {
    @IBOutlet weak var mapContainerView: UIView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var shopImageView: UIImageView!
    @IBOutlet weak var imagePageControl: UIPageControl!
    @IBOutlet weak var typeLabel: UILabel!
    @IBOutlet weak var adTitleLabel: UILabel!
    @IBOutlet weak var wageLabel: UILabel!
    @IBOutlet weak var workDayLabel: UILabel!
    @IBOutlet weak var startTimeLabel: UILabel!
    @IBOutlet weak var endTimeLabel: UILabel!
    @IBOutlet weak var workDetailLabel: UILabel!
    @IBOutlet weak var parentFieldStackView: UIStackView!
    @IBOutlet weak var genderLabel: UILabel!
    @IBOutlet weak var ageLabel: UILabel!
    @IBOutlet weak var preferenceDetailLabel: UILabel!
    @IBOutlet weak var shopNameLabel: UILabel!
    
    let queue = DispatchQueue(label: "AdDetailViewController")
    
    @IBAction func backToMarker(_ sender: UIButton) {
        SAV_MapContainerViewController.updateCamera()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //네비게이션 바 설정
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.backgroundColor = .clear
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "지원", style: .plain, target: self, action: #selector(apply))
        
        //이미지 불러오기
        let storage = Storage.storage()
        let storageRef = storage.reference()
        for index in 1..<ShowAdViewController.selectedAd.imageCount {
            let imageRef = storageRef.child("Ad/" + ShowAdViewController.selectedAd.docID + "/shop_image_" + String(index+1) + ".jpg")
            imageRef.downloadURL(completion: {(url, error) in
                if error != nil {
                    ShowAdViewController.selectedAd.images.append(UIImage(named: "empty_profile_image.jpg")!)
                } else {
                    if let imageURL = url {
                        let urlContents = try? Data(contentsOf: imageURL)
                        if let imageData = urlContents {
                            ShowAdViewController.selectedAd.images.append(UIImage(data: imageData)!)
                        }
                    }
                }
            })
        }
        
        //페이지 컨트롤 설정
        imagePageControl.numberOfPages = ShowAdViewController.selectedAd.imageCount
        imagePageControl.currentPage = 0
        imagePageControl.pageIndicatorTintColor = UIColor.lightGray
        imagePageControl.currentPageIndicatorTintColor = UIColor.black
        
        //상세 내용 설정
        typeLabel.text = ShowAdViewController.selectedAd.type
        adTitleLabel.text = ShowAdViewController.selectedAd.adTitle
        adTitleLabel.numberOfLines = 0
        adTitleLabel.lineBreakMode = .byWordWrapping
        wageLabel.text = "\(String(ShowAdViewController.selectedAd.wage))원"
        wageLabel.clipsToBounds = true
        wageLabel.layer.cornerRadius = 6
        let dateformatter = DateFormatter()
        dateformatter.dateFormat = "yyyy년 MM월 dd일"
        workDayLabel.text = dateformatter.string(from: ShowAdViewController.selectedAd.workDay)
        dateformatter.dateFormat = "HH시 mm분"
        startTimeLabel.text = dateformatter.string(from: ShowAdViewController.selectedAd.startTime)
        endTimeLabel.text = dateformatter.string(from: ShowAdViewController.selectedAd.endTime)
        workDetailLabel.text = ShowAdViewController.selectedAd.workDetail
        workDetailLabel.numberOfLines = 0
        workDetailLabel.lineBreakMode = .byWordWrapping
        (parentFieldStackView.subviews[1].subviews[0] as! UILabel).text = ShowAdViewController.selectedAd.recruitFieldArr[0]
        (parentFieldStackView.subviews[1].subviews[1] as! UILabel).text = "\(String(ShowAdViewController.selectedAd.recruitNumOfPeopleArr[0]))명"
        for index in 1..<ShowAdViewController.selectedAd.recruitFieldArr.count {
            do {
                let newFieldStackView = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(NSKeyedArchiver.archivedData(withRootObject: parentFieldStackView.subviews[1], requiringSecureCoding: false)) as! UIStackView
                let index2 = parentFieldStackView.subviews.count
                parentFieldStackView.insertArrangedSubview(newFieldStackView, at: index2)
                (parentFieldStackView.subviews[index2].subviews[0] as! UILabel).text = ShowAdViewController.selectedAd.recruitFieldArr[index]
                (parentFieldStackView.subviews[index2].subviews[1] as! UILabel).text = "\(String(ShowAdViewController.selectedAd.recruitNumOfPeopleArr[index]))명"
            } catch {
                print(error)
            }
        }
        switch ShowAdViewController.selectedAd.preferGender {
        case 0: genderLabel.text = "남자"
            break
        case 1: genderLabel.text = "여자"
            break
        case 2: genderLabel.text = "무관"
            break
        case .none:
            break
        case .some(_):
            break
        }
        if ShowAdViewController.selectedAd.preferMinAge == -1 && ShowAdViewController.selectedAd.preferMaxAge == -1 {
            ageLabel.text = "무관"
        }
        else {
            ageLabel.text = "\(String(ShowAdViewController.selectedAd.preferMinAge)) ~ \(String(ShowAdViewController.selectedAd.preferMaxAge))세"
        }
        preferenceDetailLabel.text = ShowAdViewController.selectedAd.preferInfo
        shopNameLabel.text = ShowAdViewController.selectedAd.name
        shopNameLabel.numberOfLines = 0
        shopNameLabel.lineBreakMode = .byWordWrapping
    }
    
    private func updateScrollView() {
        shopImageView.image = ShowAdViewController.selectedAd.images[0]
        for index in 1..<ShowAdViewController.selectedAd.imageCount {
            let imageView = UIImageView()
            imageView.frame = shopImageView.frame
            imageView.frame.origin.x = imageView.frame.width*CGFloat(index)
            imageView.image = ShowAdViewController.selectedAd.images[index]
            scrollView.addSubview(imageView)
        }
        
        scrollView.isPagingEnabled = true
        scrollView.alwaysBounceVertical = false
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.delegate = self
        scrollView.contentSize = CGSize(width: scrollView.frame.width*CGFloat(ShowAdViewController.selectedAd.imageCount), height: 0)
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        imagePageControl.currentPage = Int(scrollView.contentOffset.x / scrollView.frame.width)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        updateScrollView()
    }
    
    @objc func apply() {
        let db = Firestore.firestore()
        db.collection("UserData").document(LoginViewController.user.docID).collection("ReceivedAd").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot!.documents.count
            for index in 0..<count {
                let document = querySnapshot!.documents[index]
                let data = document.data()
                let tempDocID = data["docID"] as! String
                if tempDocID == ShowAdViewController.selectedAd.docID {
                    let state = data["state"] as! Int
                    if state == 0 {
                        let check_alert = UIAlertController(title: "지원하기", message: "자신을 어필할 수 있는 메시지를 입력하여 지원하세요 !", preferredStyle: .alert)
                        let ok_Action = UIAlertAction(title: "지원", style: .default, handler: {
                            (action) in
                            db.collection("UserData").document(LoginViewController.user.docID).collection("ReceivedAd").document(document.documentID).updateData(["state": 1])
                            let complete_alert = UIAlertController(title: "지원완료", message: "지원이 완료되었습니다.", preferredStyle: .alert)
                            let complete_ok_Action = UIAlertAction(title: "확인", style: .default, handler: nil)
                            complete_alert.addAction(complete_ok_Action)
                            self.present(complete_alert, animated: true, completion: nil)
                            //
                            db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").addDocument(data: [
                                "docID": LoginViewController.user.docID ?? "",
                                "message": check_alert.textFields![0].text ?? "",
                                "state": 0
                            ])
                            for index2 in 0..<LoginViewController.user.adsID.count {
                                if LoginViewController.user.adsID[index2] == tempDocID {
                                    LoginViewController.user.adsID.remove(at: index2)
                                    break
                                }
                            }
                            //푸시 알림 전송
                            self.sendPost()
                        })
                        let no_Action = UIAlertAction(title: "취소", style: .cancel, handler: nil)
                        check_alert.addTextField() {
                            (textField) in
                            textField.placeholder = "지원 메시지 입력"
                        }
                        check_alert.addAction(ok_Action)
                        check_alert.addAction(no_Action)
                        self.present(check_alert, animated: true, completion: nil)
                    }
                    else {
                        let error_alert = UIAlertController(title: "지원하기", message: "이미 지원한 광고입니다.", preferredStyle: .alert)
                        let ok_Action = UIAlertAction(title: "확인", style: .default, handler: nil)
                        error_alert.addAction(ok_Action)
                        self.present(error_alert, animated: true, completion: nil)
                    }
                    break
                }
            }
        }
    }
    
    func sendPost() {
        let db = Firestore.firestore()
        var email: String!
        var token: String!
        
        db.collection("AdData").document(ShowAdViewController.selectedAd.docID).getDocument() {
            (document, err) in
            let data = document?.data()
            email = data!["Uploader"] as? String
            db.collection("UserData").whereField("email", isEqualTo: email ?? "").getDocuments() {
                (querySnapshot, err2) in
                let data = querySnapshot?.documents[0].data()
                token = data!["fcmToken"] as? String
                print(token)
                print(email)
                let param = [
                    "to": token ?? "",
                    "data": [
                        "type": "apply",
                        "adTitle": ShowAdViewController.selectedAd.adTitle
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
                            print("error=\(error)")
                            return
                        }
                        
                        if let httpStatus = response as? HTTPURLResponse, httpStatus.statusCode != 200 {
                            print("statusCode should be 200, but is \(httpStatus.statusCode)")
                            print("response = \(response)")
                        }
                        
                        let responseString = String(data: data, encoding: .utf8)
                        print("responseString = \(responseString)")
                    }
                    
                    task.resume()
                }
            }
        }
    }
}

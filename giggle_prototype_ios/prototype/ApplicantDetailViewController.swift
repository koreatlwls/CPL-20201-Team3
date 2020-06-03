//
//  ApplicantDetailViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/06/03.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class ApplicantDetailViewController: UIViewController {
    var message: String!
    @IBOutlet weak var ratingLabel: UILabel!
    @IBOutlet weak var applicantNameLabel: UILabel!
    @IBOutlet weak var applicantProfileImageView: UIImageView!
    @IBOutlet weak var applicantGenderLabel: UILabel!
    @IBOutlet weak var applicantAgeLabel: UILabel!
    @IBOutlet weak var applicantContactLabel: UILabel!
    @IBOutlet weak var applicantHiredCountLabel: UILabel!
    @IBOutlet weak var applicantApplyMessageLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.backgroundColor = .clear
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "처리", style: .plain, target: self, action: #selector(process))
        navigationItem.title = "지원자 상세정보"
        
        applicantProfileImageView.image = CheckApplicantsViewController.selectedUser.image
        ratingLabel.text = "\(CheckApplicantsViewController.selectedUser.rating!) / 5.0"
        applicantNameLabel.text = CheckApplicantsViewController.selectedUser.name
        applicantApplyMessageLabel.text = message
    }
    
    @objc func process() {
        let process_alert = UIAlertController(title: "", message: "해당 지원자에 대한 처리를 선택해주세요.", preferredStyle: .actionSheet)
        let accept_action = UIAlertAction(title: "채용", style: .default, handler: {
            (action) in
            let db = Firestore.firestore()
            db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").whereField("docID", isEqualTo:  CheckApplicantsViewController.selectedUser.docID ?? "").getDocuments() {
                (querySnapshot, err) in
                let document = querySnapshot?.documents[0]
                let docID = document?.documentID
                db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").document(docID!).updateData(["state": 1])
            }
            db.collection("UserData").document(CheckApplicantsViewController.selectedUser.docID).collection("ReceivedAd").whereField("docID", isEqualTo: ShowAdViewController.selectedAd.docID ?? "").getDocuments() {
                (querySnapshot, err) in
                let document = querySnapshot?.documents[0]
                let docID = document?.documentID
                db.collection("UserData").document(CheckApplicantsViewController.selectedUser.docID).collection("ReceivedAd").document(docID!).updateData(["state": 2])
            }
            let accept_alert = UIAlertController(title: "채용 완료", message: "해당 지원자를 채용하였습니다.", preferredStyle: .alert)
            let ok_action = UIAlertAction(title: "확인", style: .default, handler: nil)
            accept_alert.addAction(ok_action)
            self.present(accept_alert, animated: true, completion: nil)
            self.sendPost(notificationType: "hired")
        })
        let deny_action = UIAlertAction(title: "거절", style: .default, handler: {
            (action) in
            let db = Firestore.firestore()
            db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").whereField("docID", isEqualTo:  CheckApplicantsViewController.selectedUser.docID ?? "").getDocuments() {
                (querySnapshot, err) in
                let document = querySnapshot?.documents[0]
                let docID = document?.documentID
                db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").document(docID!).updateData(["state": 2])
            }
            db.collection("UserData").document(CheckApplicantsViewController.selectedUser.docID).collection("ReceivedAd").whereField("docID", isEqualTo: ShowAdViewController.selectedAd.docID ?? "").getDocuments() {
                (querySnapshot, err) in
                let document = querySnapshot?.documents[0]
                let docID = document?.documentID
                db.collection("UserData").document(CheckApplicantsViewController.selectedUser.docID).collection("ReceivedAd").document(docID!).updateData(["state": 3])
            }
            let deny_alert = UIAlertController(title: "채용 거절", message: "해당 지원자의 채용을 거절하였습니다.", preferredStyle: .alert)
            let ok_action = UIAlertAction(title: "확인", style: .default, handler: nil)
            deny_alert.addAction(ok_action)
            self.present(deny_alert, animated: true, completion: nil)
            self.sendPost(notificationType: "rejected")
        })
        let cancel_action = UIAlertAction(title: "취소", style: .cancel, handler: nil)
        process_alert.addAction(accept_action)
        process_alert.addAction(deny_action)
        process_alert.addAction(cancel_action)
        self.present(process_alert, animated: true, completion: nil)
    }
    
    func sendPost(notificationType: String) {
        let token = CheckApplicantsViewController.selectedUser.fcmToken
        let param = [
            "to": token ?? "",
            "data": [
                "adTitle": ShowAdViewController.selectedAd.adTitle,
                "type": notificationType
            ]
        ] as [String : Any] as [String : Any]
        let paramData = try! JSONSerialization.data(withJSONObject: param, options: [])
            
        let url = URL(string: "https://fcm.googleapis.com/fcm/send")
        var request = URLRequest(url: url!)
        request.httpMethod = "POST"
        let serverKey = ""
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

//
//  CheckApplicantsViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/06/02.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class CheckApplicantsViewController : UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var applicantTableView: UITableView!
    var users: [User]!
    var messages: [String]!
    var selectedMessage: String!
    static var selectedUser: User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.backgroundColor = .clear
        navigationItem.title = "지원자 목록"
        
        applicantTableView.dataSource = self
        applicantTableView.delegate = self
        
        users = [User]()
        messages = [String]()
        
        updateApplicant {
            print(self.users.count)
            self.applicantTableView.reloadData()
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("tableView : ", users.count)
        return users.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = applicantTableView.dequeueReusableCell(withIdentifier: "ApplicantCell", for: indexPath) as! ApplicantCell
        cell.applicantNameLabel.text = users[indexPath.row].name
        cell.applicantMessageLabel.text = messages[indexPath.row]
        cell.applicantRateLabel.text = "\(users[indexPath.row].rating!)/5.0"
        let storage = Storage.storage()
        let storageRef = storage.reference()
        let imageRef = storageRef.child("Profile/" + users[indexPath.row].docID + "/profile_image.jpg")
        imageRef.downloadURL(completion: {(url, error) in
            if error != nil {
                self.users[indexPath.row].image = UIImage(named: "empty_profile_image.jpg")
            } else {
                if let imageURL = url {
                    let urlContents = try? Data(contentsOf: imageURL)
                    if let imageData = urlContents {
                        self.users[indexPath.row].image = UIImage(data: imageData)
                    }
                }
            }
            cell.applicantProfileImageView.image = self.users[indexPath.row].image
        })
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        CheckApplicantsViewController.selectedUser = users[indexPath.row]
        selectedMessage = messages[indexPath.row]
        performSegue(withIdentifier: "ApplicantDetailSegue", sender: nil)
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func updateApplicant(success: @escaping ()->()) {
        print("update applicants")
        let db = Firestore.firestore()
        db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let data = document?.data()
                let state = data!["state"] as! Int
                if state == 0 {
                    let docID = data!["docID"] as! String
                    self.messages.append(data!["message"] as! String)
                    db.collection("UserData").document(docID).getDocument() {
                        (document, err2) in
                        let data = document?.data()
                        let user = User.init(name: data!["nickname"] as! String, email: data!["email"] as! String, member_state: data!["member_state"] as! Int, rating: data!["rating"] as! Double)
                        user.docID = docID
                        user.fcmToken = data!["fcmToken"] as! String
                        self.users.append(user)
                        success()
                    }
                }
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let applicantDetailViewController = segue.destination as? ApplicantDetailViewController else { return }
        applicantDetailViewController.message = selectedMessage
    }
}


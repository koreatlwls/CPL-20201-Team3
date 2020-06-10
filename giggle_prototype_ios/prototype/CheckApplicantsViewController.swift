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

class CheckApplicantsViewController : UIViewController, UITableViewDataSource, UITableViewDelegate, UIPickerViewDataSource, UIPickerViewDelegate {
    var selected = "전체"
    let states = ["전체", "채용 완료", "채용 거절", "채용 대기"]
    
    @IBOutlet weak var applicantTableView: UITableView!
    @IBOutlet weak var statePickerView: UIPickerView!
    @IBOutlet weak var searchButton: UIButton!
    @IBAction func applicantSearch(_ sender: UIButton) {
        //0 : 미지원
        //1 : 지원(채용 대기)
        //2 : 채용 완료
        //3 : 채용 거절
        switch selected {
        case "전체":
            updateStatedApplicant(inp_state: -1) {
                self.applicantTableView.reloadData()
            }
        case "채용 완료":
            updateStatedApplicant(inp_state: 1) {
                self.applicantTableView.reloadData()
            }
        case "채용 거절":
            updateStatedApplicant(inp_state: 2) {
                self.applicantTableView.reloadData()
            }
        case "채용 대기":
            updateStatedApplicant(inp_state: 0) {
                self.applicantTableView.reloadData()
            }
        default:
            break
        }
    }
    
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
        
        statePickerView.dataSource = self
        statePickerView.delegate = self
        
        updateStatedApplicant(inp_state: -1) {
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
        let db = Firestore.firestore()
        var state: Int!
        db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").whereField("docID", isEqualTo: users[indexPath.row].docID ?? "").getDocuments() {
            (querySnapshot, err) in
            let document = querySnapshot?.documents[0]
            let data = document?.data()
            state = data!["state"] as? Int
            switch state {
            case 0: cell.stateLabel.text = "채용 대기"
            case 1: cell.stateLabel.text = "채용 완료"
            case 2: cell.stateLabel.text = "채용 거절"
            default:
                break
            }
        }
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
    
    func updateStatedApplicant(inp_state: Int, success: @escaping ()->()) {
        users.removeAll()
        self.applicantTableView.reloadData()
        let db = Firestore.firestore()
        db.collection("AdData").document(ShowAdViewController.selectedAd.docID).collection("Applicant").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot?.documents.count
            for index in 0..<count!{
                let document = querySnapshot?.documents[index]
                let data = document?.data()
                if inp_state == -1 {
                    let docID = data!["docID"] as! String
                    self.messages.append(data!["message"] as! String)
                    db.collection("UserData").document(docID).getDocument() {
                        (document, err2) in
                        let data = document?.data()
                        let user = User.init(name: data!["nickname"] as! String, email: data!["email"] as! String, member_state: data!["member_state"] as! Int, rating: data!["rating"] as! Double)
                        user.docID = docID
                        user.fcmToken = data!["fcmToken"] as? String
                        user.gender = data!["gender"] as? Int
                        user.age = data!["age"] as? Int
                        user.contact = data!["contact"] as? String
                        self.users.append(user)
                        success()
                    }
                }
                else {
                    let state = data!["state"] as! Int
                    if state == inp_state {
                        let docID = data!["docID"] as! String
                        self.messages.append(data!["message"] as! String)
                        db.collection("UserData").document(docID).getDocument() {
                            (document, err2) in
                            let data = document?.data()
                            let user = User.init(name: data!["nickname"] as! String, email: data!["email"] as! String, member_state: data!["member_state"] as! Int, rating: data!["rating"] as! Double)
                            user.docID = docID
                            user.fcmToken = data!["fcmToken"] as? String
                            user.gender = data!["gender"] as? Int
                            user.age = data!["age"] as? Int
                            user.contact = data!["contact"] as? String
                            self.users.append(user)
                            success()
                        }
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


//
//  UserInfoModificationViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/06/08.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class UserInfoCheckViewController: UIViewController {
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var genderLabel: UILabel!
    @IBOutlet weak var ageLabel: UILabel!
    @IBOutlet weak var contactLabel: UILabel!
    @IBOutlet weak var hiredCountLabel: UILabel!
    @IBOutlet weak var workedCountLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.backgroundColor = .clear
        navigationItem.title = "회원 정보 확인"
        
        nameLabel.text = LoginViewController.user.name
        emailLabel.text = LoginViewController.user.email
        switch LoginViewController.user.gender {
        case 0: genderLabel.text = "남자"
        case 1: genderLabel.text = "여자"
        case 2: genderLabel.text = "미응답"
        default:
            break
        }
        ageLabel.text = String(LoginViewController.user.age)
        contactLabel.text = LoginViewController.user.contact
        
        let db = Firestore.firestore()
        db.collection("UserData").document(LoginViewController.user.docID).getDocument() {
            (document, err) in
            if let data = document?.data() {
                if let hiredCount = data["hired"], let workedCount = data["worked"] {
                    LoginViewController.user.hired = hiredCount as? Int
                    LoginViewController.user.worked = workedCount as? Int
                    self.hiredCountLabel.text = String(LoginViewController.user.hired)
                    self.workedCountLabel.text = String(LoginViewController.user.worked)
                }
            }
        }
    }
}

//
//  MyPageViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/24.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class MyPageViewController: UIViewController {
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var requestIntegrationButton: UIButton!
    @IBOutlet weak var ongoingPartTimeJobButton: UIButton!
    @IBOutlet weak var listOfPartTimeJobButton: UIButton!
    @IBOutlet weak var ongoingAdButton: UIButton!
    @IBOutlet weak var listOfEmploymentButton: UIButton!
    @IBOutlet weak var modifyInformationButton: UIButton!
    @IBOutlet weak var logoutButton: UIButton!
    
    //회원 model 추가하여 구직 회원 및 통합 회원에 따른 처리 구분
    
    @objc func testfunc() {
        self.performSegue(withIdentifier: "CallImageViewSegue", sender: nil)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        print("MyPageViewController : view will appear")
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        profileImageView.layer.cornerRadius = 0.5*profileImageView.bounds.width
        if profileImageView.image == nil {
            profileImageView.image = UIImage(named: "empty_profile_image.jpg")
        }
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(testfunc))
        profileImageView.isUserInteractionEnabled = true
        profileImageView.addGestureRecognizer(tapGesture)
        requestIntegrationButton.layer.cornerRadius = 6
        ongoingPartTimeJobButton.layer.cornerRadius = 6
        listOfPartTimeJobButton.layer.cornerRadius = 6
        ongoingAdButton.layer.cornerRadius = 6
        listOfEmploymentButton.layer.cornerRadius = 6
        modifyInformationButton.layer.cornerRadius = 6
        logoutButton.layer.cornerRadius = 6
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if identifier == "logoutSegue" {
            let firebaseAuth = Auth.auth()
            do {
                try firebaseAuth.signOut()
            } catch let signOutError as NSError {
                print ("Error signing out: %@", signOutError)
            }
            self.performSegue(withIdentifier: "logoutSegue", sender: nil)
        }
        else {
            self.performSegue(withIdentifier: identifier, sender: nil)
        }
        return false
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "CallImageViewSegue" {
            let targetView: CallImageViewController = segue.destination as! CallImageViewController
            targetView.image = profileImageView.image
        }
    }
}

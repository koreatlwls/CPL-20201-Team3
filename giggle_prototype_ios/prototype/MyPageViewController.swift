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
import MobileCoreServices

class MyPageViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var ratingLabel: UILabel!
    @IBOutlet weak var memberStateLabel: UILabel!
    @IBOutlet weak var requestIntegrationButton: UIButton!
    @IBOutlet weak var ongoingPartTimeJobButton: UIButton!
    @IBOutlet weak var listOfPartTimeJobButton: UIButton!
    @IBOutlet weak var ongoingAdButton: UIButton!
    @IBOutlet weak var listOfEmploymentButton: UIButton!
    @IBOutlet weak var modifyInformationButton: UIButton!
    @IBOutlet weak var logoutButton: UIButton!
    
    let imagePicker: UIImagePickerController! = UIImagePickerController()
    var captureImage: UIImage!
    var flagImageSave = false
    
    //회원 model 추가하여 구직 회원 및 통합 회원에 따른 처리 구분
    
    @objc func touchImageView() {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "사진 보기", style: .default, handler: { (action) in
            self.performSegue(withIdentifier: "CallImageViewSegue", sender: nil)
        }))
        alert.addAction(UIAlertAction(title: "프로필 사진 변경하기", style: .default, handler: { (action) in
            self.flagImageSave = false
            
            self.imagePicker.delegate = self
            self.imagePicker.sourceType = .photoLibrary
            self.imagePicker.mediaTypes = [kUTTypeImage as String]
            self.imagePicker.allowsEditing = true
            
            self.present(self.imagePicker, animated: true, completion: nil)
        }))
        alert.addAction(UIAlertAction(title: "취소", style: .destructive, handler: nil))
        present(alert, animated: true)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        let mediaType = info[UIImagePickerController.InfoKey.mediaType] as! NSString
        
        if mediaType.isEqual(to: kUTTypeImage as NSString as String) {
            captureImage = (info[UIImagePickerController.InfoKey.originalImage] as! UIImage)
            
            profileImageView.image = captureImage
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        print("MyPageViewController : view will appear")
        
        //Navigation Bar 숨기기
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
        
        //회원 상태에 따른 Label 업데이트
        if LoginViewController.user.member_state == 0 {
            memberStateLabel.text = "구직 회원"
        }
        else {
            memberStateLabel.text = "통합 회원"
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        nameLabel.text = LoginViewController.user.name
        emailLabel.text = LoginViewController.user.email
        
        profileImageView.layer.cornerRadius = 0.5*profileImageView.bounds.width
        if profileImageView.image == nil {
            profileImageView.image = UIImage(named: "empty_profile_image.jpg")
        }
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(touchImageView))
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
                CurrentLocationViewController.childView.removeFromParent()
                AddAdViewController.childView.removeFromParent()
                self.performSegue(withIdentifier: identifier, sender: nil)
            } catch let signOutError as NSError {
                print ("Error signing out: %@", signOutError)
            }
        }
        else if identifier == "MemberIntegrationSegue" {
            if LoginViewController.user.member_state == 1 {
                let alert = UIAlertController(title: "오류", message: "이미 통합 회원 입니다.", preferredStyle: .alert)
                let okAction = UIAlertAction(title: "확인", style: .default, handler: {
                    (action) in
                })
                alert.addAction(okAction)
                self.present(alert, animated: true, completion: nil)
            }
            else {
                self.performSegue(withIdentifier: identifier, sender: nil)
            }
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

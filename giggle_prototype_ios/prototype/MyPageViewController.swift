//
//  MyPageViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/24.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import MobileCoreServices
import SDWebImage
import Firebase

class MyPageViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var ratingLabel: UILabel!
    @IBOutlet weak var memberStateLabel: UILabel!
    @IBOutlet weak var requestIntegrationButton: UIButton!
    @IBOutlet weak var partTimeListButton: UIButton!
    @IBOutlet weak var uploadedAdListButton: UIButton!
    @IBOutlet weak var modifyInformationButton: UIButton!
    @IBOutlet weak var logoutButton: UIButton!
    
    let imagePicker: UIImagePickerController! = UIImagePickerController()
    var captureImage: UIImage!
    var flagImageSave = false

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
            
            let imageURL = info[.imageURL] as? URL
            let storage = Storage.storage()
            let storageRef = storage.reference()
            let imageRef = storageRef.child("Profile/" + LoginViewController.user.docID + "/profile_image.jpg")
            let metadata = StorageMetadata()
            metadata.contentType = "image/jpeg"
            
            let uploadTask = imageRef.putFile(from: imageURL!, metadata: metadata)
            uploadTask.observe(.failure) { snapshot in
                if let error = snapshot.error as NSError? {
                    switch (StorageErrorCode(rawValue: error.code)!) {
                    case .objectNotFound:
                        print("object Not Found")
                        break
                    case .unauthorized:
                        print("unauthorized")
                        break
                    case .cancelled:
                        print("cancelled")
                        break
                    case .unknown:
                        print("unknown")
                        break
                    default:
                        print("default: retry to upload")
                        break
                    }
                }
            }
            
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
            memberStateLabel.text = "구인 회원"
        }
        else {
            memberStateLabel.text = "통합 회원"
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        nameLabel.text = LoginViewController.user.name
        emailLabel.text = LoginViewController.user.email
        
        //프로필 이미지 불러오기
        if LoginViewController.user.image == nil {
            profileImageView.image = UIImage(named: "empty_profile_image.jpg")
        }
        else {
            profileImageView.image = LoginViewController.user.image
        }
        /*let storage = Storage.storage()
        let storageRef = storage.reference()
        let imageRef = storageRef.child("Profile/" + LoginViewController.user.docID + "/profile_image.jpg")
        imageRef.downloadURL(completion: {(url, error) in
            if error != nil {
                self.profileImageView.image = UIImage(named: "empty_profile_image.jpg")
            } else {
                self.profileImageView.sd_setImage(with: url, placeholderImage: UIImage(named: "empty_profile_image.jpg"))
            }
        })*/
        
        profileImageView.layer.cornerRadius = 0.5*profileImageView.bounds.width
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(touchImageView))
        profileImageView.isUserInteractionEnabled = true
        profileImageView.addGestureRecognizer(tapGesture)
        requestIntegrationButton.layer.cornerRadius = 6
        partTimeListButton.layer.cornerRadius = 6
        uploadedAdListButton.layer.cornerRadius = 6
        modifyInformationButton.layer.cornerRadius = 6
        logoutButton.layer.cornerRadius = 6
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if identifier == "logoutSegue" {
            let firebaseAuth = Auth.auth()
            do {
                try firebaseAuth.signOut()
                if CurrentLocationViewController.childView != nil {
                    CurrentLocationViewController.childView.removeFromParent()
                }
                if AddAdViewController.childView != nil {
                    AddAdViewController.childView.removeFromParent()
                }
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

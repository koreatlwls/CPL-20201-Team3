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
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
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

//
//  ViewController.swift
//  giggle
//
//  Created by 윤영신 on 2020/04/20.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase
import GoogleMaps

class LoginViewController: UIViewController {
    static var user: User!
    
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    
    @IBOutlet weak var resetPasswordButton: UIButton!
    @IBOutlet weak var signupButton: UIButton!
    @IBOutlet weak var loginButton: UIButton!
    
    @IBOutlet weak var loginStackView: UIStackView!
    
    private var keyboardStatus = false
    private var keyboardHeight: CGFloat!
    private var keyboardOffset: CGFloat!
    
    override func viewWillAppear(_ animated: Bool) {
        guard let navigationController = self.navigationController else { return }
        var navigationArray = navigationController.viewControllers
        navigationArray.removeSubrange(0..<navigationArray.count-1)
        navigationItem.hidesBackButton = true
        navigationController.isNavigationBarHidden = true
        if Auth.auth().currentUser != nil {
            performSegue(withIdentifier: "loginSegue", sender: nil)
        }
    }
    
    private func addKeyboardNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc private func keyboardWillShow(_ notification: Notification) {
        if !keyboardStatus {
            keyboardStatus = true
            if let keyboardFrame: NSValue = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
                let keyboardRectangle = keyboardFrame.cgRectValue
                keyboardHeight = keyboardRectangle.height / 3 + 10
                self.view.frame.origin.y -= keyboardHeight
            }
        }
    }
    
    @objc private func keyboardWillHide(_ notification: Notification) {
        if keyboardStatus {
            keyboardStatus = false
            self.view.frame.origin.y += keyboardHeight
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        //Button UI Setting
        signupButton.layer.cornerRadius = 6
        loginButton.layer.cornerRadius = 6
        resetPasswordButton.layer.cornerRadius = 6
        
        //TextField Delegate 설정
        emailTextField.delegate = self
        passwordTextField.delegate = self
        addKeyboardNotification()
        
        //Location Authorization Check
        let status = CLLocationManager.authorizationStatus()
        
        if status == CLAuthorizationStatus.denied || status == CLAuthorizationStatus.restricted {
            let warning = UIAlertController(title: "위치 접근 허용이 '안 함'으로 설정되어있습니다.", message: "[설정 - 개인 정보 보호 - 위치 서비스 - Giggle]에서 '앱을 사용하는 동안' 또는 '항상'으로 설정해주세요.", preferredStyle: UIAlertController.Style.alert)
            let yesAction = UIAlertAction(title: "확인", style: UIAlertAction.Style.default) {
                (action) in exit(0)
            }
            warning.addAction(yesAction)
            self.present(warning, animated: true, completion: nil)
        }
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if identifier == "loginSegue" {
            Auth.auth().signIn(withEmail: emailTextField.text ?? "", password: passwordTextField.text ?? "") {
                (user, error) in
                if user != nil {
                    let curUser = Auth.auth().currentUser!
                    if curUser.isEmailVerified {
                        let db = Firestore.firestore()
                        db.collection("UserData").whereField("email", isEqualTo: self.emailTextField.text ?? "").getDocuments() {
                            (querySnapshot, err) in
                            if querySnapshot!.documents.count == 0 {
                                let error_alert = UIAlertController(title: "DB 오류", message: "해당 계정 정보를 DB에서 불러올 수 없습니다. 관리자에게 문의해주세요.", preferredStyle: .alert)
                                let error_okAction = UIAlertAction(title: "확인", style: .default, handler: nil)
                                error_alert.addAction(error_okAction)
                                self.present(error_alert, animated: true, completion: nil)
                            }
                            else {
                                let document = querySnapshot?.documents[0]
                                let data = document?.data()
                                let name = data!["nickname"] as! String
                                let email = data!["email"] as! String
                                let member_state = data!["member_state"] as! Int
                                LoginViewController.user = User.init(name: name, email: email, member_state: member_state, rating: 0)
                                if member_state == 1 {
                                    LoginViewController.user.gender = data!["gender"] as? Int
                                    LoginViewController.user.age = data!["age"] as? Int
                                    LoginViewController.user.contact = data!["contact"] as? String
                                    LoginViewController.user.hired = data!["hired"] as? Int
                                    LoginViewController.user.worked = data!["worked"] as? Int
                                }
                                LoginViewController.user.docID = document?.documentID
                                LoginViewController.user.lat = data!["lat"] as? CLLocationDegrees
                                LoginViewController.user.lng = data!["lng"] as? CLLocationDegrees
                                LoginViewController.user.fcmToken = Messaging.messaging().fcmToken
                                db.collection("UserData").document(LoginViewController.user.docID).updateData(["fcmToken": LoginViewController.user.fcmToken ?? ""])
                                let storage = Storage.storage()
                                let storageRef = storage.reference()
                                let imageRef = storageRef.child("Profile/" + LoginViewController.user.docID + "/profile_image.jpg")
                                imageRef.downloadURL(completion: {(url, error) in
                                    if error != nil {
                                        LoginViewController.user.image = UIImage(named: "empty_profile_image.jpg")
                                    } else {
                                        if let imageURL = url {
                                            let urlContents = try? Data(contentsOf: imageURL)
                                            if let imageData = urlContents {
                                                LoginViewController.user.image = UIImage(data: imageData)
                                            }
                                        }
                                    }
                                })
                                LoginViewController.user.updateReceivedAds()
                                self.performSegue(withIdentifier: "loginSegue", sender: nil)
                            }
                        }
                    }
                    else {
                        let verification_alert = UIAlertController(title: "로그인 실패", message: "이메일 인증이 완료되지 않았습니다. 인증 메일을 재전송 받으시겠습니까?", preferredStyle: UIAlertController.Style.alert)
                        let verification_yesAction = UIAlertAction(title: "확인", style: .default) {
                            (action) in
                            curUser.sendEmailVerification(completion: {
                                (error) in
                                if error != nil {
                                    let send_fail_alert = UIAlertController(title: "전송 실패", message: "인증 메일 전송에 실패했습니다.", preferredStyle: UIAlertController.Style.alert)
                                    let send_fail_yesAction = UIAlertAction(title: "확인", style: .default) {
                                            (action) in
                                    }
                                    send_fail_alert.addAction(send_fail_yesAction)
                                    self.present(send_fail_alert, animated: true, completion: nil)
                                }
                            })
                        }
                        let verification_noAction = UIAlertAction(title: "취소", style: .default) {
                            (action) in
                        }
                        verification_alert.addAction(verification_yesAction)
                        verification_alert.addAction(verification_noAction)
                        self.present(verification_alert, animated: true, completion: nil)
                    }
                }
                else {
                    let alert = UIAlertController(title: "로그인 실패", message: "이메일 또는 비밀번호가 틀렸습니다.", preferredStyle: UIAlertController.Style.alert)
                    let okAction = UIAlertAction(title: "확인", style: .default) {
                        (action) in
                    }
                    alert.addAction(okAction)
                    self.present(alert, animated: true, completion: nil)
                }
            }
        }
        else {
            performSegue(withIdentifier: identifier, sender: nil)
        }
        return false
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
}

extension LoginViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == emailTextField {
            passwordTextField.becomeFirstResponder()
        } else {
            passwordTextField.resignFirstResponder()
            performSegue(withIdentifier: "loginSegue", sender: nil)
        }
        return true
    }
}


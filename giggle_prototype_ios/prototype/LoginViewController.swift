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
    
    func addDocumentToCollection(db: Firestore, nickname: String, email: String) {
        var ref: DocumentReference? = nil
        ref = db.collection("UserData").addDocument(data: [
            "nickname": nickname,
            "email": email,
            "member_state": 0
        ]) { err in
            if let err = err {
                print("Error adding document: \(err)")
            } else {
                print("Document added with ID: \(ref!.documentID)")
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        signupButton.layer.cornerRadius = 6
        loginButton.layer.cornerRadius = 6
        resetPasswordButton.layer.cornerRadius = 6
        
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
                                let nickname_alert = UIAlertController(title: "닉네임 설정", message: "닉네임 설정이 필요합니다.", preferredStyle: .alert)
                                    nickname_alert.addTextField(configurationHandler: {
                                        (textField) in
                                        textField.placeholder = "닉네임을 입력해주세요."
                                    })
                                let nickname_verifyAction = UIAlertAction(title: "확인", style: .default) {
                                    (action) in
                                    db.collection("UserData").whereField("nickname", isEqualTo: nickname_alert.textFields?[0].text ?? "").getDocuments() { (querySnapshot, err2) in
                                        //닉네임 중복 확인 필요 없을 시
                                        self.addDocumentToCollection(db: db, nickname: nickname_alert.textFields?[0].text ?? "", email: self.emailTextField.text ?? "")
                                        LoginViewController.user = User.init(name: nickname_alert.textFields?[0].text ?? "", email: self.emailTextField.text ?? "", member_state: 0)
                                        LoginViewController.user.docID = querySnapshot!.documents[0].documentID
                                        let notify_alert = UIAlertController(title: "안내", message: "구직 활동을 하려면 마이 페이지에서 통합 회원 신청을 해주세요.", preferredStyle: .alert)
                                        let notify_okAction = UIAlertAction(title: "확인", style: .default, handler: { (action) in
                                            self.performSegue(withIdentifier: "loginSegue", sender: nil)
                                        })
                                        notify_alert.addAction(notify_okAction)
                                        self.present(notify_alert, animated: true, completion: nil)
                                        
                                        //닉네임 중복 확인 필요 시
                                        /*if querySnapshot!.documents.count == 0 {
                                            self.addDocumentToCollection(db: db, nickname: nickname_alert.textFields?[0].text ?? "", email: self.emailTextField.text ?? "")
                                            self.performSegue(withIdentifier: "loginSegue", sender: nil)
                                        }
                                        else {
                                            let nickname_duplicated_alert = UIAlertController(title: "닉네임 중복 확인", message: "이미 존재하는 닉네임 입니다.", preferredStyle: .alert)
                                            let nickname_duplicated_okAction = UIAlertAction(title: "확인", style: .default, handler: {
                                                (action) in
                                                self.present(nickname_alert, animated: true, completion: nil)
                                            })
                                            nickname_duplicated_alert.addAction(nickname_duplicated_okAction)
                                            self.present(nickname_duplicated_alert, animated: true, completion: nil)
                                        }*/
                                    }
                                }
                                nickname_alert.addAction(nickname_verifyAction)
                                self.present(nickname_alert, animated: true, completion: nil)
                            }
                            else {
                                let name = querySnapshot!.documents[0].data()["nickname"] as! String
                                let email = querySnapshot!.documents[0].data()["email"] as! String
                                let member_state = querySnapshot!.documents[0].data()["member_state"] as! Int
                                LoginViewController.user = User.init(name: name, email: email, member_state: member_state)
                                LoginViewController.user.docID = querySnapshot!.documents[0].documentID
                                LoginViewController.user.lat = querySnapshot!.documents[0].data()["lat"] as? CLLocationDegrees
                                LoginViewController.user.lng = querySnapshot!.documents[0].data()["lng"] as? CLLocationDegrees
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
}


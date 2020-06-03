//
//  SignUpView2Controller.swift
//  giggle
//
//  Created by 윤영신 on 2020/04/20.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class SignUpView2Controller: UIViewController, UIScrollViewDelegate {
    @IBOutlet weak var submitButton: UIButton!
    @IBOutlet weak var nicknameTextField: UITextField!
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var repasswordTextField: UITextField!
    
    @IBAction func submit(_ sender: UIButton) {
        if passwordTextField.text != repasswordTextField.text {
            showToast(message: "password doesn't match")
        }
        else {
            Auth.auth().createUser(withEmail: emailTextField.text ?? "", password: passwordTextField.text ?? "") {
                (user, error) in
                if user != nil {
                    let db = Firestore.firestore()
                    self.addDocumentToCollection(db: db, nickname: self.nicknameTextField.text ?? "", email: self.emailTextField.text ?? "")
                    
                    let alert = UIAlertController(title: "회원가입 성공", message: "작성하신 이메일로 인증 메일을 발송했습니다. 이메일 인증을 진행해주세요.", preferredStyle: UIAlertController.Style.alert)
                    let okAction = UIAlertAction(title: "확인", style: .default) {
                        (action) in
                        Auth.auth().currentUser!.sendEmailVerification(completion: {
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
                        self.dismiss(animated: true, completion: nil)
                    }
                    alert.addAction(okAction)
                    self.present(alert, animated: true, completion: nil)
                }
                else {
                    print(error!)
                    self.showToast(message: "account already exists")
                }
            }
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        navigationItem.hidesBackButton = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        submitButton.layer.cornerRadius = 6
    }
    
    func showToast(message: String) {
        let width_variable: CGFloat = 10
        let toastLabel = UILabel(frame: CGRect(x: width_variable, y: self.view.frame.size.height-100, width: view.frame.size.width-2*width_variable, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center;
        toastLabel.font = UIFont(name: "Montserrat-Light", size: 12.0)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.clipsToBounds = true
        toastLabel.layer.cornerRadius = 6
        self.view.addSubview(toastLabel)
        UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: { toastLabel.alpha = 0.0 }, completion: {(isCompleted) in toastLabel.removeFromSuperview()})
    }
    
    func addDocumentToCollection(db: Firestore, nickname: String, email: String) {
        var ref: DocumentReference? = nil
        ref = db.collection("UserData").addDocument(data: [
            "nickname": nickname,
            "email": email,
            "member_state": 0,
            "lat": -1,
            "lng": -1,
            "rating": 0
        ]) { err in
            if let err = err {
                print("Error adding document: \(err)")
            } else {
                print("Document added with ID: \(ref!.documentID)")
            }
        }
    }
}

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
    let locationManager = CLLocationManager()
    
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        signupButton.layer.cornerRadius = 6
        loginButton.layer.cornerRadius = 6
        resetPasswordButton.layer.cornerRadius = 6
        //Location Authorization Check
        let status = CLLocationManager.authorizationStatus()
        
        if status == CLAuthorizationStatus.notDetermined {
            locationManager.requestWhenInUseAuthorization()
        }
        else if status == CLAuthorizationStatus.denied || status == CLAuthorizationStatus.restricted {
            let warning = UIAlertController(title: "위치 접근 허용이 '안 함'으로 되어있습니다.", message: "[설정 - 개인 정보 보호 - 위치 서비스 - Giggle]에서 '앱을 사용하는 동안' 또는 '항상'으로 설정해주세요.", preferredStyle: UIAlertController.Style.alert)
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
                        self.performSegue(withIdentifier: "loginSegue", sender: nil)
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


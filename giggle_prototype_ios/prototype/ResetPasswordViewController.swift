//
//  ResetPasswordViewController.swift
//  giggle
//
//  Created by 윤영신 on 2020/04/21.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class ResetPasswordViewController: UIViewController {
    @IBOutlet weak var submitButton: UIButton!
    @IBOutlet weak var emailTextField: UITextField!
    
    @IBAction func sendResetEmail(_ sender: UIButton) {
        Auth.auth().sendPasswordReset(withEmail: emailTextField.text ?? "", completion: {
            (error) in
            if error != nil {
                let send_fail_alert = UIAlertController(title: "전송 실패", message: "비밀번호 재설정 메일 전송에 실패했습니다.", preferredStyle: UIAlertController.Style.alert)
                let send_fail_yesAction = UIAlertAction(title: "확인", style: .default) {
                    (action) in
                }
                send_fail_alert.addAction(send_fail_yesAction)
                self.present(send_fail_alert, animated: true, completion: nil)
            }
            else {
                let send_success_alert = UIAlertController(title: "전송 완료", message: "작성하신 이메일로 비밀번호 재설정 메일을 전송했습니다.", preferredStyle: UIAlertController.Style.alert)
                let send_success_yesAction = UIAlertAction(title: "확인", style: .default) {
                    (action) in
                    self.dismiss(animated: true, completion: nil)
                }
                send_success_alert.addAction(send_success_yesAction)
                self.present(send_success_alert, animated: true, completion: nil)
            }
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        navigationItem.hidesBackButton = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        submitButton.layer.cornerRadius = 6
    }
}

//
//  MemberIntegrationViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/03.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class MemberIntegrationViewController: UIViewController, UITextFieldDelegate {
    @IBOutlet weak var genderSegmentedControl: UISegmentedControl!
    @IBOutlet weak var ageTextField: UITextField!
    @IBOutlet weak var contactTextField: UITextField!
    @IBOutlet weak var integrationButton: UIButton!
    @IBOutlet weak var initButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("MemberIntegrationViewController : view did load")
        
        navigationItem.hidesBackButton = false
        navigationController?.isNavigationBarHidden = false
        
        genderSegmentedControl.selectedSegmentIndex = 2
        ageTextField.placeholder = "ex) 25"
        contactTextField.placeholder = "ex) 01012345678"
        contactTextField.delegate = self
        
        integrationButton.layer.cornerRadius = 6
        initButton.layer.cornerRadius = 6
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let newLength = (textField.text?.count)! + string.count - range.length
        return !(newLength > 11)
    }
    
    @IBAction func checkForm() {
        if ageTextField.text == "" {
            CommonFuncs.showToast(message: "나이 항목을 입력해주세요.", view: self.view)
        }
        else if contactTextField.text == "" {
            CommonFuncs.showToast(message: "연락처 항목을 입력해주세요.", view: self.view)
        }
        else {
            let alert = UIAlertController(title: "통합 회원 전환", message: "통합 회원으로 전환하시겠습니까?", preferredStyle: .alert)
            let alert_yesAction = UIAlertAction(title: "전환", style: .default, handler: {
                (action) in
                LoginViewController.user.gender = self.genderSegmentedControl.selectedSegmentIndex
                LoginViewController.user.age = Int(self.ageTextField.text ?? "0")
                LoginViewController.user.contact = self.contactTextField.text
                LoginViewController.user.member_state = 1
                LoginViewController.user.updateIntegerField(field: "gender", value: LoginViewController.user.gender)
                LoginViewController.user.updateIntegerField(field: "age", value: LoginViewController.user.age)
                LoginViewController.user.updateStringField(field: "contact", value: LoginViewController.user.contact)
                LoginViewController.user.updateIntegerField(field: "member_state", value: LoginViewController.user.member_state)
                LoginViewController.user.updateIntegerField(field: "hired", value: 0)
                LoginViewController.user.updateIntegerField(field: "worked", value: 0)
                let complete_alert = UIAlertController(title: "통합 회원 전환 완료", message: "통합 회원으로 전환이 완료되었습니다. 이제부터 구직 관련 기능을 이용하실 수 있습니다.", preferredStyle: .alert)
                let complete_okAction = UIAlertAction(title: "확인", style: .default, handler: {
                            (action) in
                        })
                complete_alert.addAction(complete_okAction)
                self.present(complete_alert, animated: true, completion: nil)
            })
            let alert_noAction = UIAlertAction(title: "취소", style: .default, handler: {
                (action) in
            })
            alert.addAction(alert_yesAction)
            alert.addAction(alert_noAction)
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    @IBAction func initForm(_ sender: UIButton) {
        CommonFuncs.showToast(message: "초기화되었습니다.", view: self.view)
    }
}

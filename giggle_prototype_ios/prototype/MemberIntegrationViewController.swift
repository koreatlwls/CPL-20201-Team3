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

class MemberIntegrationViewController: UIViewController {
    override func viewWillAppear(_ animated: Bool) {
        print("MemberIntegrationViewController : view will appear")
        navigationItem.hidesBackButton = false
        navigationController?.isNavigationBarHidden = false
        
        let alert = UIAlertController(title: "통합 회원 전환", message: "통합 회원으로 전환하시겠습니까?", preferredStyle: .alert)
        let alert_yesAction = UIAlertAction(title: "전환", style: .default, handler: {
            (action) in
            LoginViewController.user.member_state = 1
            LoginViewController.user.updateIntegerField(field: "member_state", value: LoginViewController.user.member_state)
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

//
//  SignUpViewController.swift
//  giggle
//
//  Created by 윤영신 on 2020/04/20.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit

class SignUpView1Controller: UIViewController {
    @IBOutlet weak var rejectButton: UIButton!
    @IBOutlet weak var agreeButton: UIButton!
    @IBOutlet weak var policyTextView: UITextView!
    
    @IBAction func rejectPolicy(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        rejectButton.layer.cornerRadius = 6
        agreeButton.layer.cornerRadius = 6
        policyTextView.layer.borderWidth = 1.0
        policyTextView.layer.cornerRadius = 10
        // Do any additional setup after loading the view.
    }
}

//
//  AddAdViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/24.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit

class AddAdViewController: UIViewController {
    @IBOutlet weak var searchButton: UIButton!
    @IBOutlet weak var inputTextField: UITextField!
    
    @IBAction func locationSearch(_ sender: UIButton) {
        let vc = AAV_MapContainerViewController(nibName: "AAV_MapContainerViewController", bundle: nil)
        vc.searchLocation(address: inputTextField.text ?? "")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
    }
    override func viewDidLoad() {
        super.viewDidLoad()
    }
}

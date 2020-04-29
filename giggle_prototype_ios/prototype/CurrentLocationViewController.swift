//
//  CurrentLocationViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/29.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit

class CurrentLocationViewController: UIViewController
{
    @IBOutlet weak var inputTextField: UITextField!
    @IBOutlet weak var searchButton: UIButton!
    
    
    @IBAction func locationSearch(_ sender: UIButton) {
        let vc = CLV_MapContainerViewController(nibName: "CLV_MapContainerViewController", bundle: nil)
        vc.searchLocation(address: inputTextField.text ?? "")
    }
}

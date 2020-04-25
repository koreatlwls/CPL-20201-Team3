//
//  MainViewController.swift
//  giggle
//
//  Created by 윤영신 on 2020/04/21.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit

class MainViewController: UIViewController {
    
}

/*import Foundation
import UIKit
import Firebase

class MainViewController: UIViewController
{
    //@IBOutlet weak var currentStateSwitch: UISwitch!
    //@IBOutlet weak var currentStateLabel: UILabel!
    //@IBOutlet weak var adButton: UIButton!
    //@IBOutlet weak var myPageButton: UIButton!
    //@IBOutlet weak var logOutButton: UIButton!
    
    /*@IBAction func touchAdButton(_ sender: UIButton) {
        //구인자
        /*if currentStateSwitch.isOn {
            self.performSegue(withIdentifier: "addAdSegue", sender: nil)
        }*/
        //구직자
        /*else {
            self.performSegue(withIdentifier: "showAdSegue", sender: nil)
        }*/
        self.performSegue(withIdentifier: "addAdSegue", sender: nil)
    }*/
    
    /*@IBAction func changeState(_ sender: UISwitch) {
        updateMainViewByState(state: sender.isOn)
    }*/
    
    /*private func updateMainViewByState(state: Bool) {
        if state {
            currentStateLabel.text = "구인자"
            adButton.titleLabel?.text = "광고 등록"
        }
        else {
            currentStateLabel.text = "구직자"
            adButton.titleLabel?.text = "광고 목록"
        }
    }*/
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //guard let navigationController = self.navigationController else { return }
        //var navigationArray = navigationController.viewControllers
        //navigationArray.removeSubrange(0..<navigationArray.count-1)
        navigationItem.hidesBackButton = true
        //adButton.layer.cornerRadius = 6;
        //myPageButton.layer.cornerRadius = 6;
        //logOutButton.layer.cornerRadius = 6;
        //updateMainViewByState(state: currentStateSwitch.isOn)
    }
}*/

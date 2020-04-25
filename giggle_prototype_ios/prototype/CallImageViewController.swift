//
//  CallImageViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/25.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit

class CallImageViewController: UIViewController {
    @IBOutlet weak var imageView: UIImageView!
    var image: UIImage!
    
    override func viewWillAppear(_ animated: Bool) {
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("CallImageViewController : view did load")
        imageView.image = image
    }
}

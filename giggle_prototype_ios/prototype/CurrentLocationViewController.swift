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
    static var childView: UIViewController!
    @IBOutlet weak var inputTextField: UITextField!
    @IBOutlet weak var mapContainerView: UIView!
    
    @IBAction func locationSearch(_ sender: UIButton) {
        let vc = CLV_MapContainerViewController(nibName: "CLV_MapContainerViewController", bundle: nil)
        vc.searchLocation(address: inputTextField.text ?? "")
    }
    
    @IBAction func setCurrentLocation(_ sender: UIButton) {
        LoginViewController.user.lat = CLV_MapContainerViewController.mapView.camera.target.latitude
        LoginViewController.user.lng = CLV_MapContainerViewController.mapView.camera.target.longitude
        
        LoginViewController.user.updateDoubleField(field: "lat", value: LoginViewController.user.lat)
        LoginViewController.user.updateDoubleField(field: "lng", value: LoginViewController.user.lng)
        CommonFuncs.showToast(message: "지정한 위치로 설정되었습니다.", view: self.view)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        
        print("CurrentLocationViewController : view will appear")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("CurrentLocationViewController : view did load")
        CurrentLocationViewController.childView = self.children[0]
        
        if LoginViewController.user.lat == nil || LoginViewController.user.lng == nil {
            let location_alert = UIAlertController(title: "활동 위치 설정", message: "활동 위치가 설정되어있지 않습니다. 현재 위치 탭에서 원하는 위치로 이동하여 상단의 설정 버튼을 눌러 활동 위치를 설정해주세요.", preferredStyle: .alert)
            let location_ok_action = UIAlertAction(title: "확인", style: .default, handler: {
                (action) in
            })
            location_alert.addAction(location_ok_action)
            self.present(location_alert, animated: true, completion: nil)
        }
    }
    
}

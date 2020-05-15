//
//  commonFuncs.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/12.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit

class CommonFuncs {
    static func showToast(message: String, view: UIView) {
        let width_variable: CGFloat = 10
        let toastLabel = UILabel(frame: CGRect(x: width_variable, y: view.frame.size.height-100, width: view.frame.size.width-2*width_variable, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center;
        toastLabel.font = UIFont(name: "Montserrat-Light", size: 12.0)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.clipsToBounds = true
        toastLabel.layer.cornerRadius = 6
        view.addSubview(toastLabel)
        UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: { toastLabel.alpha = 0.0 }, completion: {(isCompleted) in toastLabel.removeFromSuperview()})
    }
}

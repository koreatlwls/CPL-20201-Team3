//
//  ApplicantCell.swift
//  prototype
//
//  Created by 윤영신 on 2020/06/03.
//  Copyright © 2020 OSO. All rights reserved.
//

import UIKit

class ApplicantCell: UITableViewCell {
    @IBOutlet weak var applicantProfileImageView: UIImageView!
    @IBOutlet weak var applicantNameLabel: UILabel!
    @IBOutlet weak var applicantRateLabel: UILabel!
    @IBOutlet weak var applicantMessageLabel: UILabel!
    @IBOutlet weak var stateLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

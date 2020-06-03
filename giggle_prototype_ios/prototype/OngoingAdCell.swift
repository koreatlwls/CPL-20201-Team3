//
//  OngoingAdCell.swift
//  prototype
//
//  Created by 윤영신 on 2020/06/02.
//  Copyright © 2020 OSO. All rights reserved.
//

import UIKit

class OngoingAdCell: UITableViewCell {
    @IBOutlet weak var shopImageView: UIImageView!
    @IBOutlet weak var adTitleLabel: UILabel!
    @IBOutlet weak var shopDayLabel: UILabel!
    @IBOutlet weak var shopTimeLabel: UILabel!
    @IBOutlet weak var shopWageLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}

//
//  User.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/02.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation

class User {
    var name: String!
    var email: String!
    var member_state: Int!
    
    init(name: String, email: String, member_state: Int) {
        self.name = name
        self.email = email
        self.member_state = member_state
    }
}

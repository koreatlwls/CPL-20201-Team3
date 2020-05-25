//
//  Ad.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/03.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import CoreLocation

class Ad {
    var uploader: String!
    var state: Int!
    //0:구인중 1:구인완료
    
    var name: String!
    var type: String!
    var imageCount: Int!
    var images: [UIImage]!
    var latitude: Double!
    var longitude: Double!
    var range: Int!
    var recruitFieldArr: [String]!
    var recruitNumOfPeopleArr: [Int]!
    var adTitle: String!
    var workDay: Date!
    var startTime: Date!
    var endTime: Date!
    var wage: Int!
    
    var workDetail: String!
    var preferGender: Int!
    //0:남자 1:여자 2:무관
    
    var preferMinAge: Int!
    var preferMaxAge: Int!
    // 둘 다 -1 : 무관
    
    var preferInfo: String!
    
    var docID: String!
    
    init(email uploader: String, name: String, type: String, lat latitude: Double, lng longitude: Double, range: Int, title adTitle: String!, day workDay: Date!, start startTime: Date, end endTime: Date, wage: Int, workDetail: String, preferGender: Int, preferMinAge: Int, preferMaxAge: Int, preferInfo: String) {
        self.uploader = uploader
        self.name = name
        self.type = type
        self.latitude = latitude
        self.longitude = longitude
        self.range = range
        self.adTitle = adTitle
        self.workDay = workDay
        self.startTime = startTime
        self.endTime = endTime
        self.wage = wage
        self.workDetail = workDetail
        self.preferGender = preferGender
        self.preferMinAge = preferMinAge
        self.preferMaxAge = preferMaxAge
        self.preferInfo = preferInfo
        self.state = 0
    }
}

//
//  User.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/02.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import CoreLocation
import Firebase

class User {
    var name: String!
    var email: String!
    var gender: Int!
    var age: Int!
    var contact: String!
    var member_state: Int!
    var docID: String!
    var lat: CLLocationDegrees!
    var lng: CLLocationDegrees!
    let bucketURL = "gs://giggle-prototype.appspot.com"
    var fcmToken: String!
    var adsID: [String]!
    var rating: Double!
    var image: UIImage!
    var hired: Int!
    var worked: Int!
    
    init(name: String, email: String, member_state: Int, rating: Double) {
        self.name = name
        self.email = email
        self.member_state = member_state
        self.rating = rating
        adsID = [String]()
        hired = 0
        worked = 0
    }
    
    func updateReceivedAds() {
        adsID.removeAll()
        let db = Firestore.firestore()
        db.collection("UserData").document(docID).collection("ReceivedAd").getDocuments() {
            (querySnapshot, err) in
            let count = querySnapshot!.documents.count
            for index in 0..<count {
                let document = querySnapshot!.documents[index]
                let data = document.data()
                let adDocID = data["docID"] as! String
                let state = data["state"] as! Int
                db.collection("AdData").document(adDocID).getDocument() {
                    (documentSnapshot, err) in
                    let data = documentSnapshot?.data()
                    if data != nil {
                        if state == 0 {
                            self.adsID.append(adDocID)
                        }
                    }
                }
            }
        }
    }
    
    func updateIntegerField(field: String, value: Int)
    {
        let db = Firestore.firestore()
        db.collection("UserData").whereField("email", isEqualTo: email!).getDocuments() {
        (querySnapshot, err) in
            db.collection("UserData").document(self.docID).updateData([
                field: value
            ])
        }
    }
    
    func updateDoubleField(field: String, value: Double)
    {
        let db = Firestore.firestore()
        db.collection("UserData").whereField("email", isEqualTo: email!).getDocuments() {
        (querySnapshot, err) in
            db.collection("UserData").document(self.docID).updateData([
                field: value
            ])
        }
    }
    
    func updateStringField(field: String, value: String)
    {
        let db = Firestore.firestore()
        db.collection("UserData").whereField("email", isEqualTo: email!).getDocuments() {
        (querySnapshot, err) in
            db.collection("UserData").document(self.docID).updateData([
                field: value
            ])
        }
    }
}

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
    var member_state: Int!
    var docID: String!
    var lat: CLLocationDegrees!
    var lng: CLLocationDegrees!
    let bucketURL = "gs://giggle-prototype.appspot.com"
    
    init(name: String, email: String, member_state: Int) {
        self.name = name
        self.email = email
        self.member_state = member_state
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

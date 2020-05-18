//
//  ShowAdViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/24.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase
import SDWebImage

class ShowAdViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var AdTableView: UITableView!
    var ads = [Ad]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("ShowAdViewController : view did load")
        updateAds()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        print("ShowAdViewController : view will appear")
        navigationController?.isNavigationBarHidden = true
        navigationItem.hidesBackButton = true
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        print("tableView : ", ads.count)
        return ads.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell()
        cell.textLabel?.text = "\(ads[indexPath.row].name ?? "")"
        print(cell.textLabel?.text)
        return cell
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func updateAds() {
        let db = Firestore.firestore()
        db.collection("AdData").whereField("state", isEqualTo: 0).getDocuments() {
            (querySnapshot, err) in
            for index in 0..<querySnapshot!.documents.count {
                let document = querySnapshot!.documents[index]
                let data = document.data()
                let startDateString = data["startDate"] as! String
                let endDateString = data["endDate"] as! String
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "yyyy-MM-dd HH:mm"
                let startDate = dateFormatter.date(from: startDateString)
                let endDate = dateFormatter.date(from: endDateString)
                let ad = Ad.init(email: data["Uploader"] as! String, name: data["name"] as! String, type: data["type"] as! String, lat: data["latitude"] as! Double, lng: data["longitude"] as! Double, range: data["range"] as! Int, start: startDate!, end: endDate!, wage: data["wage"] as! Int, workInfo: data["workInfo"] as! String, preferGender: data["preferGender"] as! Int, preferMinAge: data["preferMinAge"] as! Int, preferMaxAge: data["preferMaxAge"] as! Int, preferInfo: data["preferInfo"] as! String)
                
                self.ads.append(ad)
                print("updateAds : ", self.ads.count)
            }
        }
        self.AdTableView.dataSource = self
        self.AdTableView.delegate = self
    }
}

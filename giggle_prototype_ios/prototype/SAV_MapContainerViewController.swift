//
//  SAV_MapContainerViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/23.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import GoogleMaps
import GooglePlaces
import CoreLocation
import AddressBookUI

class SAV_MapContainerViewController: UIViewController, CLLocationManagerDelegate, GMSMapViewDelegate {
    var marker = GMSMarker()
    let locationManager = CLLocationManager()
    let geocoder = GMSGeocoder()
    static let mapView = GMSMapView()
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        
        print("SAV_MapContainerViewController : view will appear")
        
        locationManager.startUpdatingLocation()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        self.removeFromParent()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("SAV_MapContainerViewController : view did load")
        
        navigationItem.hidesBackButton = false
        navigationController?.isNavigationBarHidden = false
        
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.delegate = self
        if CLLocationManager.authorizationStatus() == .notDetermined {
            locationManager.requestWhenInUseAuthorization()
        }
        
        SAV_MapContainerViewController.mapView.camera = GMSCameraPosition(latitude: ShowAdViewController.selectedAd.latitude, longitude: ShowAdViewController.selectedAd.longitude, zoom: 18)
        SAV_MapContainerViewController.mapView.settings.myLocationButton = true
        SAV_MapContainerViewController.mapView.isMyLocationEnabled = true
        SAV_MapContainerViewController.mapView.delegate = self
        
        self.view = SAV_MapContainerViewController.mapView
        
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: ShowAdViewController.selectedAd.latitude, longitude: ShowAdViewController.selectedAd.longitude)
        marker.title = ShowAdViewController.selectedAd.name
        marker.map = SAV_MapContainerViewController.mapView
    }
    
    static func updateCamera() {
        SAV_MapContainerViewController.mapView.camera = GMSCameraPosition(latitude: ShowAdViewController.selectedAd.latitude, longitude: ShowAdViewController.selectedAd.longitude, zoom: 18)
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        print("SAV_MapContainerViewController : did Change Authorizations")
        if CLLocationManager.locationServicesEnabled() {
            print("SAV_MapContainerViewController : location Services Enabled")
            locationManager.startUpdatingLocation()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print(error.localizedDescription)
    }
}

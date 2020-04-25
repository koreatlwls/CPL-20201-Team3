//
//  MapContainerViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/24.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import GoogleMaps
import GooglePlaces

class MapContainerViewController: UIViewController, CLLocationManagerDelegate, GMSMapViewDelegate {
    
    var mapView: GMSMapView!
    var marker = GMSMarker()
    let locationManager = CLLocationManager()
    let geocoder = GMSGeocoder()
    
    override func viewWillAppear(_ animated: Bool) {
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print("view did load")
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.delegate = self
        if CLLocationManager.authorizationStatus() == .notDetermined {
            locationManager.requestWhenInUseAuthorization()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        print("did Change Authorizations")
        if CLLocationManager.locationServicesEnabled() {
            print("location Services Enabled")
            locationManager.startUpdatingLocation()
        }
        
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print(error.localizedDescription)
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("didUpdateLocations called")
        let coor = locationManager.location?.coordinate
        if coor != nil {
            let latitude = coor!.latitude
            let longitude = coor!.longitude
            
            mapView = GMSMapView()
            let camera = GMSCameraPosition.camera(withLatitude: latitude, longitude: longitude, zoom: 13.8)
            mapView.camera = camera
        }
        else {
            mapView = GMSMapView()
            let camera = GMSCameraPosition.camera(withLatitude: 35.890059, longitude: 128.611326, zoom: 13.8)
            mapView.camera = camera
        }
        mapView.settings.myLocationButton = true
        mapView.isMyLocationEnabled = true
        self.mapView.delegate = self
        
        self.view = mapView
        
        locationManager.stopUpdatingLocation()
    }
    
    func mapView(_ mapView: GMSMapView, willMove gesture: Bool) {
        mapView.clear()
    }
    
    func mapView(_ mapView: GMSMapView, idleAt position: GMSCameraPosition) {
        geocoder.reverseGeocodeCoordinate(position.target, completionHandler: { (response, error) in
            guard error == nil else { return }
            if let result = response?.firstResult() {
                let marker = GMSMarker()
                marker.position = position.target
                marker.title = result.lines?[0]
                if (result.lines?.count ?? 0 > 1) {
                    marker.snippet = result.lines?[1]
                }
                marker.map = mapView
            }
        })
    }
}

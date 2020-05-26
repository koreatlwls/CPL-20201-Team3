//
//  AAV_MapContainerViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/04/29.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import GoogleMaps
import GooglePlaces
import CoreLocation
import AddressBookUI

class AAV_MapContainerViewController: UIViewController, CLLocationManagerDelegate, GMSMapViewDelegate {
    //var mapView: GMSMapView!
    static var mapView = GMSMapView()
    var marker = GMSMarker()
    let locationManager = CLLocationManager()
    let geocoder = GMSGeocoder()
    let address: String = ""
    var coordinate: CLLocationCoordinate2D!
    //let test_address = "1 Infinite Loop, CA, USA"
    
    func searchLocation(address: String) {
        CLGeocoder().geocodeAddressString(address, completionHandler: { (placemarks, error) in
            if error != nil {
                print(error ?? "")
                return
            }
            
            if (placemarks?.count)! > 0 {
                let placemark = placemarks?[0]
                let location = placemark?.location
                self.coordinate = location?.coordinate
                
                let camera = GMSCameraPosition(latitude: self.coordinate!.latitude, longitude: self.coordinate!.longitude, zoom: 18)
                AAV_MapContainerViewController.mapView.camera = camera
            }
        })
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        
        navigationItem.hidesBackButton = true
        navigationController?.isNavigationBarHidden = true
        AAV_MapContainerViewController.mapView.clear()
        
        locationManager.startUpdatingLocation()
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
            
            //mapView = GMSMapView()
            let camera = GMSCameraPosition.camera(withLatitude: latitude, longitude: longitude, zoom: 16)
            AAV_MapContainerViewController.mapView.camera = camera
        }
        else {
            //mapView = GMSMapView()
            let camera = GMSCameraPosition.camera(withLatitude: 35.890059, longitude: 128.611326, zoom: 16)
            AAV_MapContainerViewController.mapView.camera = camera
        }
        AAV_MapContainerViewController.mapView.settings.myLocationButton = true
        AAV_MapContainerViewController.mapView.isMyLocationEnabled = true
        AAV_MapContainerViewController.mapView.delegate = self
        
        self.view = AAV_MapContainerViewController.mapView
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

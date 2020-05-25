//
//  AdDetailViewController.swift
//  prototype
//
//  Created by 윤영신 on 2020/05/22.
//  Copyright © 2020 OSO. All rights reserved.
//

import Foundation
import UIKit
import Firebase
import SDWebImage

class AdDetailViewController: UIViewController, UIScrollViewDelegate {
    @IBOutlet weak var mapContainerView: UIView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var shopImageView: UIImageView!
    @IBOutlet weak var imagePageControl: UIPageControl!
    @IBOutlet weak var typeLabel: UILabel!
    @IBOutlet weak var adTitleLabel: UILabel!
    @IBOutlet weak var wageLabel: UILabel!
    @IBOutlet weak var workDayLabel: UILabel!
    @IBOutlet weak var startTimeLabel: UILabel!
    @IBOutlet weak var endTimeLabel: UILabel!
    @IBOutlet weak var workDetailLabel: UILabel!
    @IBOutlet weak var parentFieldStackView: UIStackView!
    @IBOutlet weak var genderLabel: UILabel!
    @IBOutlet weak var ageLabel: UILabel!
    @IBOutlet weak var preferenceDetailLabel: UILabel!
    @IBOutlet weak var shopNameLabel: UILabel!
    
    @IBAction func backToMarker(_ sender: UIButton) {
        SAV_MapContainerViewController.updateCamera()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //네비게이션 바 설정
        navigationController?.isNavigationBarHidden = false
        navigationItem.hidesBackButton = false
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.backgroundColor = .clear
        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "신청", style: .plain, target: nil, action: #selector(apply))
        
        //이미지 불러오기
        let storage = Storage.storage()
        let storageRef = storage.reference()
        for index in 1..<ShowAdViewController.selectedAd.imageCount {
            let imageRef = storageRef.child("Ad/" + ShowAdViewController.selectedAd.docID + "/shop_image_" + String(index+1) + ".jpg")
            imageRef.downloadURL(completion: {(url, error) in
                if error != nil {
                    ShowAdViewController.selectedAd.images.append(UIImage(named: "empty_profile_image.jpg")!)
                } else {
                    if let imageURL = url {
                        let urlContents = try? Data(contentsOf: imageURL)
                        if let imageData = urlContents {
                            ShowAdViewController.selectedAd.images.append(UIImage(data: imageData)!)
                        }
                    }
                }
            })
        }
        
        //페이지 컨트롤 설정
        imagePageControl.numberOfPages = ShowAdViewController.selectedAd.imageCount
        imagePageControl.currentPage = 0
        imagePageControl.pageIndicatorTintColor = UIColor.lightGray
        imagePageControl.currentPageIndicatorTintColor = UIColor.black
        
        //상세 내용 설정
        typeLabel.text = ShowAdViewController.selectedAd.type
        adTitleLabel.text = ShowAdViewController.selectedAd.adTitle
        adTitleLabel.numberOfLines = 0
        adTitleLabel.lineBreakMode = .byWordWrapping
        wageLabel.text = "\(String(ShowAdViewController.selectedAd.wage))원"
        wageLabel.clipsToBounds = true
        wageLabel.layer.cornerRadius = 6
        let dateformatter = DateFormatter()
        dateformatter.dateFormat = "yyyy년 MM월 dd일"
        workDayLabel.text = dateformatter.string(from: ShowAdViewController.selectedAd.workDay)
        dateformatter.dateFormat = "HH시 mm분"
        startTimeLabel.text = dateformatter.string(from: ShowAdViewController.selectedAd.startTime)
        endTimeLabel.text = dateformatter.string(from: ShowAdViewController.selectedAd.endTime)
        workDetailLabel.text = ShowAdViewController.selectedAd.workDetail
        workDetailLabel.numberOfLines = 0
        workDetailLabel.lineBreakMode = .byWordWrapping
        (parentFieldStackView.subviews[1].subviews[0] as! UILabel).text = ShowAdViewController.selectedAd.recruitFieldArr[0]
        (parentFieldStackView.subviews[1].subviews[1] as! UILabel).text = "\(String(ShowAdViewController.selectedAd.recruitNumOfPeopleArr[0]))명"
        for index in 1..<ShowAdViewController.selectedAd.recruitFieldArr.count {
            do {
                let newFieldStackView = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(NSKeyedArchiver.archivedData(withRootObject: parentFieldStackView.subviews[1], requiringSecureCoding: false)) as! UIStackView
                let index2 = parentFieldStackView.subviews.count
                parentFieldStackView.insertArrangedSubview(newFieldStackView, at: index2)
                (parentFieldStackView.subviews[index2].subviews[0] as! UILabel).text = ShowAdViewController.selectedAd.recruitFieldArr[index]
                (parentFieldStackView.subviews[index2].subviews[1] as! UILabel).text = "\(String(ShowAdViewController.selectedAd.recruitNumOfPeopleArr[index]))명"
            } catch {
                print(error)
            }
        }
        switch ShowAdViewController.selectedAd.preferGender {
        case 0: genderLabel.text = "남자"
            break
        case 1: genderLabel.text = "여자"
            break
        case 2: genderLabel.text = "무관"
            break
        case .none:
            break
        case .some(_):
            break
        }
        if ShowAdViewController.selectedAd.preferMinAge == -1 && ShowAdViewController.selectedAd.preferMaxAge == -1 {
            ageLabel.text = "무관"
        }
        else {
            ageLabel.text = "\(String(ShowAdViewController.selectedAd.preferMinAge)) ~ \(String(ShowAdViewController.selectedAd.preferMaxAge))세"
        }
        preferenceDetailLabel.text = ShowAdViewController.selectedAd.preferInfo
        shopNameLabel.text = ShowAdViewController.selectedAd.name
        shopNameLabel.numberOfLines = 0
        shopNameLabel.lineBreakMode = .byWordWrapping
    }
    
    private func updateScrollView() {
        shopImageView.image = ShowAdViewController.selectedAd.images[0]
        for index in 1..<ShowAdViewController.selectedAd.imageCount {
            let imageView = UIImageView()
            imageView.frame = shopImageView.frame
            imageView.frame.origin.x = imageView.frame.width*CGFloat(index)
            imageView.image = ShowAdViewController.selectedAd.images[index]
            scrollView.addSubview(imageView)
        }
        
        scrollView.isPagingEnabled = true
        scrollView.alwaysBounceVertical = false
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.delegate = self
        scrollView.contentSize = CGSize(width: scrollView.frame.width*CGFloat(ShowAdViewController.selectedAd.imageCount), height: 0)
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        imagePageControl.currentPage = Int(scrollView.contentOffset.x / scrollView.frame.width)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        updateScrollView()
    }
    
    @objc func apply() {
        
    }
}

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
        
        //
        typeLabel.text = ShowAdViewController.selectedAd.type
        adTitleLabel.text = ShowAdViewController.selectedAd.adTitle
        adTitleLabel.numberOfLines = 0
        adTitleLabel.lineBreakMode = .byWordWrapping
        wageLabel.text = "\(String(ShowAdViewController.selectedAd.wage))원"
        wageLabel.clipsToBounds = true
        wageLabel.layer.cornerRadius = 6
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

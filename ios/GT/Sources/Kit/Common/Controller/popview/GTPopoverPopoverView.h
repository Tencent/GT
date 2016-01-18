//
//  GTPopoverPopoverView.h
//  GTKit
//
//  Created  Saito Takashi on 5/10/12.
//  Copyright (c) 2012 synetics ltd. All rights reserved.
//
// https://github.com/takashisite/TSPopover
//
#ifndef GT_DEBUG_DISABLE
#import <UIKit/UIKit.h>
#import "GTPopoverController.h"

@interface GTPopoverPopoverView : UIView

@property (nonatomic, assign) int cornerRadius;
@property (nonatomic, assign) CGPoint arrowPoint;
@property (nonatomic, assign) BOOL isGradient;
@property (nonatomic, strong) UIColor *baseColor;
@property (nonatomic, readwrite) GTPopoverArrowDirection arrowDirection;
@property (nonatomic, readwrite) GTPopoverArrowPosition arrowPosition;



@end
#endif

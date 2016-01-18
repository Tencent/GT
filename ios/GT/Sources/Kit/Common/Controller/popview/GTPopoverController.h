//
//  GTPopoverViewController.h
//  GTKit
//
//  Created  Saito Takashi on 5/9/12.
//  Copyright (c) 2012 synetics ltd. All rights reserved.
//
// https://github.com/takashisite/TSPopover
//
#ifndef GT_DEBUG_DISABLE
#import <UIKit/UIKit.h>
#import "GTPopoverTouchesDelegate.h"
#import "GTPopoverTouchView.h"

enum {
    GTPopoverArrowDirectionTop = 0,
	GTPopoverArrowDirectionRight,
    GTPopoverArrowDirectionBottom,
    GTPopoverArrowDirectionLeft
};
typedef NSUInteger GTPopoverArrowDirection;

enum {
    GTPopoverArrowPositionVertical = 0,
    GTPopoverArrowPositionHorizontal
};
typedef NSUInteger GTPopoverArrowPosition;

@class GTPopoverPopoverView;

@class GTPopoverController;

@protocol GTPopoverControllerDelegate

- (void) dismissPopoverController:(GTPopoverController* )controller;

@end

@interface GTPopoverController : UIViewController <GTPopoverTouchesDelegate>
{
    GTPopoverTouchView      *_touchView;
    GTPopoverPopoverView    *_popoverView;
    GTPopoverArrowDirection  _arrowDirection;
    CGRect  _screenRect;
    int     _titleLabelheight;
    
    id <GTPopoverControllerDelegate> _popDelegate;
}

@property (nonatomic, retain) UIViewController *contentViewController;
@property (nonatomic, retain) UIView *contentView;
@property (nonatomic, retain) NSString *titleText;
@property (nonatomic, retain) UIColor *titleColor;
@property (nonatomic, retain) UIFont *titleFont;
@property (nonatomic, retain) UIColor *popoverBaseColor;
@property (nonatomic, assign) int cornerRadius;
@property (nonatomic, readwrite) GTPopoverArrowPosition arrowPosition;
@property (nonatomic, assign) BOOL popoverGradient;
@property (nonatomic, assign) id <GTPopoverControllerDelegate> popDelegate;

- (id)initWithContentViewController:(UIViewController*)viewController;
- (id)initWithView:(UIView*)view;

- (void)showPopoverWithViewController:(UIViewController *)vc forEvent:(UIEvent*)senderEvent;

- (void)showPopoverWithCell:(UITableViewCell*)senderCell;
- (void)showPopoverWithRect:(CGRect)senderRect;
- (void)view:(UIView*)view touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event;
- (void)dismissPopoverAnimatd:(BOOL)animated;

@end
#endif

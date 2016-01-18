//
//  GTPopoverTouchDelegate.h
//
//  Created  Saito Takashi on 5/9/12.
//  Copyright (c) 2012 synetics ltd. All rights reserved.
//
// https://github.com/takashisite/TSPopover
//
#ifndef GT_DEBUG_DISABLE
#import <UIKit/UIKit.h>


@protocol GTPopoverTouchesDelegate

@optional
- (void)view:(UIView*)view touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event;

@end
#endif

//
//  GTProgressHUD.h
//  GTKit
//
//  SVProgressHUD, https://github.com/TransitApp/SVProgressHUD
//
//  Copyright (c) 2011-2014 Sam Vermette and contributors. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <AvailabilityMacros.h>

enum {
    GTProgressHUDMaskTypeNone = 1, // allow user interactions while HUD is displayed
    GTProgressHUDMaskTypeClear, // don't allow
    GTProgressHUDMaskTypeBlack, // don't allow and dim the UI in the back of the HUD
    GTProgressHUDMaskTypeGradient // don't allow and dim the UI with a a-la-alert-view bg gradient
};

typedef NSUInteger GTProgressHUDMaskType;

@interface GTProgressHUD : UIView

- (void)show;
- (void)showWithStatus:(NSString*)status;
- (void)showWithStatus:(NSString*)status maskType:(GTProgressHUDMaskType)maskType;
- (void)showWithMaskType:(GTProgressHUDMaskType)maskType;

- (void)showSuccessWithStatus:(NSString*)string;
- (void)showSuccessWithStatus:(NSString *)string duration:(NSTimeInterval)duration;
- (void)showErrorWithStatus:(NSString *)string;
- (void)showErrorWithStatus:(NSString *)string duration:(NSTimeInterval)duration;

- (void)showWithString:(NSString *)string duration:(NSTimeInterval)duration;

- (void)setStatus:(NSString*)string; // change the HUD loading status while it's showing

- (void)dismiss; // simply dismiss the HUD with a fade+scale out animation
- (void)dismissWithString:(NSString*)string afterDelay:(NSTimeInterval)seconds;
- (void)dismissWithSuccess:(NSString*)successString; // also displays the success icon image
- (void)dismissWithSuccess:(NSString*)successString afterDelay:(NSTimeInterval)seconds;
- (void)dismissWithError:(NSString*)errorString; // also displays the error icon image
- (void)dismissWithError:(NSString*)errorString afterDelay:(NSTimeInterval)seconds;

- (BOOL)isVisible;

+ (void)showWithString:(NSString *)string;

@end

//
//  UIScreen+Bounds.h
//
//
//  Created by navyzhou on 16/3/15.
//  E-mail: woshizhouhaijun@163.com
//

#import <UIKit/UIKit.h>

@interface UIScreen (Bounds)

- (CGRect)PortraitFullScreenBounds;
- (CGRect)PortraitScreenBounds;

- (CGRect)screenBounds;
- (CGRect)fullScreenBounds;

@end

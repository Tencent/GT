//
//  UIScreen+Bounds.m
//  
//
//  Created by navyzhou on 16/3/15.
//  E-mail: woshizhouhaijun@163.com
//

#import "UIScreen+Bounds.h"

@implementation UIScreen (Bounds)

- (CGRect)PortraitFullScreenBounds
{
    CGRect rectScreen = [UIScreen mainScreen].bounds;
    CGFloat widthLandscape = rectScreen.size.width < rectScreen.size.height ? rectScreen.size.width : rectScreen.size.height;
    CGFloat heightLandscape = rectScreen.size.width < rectScreen.size.height ? rectScreen.size.height : rectScreen.size.width;
    return CGRectMake(0, 0, widthLandscape, heightLandscape);
}

- (CGRect)PortraitScreenBounds
{
    CGRect rectScreen = [UIScreen mainScreen].applicationFrame;
    CGFloat widthLandscape = rectScreen.size.width < rectScreen.size.height ? rectScreen.size.width : rectScreen.size.height;
    CGFloat heightLandscape = rectScreen.size.width < rectScreen.size.height ? rectScreen.size.height : rectScreen.size.width;
    return CGRectMake(rectScreen.origin.x, rectScreen.origin.y, widthLandscape, heightLandscape);
}

- (CGRect)fullScreenBounds
{
    return [self PortraitFullScreenBounds];
}

- (CGRect)screenBounds
{
    return [self PortraitScreenBounds];
}

@end

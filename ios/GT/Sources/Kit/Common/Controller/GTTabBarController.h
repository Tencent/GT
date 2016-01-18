//
//  GTTabBarController.h
//  GTKit
//
//  Created   on 13-3-18.
//  Created by Levey Zhu on 12/15/10.
//  Copyright 2010 SlyFairy. All rights reserved.
//
#ifndef GT_DEBUG_DISABLE
#import <UIKit/UIKit.h>
#import "GTTabBar.h"
#import "GTUINavigationController.h"

@class UITabBarController;
@protocol GTTabBarControllerDelegate;
@interface GTTabBarController : UIViewController <GTTabBarDelegate>
{
	GTTabBar    *_tabBar;
	UIView      *_containerView;
	UIView		*_transitionView;
	id<GTTabBarControllerDelegate> _delegate;
	NSMutableArray *_viewControllers;
	NSUInteger      _selectedIndex;
	
	BOOL _tabBarTransparent;
	BOOL _tabBarHidden;
    
    NSInteger animateDriect;
}

@property(nonatomic, retain) NSMutableArray *viewControllers;

@property(nonatomic, assign) GTUINavigationController *selectedViewController;
@property(nonatomic) NSUInteger selectedIndex;

// Apple is readonly
@property (nonatomic, assign) GTTabBar *tabBar;
@property (nonatomic, assign) id<GTTabBarControllerDelegate> delegate;


// Default is NO, if set to YES, content will under tabbar
@property (nonatomic, assign) BOOL tabBarTransparent;
@property (nonatomic, assign) BOOL tabBarHidden;

@property(nonatomic, assign) NSInteger animateDriect;

- (id)initWithViewControllers:(NSArray *)vcs titleArray:(NSArray *)arr withIndex:(NSUInteger)index;

- (void)hidesTabBar:(BOOL)yesOrNO animated:(BOOL)animated;

@end


@protocol GTTabBarControllerDelegate <NSObject>
@optional
- (BOOL)tabBarController:(GTTabBarController *)tabBarController shouldSelectViewController:(UIViewController *)viewController;
- (void)tabBarController:(GTTabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController;
@end

@interface UIViewController (GTTabBarControllerSupport)
@property(nonatomic, readonly) GTTabBarController *GTTabBarController;
@end


#endif

//
//  GTTabBarController.m
//  GTKit
//
//  Created   on 13-3-18.
//  Created by Levey Zhu on 12/15/10.
//  Copyright 2010 SlyFairy. All rights reserved.
//
#ifndef GT_DEBUG_DISABLE
#import "GTTabBarController.h"
#import "GTDebugDef.h"
#import "GTUIViewController.h"
#import "GTUtility.h"
#import "GTMTA.h"

static GTTabBarController *g_gtTabBarController;


@interface GTTabBarController (private)
- (void)displayViewAtIndex:(NSUInteger)index;
@end

@implementation GTTabBarController

@synthesize delegate = _delegate;
@synthesize selectedViewController = _selectedViewController;
@synthesize viewControllers = _viewControllers;
@synthesize selectedIndex = _selectedIndex;
@synthesize tabBarHidden = _tabBarHidden;
@synthesize animateDriect;

#pragma mark - lifecycle
- (id)initWithViewControllers:(NSArray *)vcs titleArray:(NSArray *)arr withIndex:(NSUInteger)index
{
	self = [super init];
	if (self != nil)
	{
		_viewControllers = [[NSMutableArray arrayWithArray:vcs] retain];
		
        _containerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, M_GT_SCREEN_WIDTH, M_GT_SCREEN_HEIGHT)];
        
        CGRect frame = _containerView.bounds;
        frame.size.height -= M_GT_TARBAR_HEIGHT;
        _transitionView = [[UIView alloc] initWithFrame:frame];

        _transitionView.backgroundColor = [UIColor clearColor];
		
		_tabBar = [[GTTabBar alloc] initWithFrame:CGRectMake(0, _containerView.frame.size.height - M_GT_TARBAR_HEIGHT, M_GT_SCREEN_WIDTH, M_GT_TARBAR_HEIGHT) buttonTitles:arr];
		_tabBar.delegate = self;
		
        g_gtTabBarController = self;
        animateDriect = 0;
        
        // 这里先设置_selectedIndex值，保证里面页面显示不做页面统计，第一次进入由这里进行统计
        _selectedIndex = index;
        [self setSelectedIndex:index];
        
        // 页面统计
        GTUINavigationController *selectedVC = [self selectedViewController];
        if ([selectedVC.viewControllers count] > 0) {
            [GTMTA trackPageViewBegin:NSStringFromClass([[selectedVC.viewControllers objectAtIndex:0] class])];
//            NSLog(@"trackPageViewBegin:%@", NSStringFromClass([[selectedVC.viewControllers objectAtIndex:0] class]));
        }
        
	}
	return self;
}

//
//- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
//{
//    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
//    if (self) {
//        // Custom initialization
//    }
//    return self;
//}



- (void)loadView
{
	[super loadView];
	
	[_containerView addSubview:_transitionView];
	[_containerView addSubview:_tabBar];
	self.view = _containerView;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)viewDidUnload
{
	[super viewDidUnload];
	
	self.tabBar = nil;
	self.viewControllers = nil;
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc
{
    // 页面统计
    GTUINavigationController *selectedVC = [self selectedViewController];
    if ([selectedVC.viewControllers count] > 0) {
        [GTMTA trackPageViewEnd:NSStringFromClass([[selectedVC.viewControllers objectAtIndex:0] class])];
//        NSLog(@"trackPageViewEnd:%@", NSStringFromClass([[selectedVC.viewControllers objectAtIndex:0] class]));
    }
    
    _tabBar.delegate = nil;
	[_tabBar release];
    
    [_containerView release];
    [_transitionView release];
	[_viewControllers release];
    [super dealloc];
}

//iOS7
- (BOOL)prefersStatusBarHidden
{
    return NO;
}

#pragma mark -

- (BOOL)shouldAutorotate
{
    return NO;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


#pragma mark - instant methods

- (GTTabBar *)tabBar
{
	return _tabBar;
}

- (BOOL)tabBarTransparent
{
	return _tabBarTransparent;
}

- (void)setTabBarTransparent:(BOOL)yesOrNo
{
	if (yesOrNo == YES)
	{
		_transitionView.frame = _containerView.bounds;
	}
	else
	{
		_transitionView.frame = CGRectMake(0, 0, M_GT_SCREEN_WIDTH, _containerView.frame.size.height - M_GT_TARBAR_HEIGHT);
	}
}




- (void)hidesTabBar:(BOOL)yesOrNO animated:(BOOL)animated
{
	NSUInteger height = 0;
    if ([[GTUtility sharedInstance] systemVersion] >= 7)
    {
        //iOS7上电池栏的20高度计算在viewcontroller里
        height = 20;
    }
    
    if (yesOrNO == YES)
	{
        if (self.tabBar.frame.origin.y + height == self.view.frame.size.height)
		{
			return;
		}
	}
	else
	{
		if (self.tabBar.frame.origin.y <= self.view.frame.size.height - M_GT_TARBAR_HEIGHT)
		{
            return;
		}
	}
	
	if (animated == YES)
	{
		[UIView beginAnimations:nil context:NULL];
		[UIView setAnimationDuration:0.3f];
		if (yesOrNO == YES)
		{
			self.tabBar.frame = CGRectMake(self.tabBar.frame.origin.x, self.tabBar.frame.origin.y + M_GT_TARBAR_HEIGHT, self.tabBar.frame.size.width, self.tabBar.frame.size.height);
		}
		else
		{
			self.tabBar.frame = CGRectMake(self.tabBar.frame.origin.x, self.tabBar.frame.origin.y - M_GT_TARBAR_HEIGHT, self.tabBar.frame.size.width, self.tabBar.frame.size.height);
            
		}
        
		[UIView commitAnimations];
	}
	else
	{
		if (yesOrNO == YES)
		{
			self.tabBar.frame = CGRectMake(self.tabBar.frame.origin.x, self.tabBar.frame.origin.y + M_GT_TARBAR_HEIGHT, self.tabBar.frame.size.width, self.tabBar.frame.size.height);
		}
		else
		{
			self.tabBar.frame = CGRectMake(self.tabBar.frame.origin.x, self.tabBar.frame.origin.y - M_GT_TARBAR_HEIGHT, self.tabBar.frame.size.width, self.tabBar.frame.size.height);
		}
        
	}

}


- (NSUInteger)selectedIndex
{
	return _selectedIndex;
}
- (GTUINavigationController *)selectedViewController
{
    return [_viewControllers objectAtIndex:_selectedIndex];
}

-(void)setSelectedIndex:(NSUInteger)index
{
    [self displayViewAtIndex:index];
    [_tabBar selectTabAtIndex:index];
}

#pragma mark - Private methods
- (void)displayViewAtIndex:(NSUInteger)index
{
    // Before change index, ask the delegate should change the index.
    if ([_delegate respondsToSelector:@selector(tabBarController:shouldSelectViewController:)])
    {
        if (![_delegate tabBarController:self shouldSelectViewController:[self.viewControllers objectAtIndex:index]])
        {
            return;
        }
    }
    // If target index if equal to current index, do nothing.
    if (_selectedIndex == index && [[_transitionView subviews] count] != 0)
    {
        return;
    }
    
    GTUINavigationController *oldSelectedVC = [self selectedViewController];
    [oldSelectedVC viewWillDisappear:YES];
    [oldSelectedVC.view setHidden:YES];
    [oldSelectedVC viewDidDisappear:YES];
    
	GTUINavigationController *selectedVC = [self.viewControllers objectAtIndex:index];
    [selectedVC viewWillAppear:YES];
    [selectedVC.view setHidden:NO];
    [selectedVC viewDidAppear:YES];
	
//    NSLog(@"_selectedIndex:%lu index:%lu", (unsigned long)_selectedIndex, (unsigned long)index);
    // 统计页面次数
    if (_selectedIndex != index) {
        if ([oldSelectedVC.viewControllers count] > 0) {
            [GTMTA trackPageViewEnd:NSStringFromClass([[oldSelectedVC.viewControllers objectAtIndex:0] class])];
//            NSLog(@"trackPageViewEnd:%@", NSStringFromClass([[oldSelectedVC.viewControllers objectAtIndex:0] class]));
        }
        
        if ([selectedVC.viewControllers count] > 0) {
            [GTMTA trackPageViewBegin:NSStringFromClass([[selectedVC.viewControllers objectAtIndex:0] class])];
//            NSLog(@"trackPageViewBegin:%@", NSStringFromClass([[selectedVC.viewControllers objectAtIndex:0] class]));
        }
    }
    
    
    _selectedIndex = index;
    
    
	selectedVC.view.frame = _transitionView.frame;
	if ([selectedVC.view isDescendantOfView:_transitionView])
	{
		[_transitionView bringSubviewToFront:selectedVC.view];
	}
	else
	{
		[_transitionView addSubview:selectedVC.view];
	}
    
    // Notify the delegate, the viewcontroller has been changed.
    if ([_delegate respondsToSelector:@selector(tabBarController:didSelectViewController:)])
    {
        [_delegate tabBarController:self didSelectViewController:selectedVC];
    }
    
}

#pragma mark - tabBar delegates
- (void)tabBar:(GTTabBar *)tabBar didSelectIndex:(NSInteger)index
{
	if (self.selectedIndex == index) {
        UINavigationController *nav = [self.viewControllers objectAtIndex:index];
        [nav popToRootViewControllerAnimated:YES];
    }else {
        [self displayViewAtIndex:index];
    }
}

@end


#endif

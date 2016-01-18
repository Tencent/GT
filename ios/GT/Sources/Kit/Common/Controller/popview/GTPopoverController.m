//
//  GTPopoverViewController.m
//  GTKit
//
//  Created  Saito Takashi on 5/9/12.
//  Copyright (c) 2012 synetics ltd. All rights reserved.
//
// https://github.com/takashisite/TSPopover
//

#ifndef GT_DEBUG_DISABLE
#import "GTPopoverController.h"
#import "GTPopoverPopoverView.h"
#import <QuartzCore/QuartzCore.h>
#import "GTDebugDef.h"


#define CORNER_RADIUS 0
#define MARGIN 0
#define OUTER_MARGIN 0
#define TITLE_LABEL_HEIGHT 25
#define ARROW_SIZE 20
#define ARROW_MARGIN 0

@interface GTPopoverController ()

@end

@implementation GTPopoverController

@synthesize contentViewController;
@synthesize contentView;
@synthesize cornerRadius;
@synthesize titleText;
@synthesize titleColor;
@synthesize titleFont;
@synthesize arrowPosition;
@synthesize popoverBaseColor;
@synthesize popoverGradient;
@synthesize popDelegate = _popDelegate;

- (id)init {
    
	if ((self = [super init])) {
        self.cornerRadius = CORNER_RADIUS;
        self.titleColor = [UIColor whiteColor];
        self.titleFont = [UIFont boldSystemFontOfSize:14];
        self.view.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6];
        self.arrowPosition = GTPopoverArrowPositionVertical;
        self.popoverBaseColor = M_GT_CELL_BKGD_COLOR;
        self.popoverGradient = YES;
        _screenRect = [[UIScreen mainScreen] bounds];
        if(self.interfaceOrientation == UIInterfaceOrientationLandscapeLeft || self.interfaceOrientation == UIInterfaceOrientationLandscapeRight){
            _screenRect.size.width = [[UIScreen mainScreen] bounds].size.height;
            _screenRect.size.height = [[UIScreen mainScreen] bounds].size.width;
        }
        self.view.frame = _screenRect;
        _screenRect.origin.y = 0;
        _screenRect.size.height = _screenRect.size.height-20;   
        
        _titleLabelheight = 0;
        
	}
	return self;
}

- (id)initWithContentViewController:(UIViewController*)viewController
{
    self = [self init];
    if (self) {
        self.contentViewController = viewController;
        self.contentView = viewController.view;
    }
    
    return self;
}

- (id)initWithView:(UIView*)view
{
    self = [self init];
    if (self) {
        self.contentView = view;
    }
    
    return self;   
}

- (void)dealloc
{
    self.contentViewController = nil;
    self.titleText = nil;
    self.titleColor = nil;
    self.titleFont = nil;
    self.popoverBaseColor = nil;
    
    M_GT_SAFE_FREE(_popoverView);
    
    if (_touchView != nil) {
        [_touchView setDelegate:nil];
        [_touchView release];
        _touchView = nil;
    }
    [super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (void)showPopoverWithViewController:(UIViewController *)vc forEvent:(UIEvent*)senderEvent
{
    UIView *senderView = [[senderEvent.allTouches anyObject] view];
    CGPoint applicationFramePoint = CGPointMake(_screenRect.origin.x,0-_screenRect.origin.y);
    CGPoint senderLocationInWindowPoint = [vc.view convertPoint:applicationFramePoint fromView:senderView];
    CGRect senderFrame = [[[senderEvent.allTouches anyObject] view] frame];
    senderFrame.origin.x = senderLocationInWindowPoint.x;
    senderFrame.origin.y = senderLocationInWindowPoint.y;
    CGPoint senderPoint = [self senderPointFromSenderRect:senderFrame];
    
    [self showPopoverWithPoint:senderPoint forViewController:vc];
}

- (void)showPopoverWithCell:(UITableViewCell*)senderCell
{
    UIView *senderView = senderCell.superview;
    CGPoint applicationFramePoint = CGPointMake(_screenRect.origin.x,0-_screenRect.origin.y);
    CGPoint senderLocationInWindowPoint = [[[UIApplication sharedApplication] keyWindow] convertPoint:applicationFramePoint fromView:senderView];
    CGRect senderFrame = senderCell.frame;
    senderFrame.origin.x = senderLocationInWindowPoint.x;
    senderFrame.origin.y = senderLocationInWindowPoint.y + senderFrame.origin.y;
    CGPoint senderPoint = [self senderPointFromSenderRect:senderFrame];
    [self showPopoverWithPoint:senderPoint forViewController:nil];
}

- (void)showPopoverWithRect:(CGRect)senderRect
{

    CGPoint senderPoint = [self senderPointFromSenderRect:senderRect];
    [self showPopoverWithPoint:senderPoint forViewController:nil];
}

- (void)showPopoverWithPoint:(CGPoint)senderPoint forViewController:(UIViewController *)vc
{
    if(self.titleText){
        _titleLabelheight = TITLE_LABEL_HEIGHT;
    }
    if (_touchView != nil) {
        [_touchView setDelegate:nil];
        [_touchView release];
        _touchView = nil;
    }
    _touchView = [[GTPopoverTouchView alloc] init];
    _touchView.frame = self.view.frame;
    
    [_touchView setDelegate:self];
    
    [self.view addSubview:_touchView];
    CGRect contentViewFrame = [self contentFrameRect:self.contentView.frame senderPoint:senderPoint];
    
    int backgroundPositionX = 0;
    int backgroundPositionY = 0;
    if(_arrowDirection == GTPopoverArrowDirectionLeft){
        backgroundPositionX = ARROW_SIZE;
    }
    if(_arrowDirection == GTPopoverArrowDirectionTop){
        backgroundPositionY = ARROW_SIZE;
    }
    
    UILabel *titleLabel = nil;
    if(self.titleText){
        titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(backgroundPositionX, backgroundPositionY, contentViewFrame.size.width+MARGIN*2, TITLE_LABEL_HEIGHT+MARGIN)];
        titleLabel.textColor = self.titleColor;
        titleLabel.text = self.titleText;
        titleLabel.backgroundColor = [UIColor clearColor];
        titleLabel.textAlignment = NSTextAlignmentCenter;
        titleLabel.font = self.titleFont;
    }
    contentViewFrame.origin.x = backgroundPositionX+MARGIN;
    contentViewFrame.origin.y = backgroundPositionY+_titleLabelheight+MARGIN;


    self.contentView.frame = contentViewFrame;
    CALayer * contentViewLayer = [self.contentView layer];
    [contentViewLayer setMasksToBounds:YES];
    [contentViewLayer setCornerRadius:self.cornerRadius];
    
    _popoverView = [[GTPopoverPopoverView alloc] init];
    _popoverView.arrowDirection = _arrowDirection;
    _popoverView.arrowPosition = self.arrowPosition;
    _popoverView.arrowPoint = senderPoint;
    _popoverView.alpha = 0;
    _popoverView.frame = [self popoverFrameRect:contentViewFrame senderPoint:senderPoint];
    _popoverView.cornerRadius = self.cornerRadius;
    _popoverView.baseColor = self.popoverBaseColor;
    _popoverView.isGradient = self.popoverGradient;
    [_popoverView addSubview:self.contentView];
    
    if (titleLabel) {
        [_popoverView addSubview:titleLabel];
        [titleLabel release];
    }
    
    CALayer* layer = _popoverView.layer;
    layer.shadowOffset = CGSizeMake(0, 2);
    layer.shadowColor = [[UIColor blackColor] CGColor];
    layer.shadowOpacity = 0.5;
    
    [self.view addSubview:_popoverView];
    
    if (vc) {
        [vc.view addSubview:self.view];
    } else {
        UIWindow *appWindow = [[UIApplication sharedApplication] keyWindow];
        //[appWindow addSubview:self.view];
        
        [appWindow.rootViewController.view addSubview:self.view];
    }
    
    [UIView animateWithDuration:0.0
                          delay:0.0
                        options:UIViewAnimationOptionAllowAnimatedContent
                     animations:^{
                         _popoverView.alpha = 1;
                     }
                     completion:^(BOOL finished) {
                     }
     ];
    
}

- (void)view:(UIView*)view touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event
{
    [self dismissPopoverAnimatd:NO];
    
    if (_popDelegate) {
        [_popDelegate dismissPopoverController:self];
    }
}


- (void)dismissPopoverAnimatd:(BOOL)animated
{
    if (self.view) {
        if(animated) {
            [UIView animateWithDuration:0.2
                                  delay:0.0
                                options:UIViewAnimationOptionAllowAnimatedContent
                             animations:^{
                                 _popoverView.alpha = 0;
                             }
                             completion:^(BOOL finished) {
                                 [self.contentViewController viewDidDisappear:animated];
                                 M_GT_SAFE_FREE(_popoverView);
                                 [self.view removeFromSuperview];
                                 self.contentViewController = nil;
                                 self.titleText = nil;
                                 self.titleColor = nil;
                                 self.titleFont = nil;
                                 if (_touchView != nil) {
                                     [_touchView setDelegate:nil];
                                     [_touchView release];
                                     _touchView = nil;
                                 }
                             }
             ];
        }else{
            [self.contentViewController viewDidDisappear:animated];
            M_GT_SAFE_FREE(_popoverView);
            [self.view removeFromSuperview];
            self.contentViewController = nil;
            self.titleText = nil;
            self.titleColor = nil;
            self.titleFont = nil;
            if (_touchView != nil) {
                [_touchView setDelegate:nil];
                [_touchView release];
                _touchView = nil;
            }
        }
        
    }
}

- (CGRect)contentFrameRect:(CGRect)contentFrame senderPoint:(CGPoint)senderPoint
{
    CGRect contentFrameRect = contentFrame;
    float screenWidth = _screenRect.size.width;
    float screenHeight = _screenRect.size.height - _screenRect.origin.y;

    contentFrameRect.origin.x = MARGIN;
    contentFrameRect.origin.y = MARGIN;
    
    float statusBarHeight = [[UIApplication sharedApplication] statusBarFrame].size.height;


    if(self.arrowPosition == GTPopoverArrowPositionVertical){
        if(contentFrameRect.size.width > self.view.frame.size.width - (OUTER_MARGIN*2+MARGIN*2)){
            contentFrameRect.size.width = self.view.frame.size.width - (OUTER_MARGIN*2+MARGIN*2);
        }
        
        float popoverY;
        float popoverHeight = contentFrameRect.size.height+_titleLabelheight+(ARROW_SIZE+MARGIN*2);
        
        if(_arrowDirection == GTPopoverArrowDirectionTop){
            popoverY = senderPoint.y+ARROW_MARGIN;
            if((popoverY+popoverHeight) > screenHeight){
                contentFrameRect.size.height = screenHeight - (_screenRect.origin.y + popoverY + _titleLabelheight + (OUTER_MARGIN*2+MARGIN*2));
            }
        }
        
        if(_arrowDirection == GTPopoverArrowDirectionBottom){
            popoverY = senderPoint.y - ARROW_MARGIN;
            if((popoverY-popoverHeight) < statusBarHeight){
                contentFrameRect.size.height = popoverY - (statusBarHeight + ARROW_SIZE + _screenRect.origin.y + _titleLabelheight + (OUTER_MARGIN+MARGIN*2));
            }
        }
    }else if(self.arrowPosition == GTPopoverArrowPositionHorizontal){
        if(contentFrameRect.size.height > screenHeight - (OUTER_MARGIN*2+MARGIN*2)){
            contentFrameRect.size.height = screenHeight - (OUTER_MARGIN*2+MARGIN*2);
        }
        
        float popoverX;
        float popoverWidth = contentFrameRect.size.width+(ARROW_SIZE+MARGIN*2);
        
        if(_arrowDirection == GTPopoverArrowDirectionLeft){
            popoverX = senderPoint.x + ARROW_MARGIN;
            if((popoverX+popoverWidth)> screenWidth - (OUTER_MARGIN*2+MARGIN*2)){
                contentFrameRect.size.width = screenWidth - popoverX - ARROW_SIZE - (OUTER_MARGIN*2+MARGIN*2);
            }
        }
        
        if(_arrowDirection == GTPopoverArrowDirectionRight){
            popoverX = senderPoint.x - ARROW_MARGIN;
            if((popoverX-popoverWidth) < _screenRect.origin.x+(OUTER_MARGIN*2+MARGIN*2)){
                contentFrameRect.size.width = popoverX - ARROW_SIZE - (OUTER_MARGIN*2+MARGIN*2);
            }
        }
        
    }
    
    return contentFrameRect;
}


- (CGRect)popoverFrameRect:(CGRect)contentFrame senderPoint:(CGPoint)senderPoint
{
    CGRect popoverRect = CGRectZero;
    float popoverWidth;
    float popoverHeight;
    float popoverX;
    float popoverY;

    if(self.arrowPosition == GTPopoverArrowPositionVertical){
        
        popoverWidth = contentFrame.size.width+MARGIN*2;
        popoverHeight = contentFrame.size.height+_titleLabelheight+(ARROW_SIZE+MARGIN*2);

        popoverX = senderPoint.x - (popoverWidth/2);
        if(popoverX < OUTER_MARGIN) {
            popoverX = OUTER_MARGIN;
        } else if((popoverX + popoverWidth)>self.view.frame.size.width) {
            popoverX = self.view.frame.size.width - (popoverWidth+OUTER_MARGIN);
        }
        
        if(_arrowDirection == GTPopoverArrowDirectionBottom){
            popoverY = senderPoint.y - popoverHeight - ARROW_MARGIN;
        }else{
            popoverY = senderPoint.y + ARROW_MARGIN;
        }
        
        popoverRect = CGRectMake(popoverX, popoverY, popoverWidth, popoverHeight);
        
    }else if(self.arrowPosition == GTPopoverArrowPositionHorizontal){
        
        popoverWidth = contentFrame.size.width+ARROW_SIZE+MARGIN*2;
        popoverHeight = contentFrame.size.height+_titleLabelheight+MARGIN*2;

        if(_arrowDirection == GTPopoverArrowDirectionRight){
            popoverX = senderPoint.x - popoverWidth - ARROW_MARGIN;
        }else{
            popoverX = senderPoint.x + ARROW_MARGIN;
        }
        
        popoverY = senderPoint.y - (popoverHeight/2);
        if(popoverY < OUTER_MARGIN){
            popoverY = OUTER_MARGIN;
        }else if((popoverY + popoverHeight)>self.view.frame.size.height){
            popoverY = self.view.frame.size.height - (popoverHeight+OUTER_MARGIN);
        }
        
        popoverRect = CGRectMake(popoverX, popoverY, popoverWidth, popoverHeight);

    }


    return popoverRect;
    
}

- (CGPoint)senderPointFromSenderRect:(CGRect)senderRect
{
    CGPoint senderPoint = CGPointMake(0, 0);
    [self checkArrowPosition:senderRect];
    
    if(_arrowDirection == GTPopoverArrowDirectionTop){
        senderPoint = CGPointMake(senderRect.origin.x + (senderRect.size.width/2), senderRect.origin.y + senderRect.size.height);
    }else if(_arrowDirection == GTPopoverArrowDirectionBottom){
        senderPoint = CGPointMake(senderRect.origin.x + (senderRect.size.width/2), senderRect.origin.y);
    }else if(_arrowDirection == GTPopoverArrowDirectionRight){
        senderPoint = CGPointMake(senderRect.origin.x, senderRect.origin.y + (senderRect.size.height/2));
        senderPoint.y = senderPoint.y + _screenRect.origin.y;
    }else if(_arrowDirection == GTPopoverArrowDirectionLeft){
        senderPoint = CGPointMake(senderRect.origin.x + senderRect.size.width, senderRect.origin.y + (senderRect.size.height/2));
        senderPoint.y = senderPoint.y + _screenRect.origin.y;
    }

    return senderPoint;
}

- (void)checkArrowPosition:(CGRect)senderRect
{
    float clearSpaceA=0;
    float clearSpaceB=0;
    if(self.arrowPosition == GTPopoverArrowPositionVertical){
        if(!_arrowDirection){
            clearSpaceA = _screenRect.origin.y + senderRect.origin.y;
            clearSpaceB = _screenRect.size.height - (senderRect.origin.y+senderRect.size.height);
            if(clearSpaceA> clearSpaceB){
                if(clearSpaceA < _titleLabelheight+10){
                    self.arrowPosition = GTPopoverArrowPositionHorizontal;
                    [self checkArrowPosition:senderRect];
                }else{
                    _arrowDirection = GTPopoverArrowDirectionBottom;
                }
            }else{
                if(clearSpaceB < _titleLabelheight+10){
                    self.arrowPosition = GTPopoverArrowPositionHorizontal;
                    [self checkArrowPosition:senderRect];
                }else{
                    _arrowDirection = GTPopoverArrowDirectionTop;
                }
            }
        }
        
        
    }else if(self.arrowPosition == GTPopoverArrowPositionHorizontal){
        
        if(!_arrowDirection){
            clearSpaceA = _screenRect.origin.x + senderRect.origin.x;
            clearSpaceB = _screenRect.size.width - (senderRect.origin.x+senderRect.size.width);
            if(clearSpaceA> clearSpaceB){
                if(clearSpaceA < 40){
                    self.arrowPosition = GTPopoverArrowPositionVertical;
                    [self checkArrowPosition:senderRect];
                }else{
                    _arrowDirection = GTPopoverArrowDirectionRight;
                }
            }else{
                if(clearSpaceB < 40){
                    self.arrowPosition = GTPopoverArrowPositionVertical;
                    [self checkArrowPosition:senderRect];
                }else{
                    _arrowDirection = GTPopoverArrowDirectionLeft;
                }
            }
        }
        
    }
}

@end
#endif

//
//  GTVerticalScrollBar.m
//  GTKit
//
//  Created   on 13-4-10.
// http://github.com/litl/WKVerticalScrollBar
//
// Copyright (C) 2012 litl, LLC
// Copyright (C) 2012 WKVerticalScrollBar authors
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.
//
//
#ifndef GT_DEBUG_DISABLE
#import "GTVerticalScrollBar.h"
#import "GTImage.h"

#define CLAMP(x, low, high)  (((x) > (high)) ? (high) : (((x) < (low)) ? (low) : (x)))

@implementation GTUIScrollButton

@synthesize delegate = _delegate;

- (BOOL)beginTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    if (_delegate && [_delegate respondsToSelector:@selector(beginTrackingWithTouch:withEvent:)]) {
        return [_delegate beginTrackingWithTouch:touch withEvent:event];
    }
    return YES;
    
}

- (BOOL)continueTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    if (_delegate && [_delegate respondsToSelector:@selector(continueTrackingWithTouch:withEvent:)]) {
        return [_delegate continueTrackingWithTouch:touch withEvent:event];
    }
    return YES;
}

- (void)endTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    if (_delegate && [_delegate respondsToSelector:@selector(endTrackingWithTouch:withEvent:)]) {
        return [_delegate endTrackingWithTouch:touch withEvent:event];
    }
}


@end

@implementation GTVerticalScrollBar

@synthesize handleWidth = _handleWidth;
@synthesize handleHitWidth = _handleHitWidth;
@synthesize handleSelectedWidth = _handleSelectedWidth;

@synthesize handleMinimumHeight = _handleMinimumHeight;

- (id)initWithFrame:(CGRect)frame
{
    if ((self = [super initWithFrame:frame])) {
        _handleWidth = 5.0f;
        _handleSelectedWidth = 15.0f;
        _handleHitWidth = 44.0f;
        _handleMinimumHeight = 40.0f;
        
        _handleCornerRadius = _handleWidth / 2;
        _handleSelectedCornerRadius = _handleSelectedWidth / 2;
        
        _handleHitArea = CGRectZero;
        
        _normalColor = [[UIColor clearColor] retain];
        _selectedColor = [[UIColor clearColor] retain];
        
        
        _handle = [[CALayer alloc] init];
        [_handle setCornerRadius:_handleCornerRadius];
        [_handle setAnchorPoint:CGPointMake(1.0f, 0.0f)];
        [_handle setFrame:CGRectMake(0, 0, _handleWidth, 0)];
        [_handle setBackgroundColor:[_normalColor CGColor]];
        [[self layer] addSublayer:_handle];
        
        
        _scrollBarView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        [self addSubview:_scrollBarView];
        
        _btnMiddle = [[GTUIScrollButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        [_btnMiddle setImage:[GTImage imageNamed:@"gt_middle" ofType:@"png"] forState:UIControlStateNormal];
        [_btnMiddle setImage:[GTImage imageNamed:@"gt_middle_sel" ofType:@"png"] forState:UIControlStateSelected];
        [_btnMiddle setDelegate:self];
        [_scrollBarView addSubview:_btnMiddle];
        
    }
    return self;
}

- (void)dealloc
{
    M_GT_SAFE_FREE(_handle);
    M_GT_SAFE_FREE(_scrollBarView);
    
    [_scrollView removeObserver:self forKeyPath:@"contentOffset"];
    [_scrollView removeObserver:self forKeyPath:@"contentSize"];
    M_GT_SAFE_FREE(_scrollView);
    
    M_GT_SAFE_FREE(_handleAccessoryView);
    M_GT_SAFE_FREE(_normalColor);
    M_GT_SAFE_FREE(_selectedColor);
    M_GT_SAFE_FREE(_btnMiddle);
    
    [super dealloc];
}

- (UIScrollView *)scrollView
{
    return _scrollView;
}

- (void)setScrollView:(UIScrollView *)scrollView;
{
    if (_scrollView == scrollView) {
        return;
    }
    
    [_scrollView removeObserver:self forKeyPath:@"contentOffset"];
    [_scrollView removeObserver:self forKeyPath:@"contentSize"];
    
    M_GT_SAFE_FREE(_scrollView);
    _scrollView = [scrollView retain];
    
    [_scrollView addObserver:self forKeyPath:@"contentOffset" options:NSKeyValueObservingOptionNew context:nil];
    [_scrollView addObserver:self forKeyPath:@"contentSize" options:NSKeyValueObservingOptionNew context:nil];
    [_scrollView setShowsVerticalScrollIndicator:NO];
    
    [self setNeedsLayout];
}

- (UIView *)handleAccessoryView
{
    return _handleAccessoryView;
}

- (void)setHandleAccessoryView:(UIView *)handleAccessoryView
{
    [_handleAccessoryView removeFromSuperview];
    [_handleAccessoryView release];
    _handleAccessoryView = [handleAccessoryView retain];
    
    [_handleAccessoryView setAlpha:0.0f];
    [self addSubview:_handleAccessoryView];
    [self setNeedsLayout];
}

- (void)setHandleColor:(UIColor *)color forState:(UIControlState)state
{
    if (state == UIControlStateNormal) {
        [_normalColor release];
        _normalColor = [color retain];
    } else if (state == UIControlStateSelected) {
        [_selectedColor release];
        _selectedColor = [color retain];
    }
}

- (CGFloat)handleCornerRadius
{
    return _handleCornerRadius;
}

- (void)setHandleCornerRadius:(CGFloat)handleCornerRadius
{
    _handleCornerRadius = handleCornerRadius;
    
    if (!_handleDragged) {
        [_handle setCornerRadius:_handleCornerRadius];
    }
}

- (CGFloat)handleSelectedCornerRadius
{
    return _handleSelectedCornerRadius;
}

- (void)setHandleSelectedCornerRadius:(CGFloat)handleSelectedCornerRadius
{
    _handleSelectedCornerRadius = handleSelectedCornerRadius;
    
    if (_handleDragged) {
        [_handle setCornerRadius:_handleSelectedCornerRadius];
    }
}

- (void)layoutSubviews
{
    [CATransaction begin];
    [CATransaction setDisableActions:YES];
    
    CGRect bounds = [self bounds];
    CGFloat contentHeight = [_scrollView contentSize].height;
    CGFloat frameHeight = [_scrollView frame].size.height;
    
    // Calculate the current scroll value (0, 1) inclusive.
    // Note that contentOffset.y only goes from (0, contentHeight - frameHeight)
    if (contentHeight == frameHeight) {
        return;
    }
    CGFloat scrollValue = [_scrollView contentOffset].y / (contentHeight - frameHeight);
    
    if (contentHeight == 0) {
        return;
    }
    
    //滚动条固定高度
    CGFloat handleHeight = 44.0f;
    
    [_handle setOpacity:(handleHeight == bounds.size.height) ? 0.0f : 1.0f];
    
    // Not only move the handle, but also shift where the position maps on to the handle,
    // so that the handle doesn't go off screen when the scrollValue approaches 1.
    CGFloat handleY = CLAMP((scrollValue * bounds.size.height) - (scrollValue * handleHeight),
                            0, bounds.size.height - handleHeight);
    
    CGFloat previousWidth = [_handle bounds].size.width ?: _handleWidth;
    [_handle setPosition:CGPointMake(bounds.size.width, handleY)];
    [_handle setBounds:CGRectMake(0, 0, previousWidth, handleHeight)];
    
    [_scrollBarView setFrame:CGRectMake(bounds.size.width - 44, handleY, 44, 44)];
    
    // Center the accessory view to the left of the handle
    CGRect accessoryFrame = [_handleAccessoryView frame];
    [_handleAccessoryView setCenter:CGPointMake(bounds.size.width - _handleHitWidth - (accessoryFrame.size.width / 2),
                                                handleY + (handleHeight / 2))];
    
    _handleHitArea = CGRectMake(bounds.size.width - _handleHitWidth, handleY,
                               _handleHitWidth, handleHeight);
    
    [CATransaction commit];
}

- (BOOL)handleVisible
{
    return [_handle opacity] == 1.0f;
}

- (void)growHandle
{
    if (![self handleVisible]) {
        return;
    }
    
    [CATransaction begin];
    [CATransaction setAnimationDuration:0.3f];
    
    [_handle setCornerRadius:_handleSelectedCornerRadius];
    [_handle setBounds:CGRectMake(0, 0, _handleSelectedWidth, [_handle bounds].size.height)];
    [_handle setBackgroundColor:[_selectedColor CGColor]];
    
    [CATransaction commit];
    
    [UIView animateWithDuration:0.3f animations:^{
        [_handleAccessoryView setAlpha:1.0f];
    }];
}

- (void)shrinkHandle
{
    if (![self handleVisible]) {
        return;
    }
    
    [CATransaction begin];
    [CATransaction setAnimationDuration:0.3f];
    
    [_handle setCornerRadius:_handleCornerRadius];
    [_handle setBounds:CGRectMake(0, 0, _handleWidth, [_handle bounds].size.height)];
    [_handle setBackgroundColor:[_normalColor CGColor]];
    
    [CATransaction commit];
    
    [UIView animateWithDuration:0.3f animations:^{
        [_handleAccessoryView setAlpha:0.0f];
    }];
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event
{
    return CGRectContainsPoint(_handleHitArea, point);
}

- (BOOL)beginTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    [self unobserveTick];
    [self setHandleHidden:NO];
    if (![self handleVisible]) {
        return NO;
    }
    
    _lastTouchPoint = [touch locationInView:self];
    
    // When the user initiates a drag, make the handle grow so it's easier to see
    _handleDragged = YES;
    [self growHandle];
    
    [self setNeedsLayout];
    
    return YES;
}

- (BOOL)continueTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    CGPoint point = [touch locationInView:self];
    
    CGSize contentSize = [_scrollView contentSize];
    CGPoint contentOffset = [_scrollView contentOffset];
    CGFloat frameHeight = [_scrollView frame].size.height;
    CGFloat deltaY = ((point.y - _lastTouchPoint.y) / [self bounds].size.height)
    * [_scrollView contentSize].height;
    
    [_scrollView setContentOffset:CGPointMake(contentOffset.x,  CLAMP(contentOffset.y + deltaY,
                                                                      0, contentSize.height - frameHeight))
                         animated:NO];
    _lastTouchPoint = point;
    
    return YES;
}

- (void)endTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    _lastTouchPoint = CGPointZero;
    
    // When user drag is finished, return handle to previous size
    _handleDragged = NO;
    [self shrinkHandle];
    [self observeTick];
}

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context
{
    if (object != _scrollView) {
        return;
    }
    
    [self setNeedsLayout];
}

- (void)setHandleHidden:(BOOL)hidden
{
    if (hidden == YES) {
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
        [UIView setAnimationDelay:0.3f];
        [UIView setAnimationDelegate:self];
        
        [_btnMiddle setFrame:CGRectMake(44, 0, 0, 44)];
        
        [UIView commitAnimations];
    } else {
        [_btnMiddle setFrame:CGRectMake(0, 0, 44, 44)];
    }
    
    
}

#pragma mark - hide scroll bar

- (void)observeTick
{
    if (_timer == nil) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:3.0f
                                                  target:self
                                                selector:@selector(handleTick)
                                                userInfo:nil
                                                 repeats:NO];
        [_timer retain];
    }
	
}

- (void)unobserveTick
{
    if (_timer) {
        [_timer invalidate];
        [_timer release];
        _timer = nil;
    }
}


- (void)handleTick
{
    [self setHandleHidden:YES];
    [self unobserveTick];
}


/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect
 {
 // Drawing code
 }
 */

@end

#endif

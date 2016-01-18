//
//  GTPopoverTouchView.m
//
//  Created  Saito Takashi on 5/9/12.
//  Copyright (c) 2012 synetics ltd. All rights reserved.
//
// https://github.com/takashisite/TSPopover
//
#ifndef GT_DEBUG_DISABLE
#import "GTPopoverTouchView.h"

@implementation GTPopoverTouchView

@synthesize delegate = _delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.delegate view:self touchesBegan:touches withEvent:event];
}

@end
#endif

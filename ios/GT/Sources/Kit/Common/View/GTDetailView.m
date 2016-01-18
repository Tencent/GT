//
//  GTDetailView.m
//  GTKit
//
//  Created   on 12-12-13.
//
//   ______    ______    ______
//  /\  __ \  /\  ___\  /\  ___\
//  \ \  __<  \ \  __\_ \ \  __\_
//   \ \_____\ \ \_____\ \ \_____\
//    \/_____/  \/_____/  \/_____/
//
//
//  Copyright (c) 2014-2015, Geek Zoo Studio
//  http://www.bee-framework.com
//
//
//  Permission is hereby granted, free of charge, to any person obtaining a
//  copy of this software and associated documentation files (the "Software"),
//  to deal in the Software without restriction, including without limitation
//  the rights to use, copy, modify, merge, publish, distribute, sublicense,
//  and/or sell copies of the Software, and to permit persons to whom the
//  Software is furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//  IN THE SOFTWARE.
//
//

#ifndef GT_DEBUG_DISABLE

#import "GTDetailView.h"
#import "GTUtility.h"
#import <QuartzCore/QuartzCore.h>

@implementation GTCloseButton

+ (UIImage *)closeButtonImage:(CGSize)size{
    UIGraphicsBeginImageContextWithOptions(size, NO, 0);
    
    // General Declarations
    CGContextRef context = UIGraphicsGetCurrentContext();
    if (!context) {
        return nil;
    }
    
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    // Color Declarations
    UIColor *topGradient = [UIColor colorWithRed:0.21 green:0.21 blue:0.21 alpha:0.9];
    UIColor *bottomGradient = [UIColor colorWithRed:0.03 green:0.03 blue:0.03 alpha:0.9];
    
    // Gradient Declarations
    NSArray *gradientColors = @[(id)topGradient.CGColor,
                                (id)bottomGradient.CGColor];
    CGFloat gradientLocations[] = {0, 1};
    CGGradientRef gradient = CGGradientCreateWithColors(colorSpace, ( CFArrayRef)gradientColors, gradientLocations);

    // Shadow Declarations
    CGColorRef shadow = [UIColor blackColor].CGColor;
    CGSize shadowOffset = CGSizeMake(0, 1);
    CGFloat shadowBlurRadius = 3;
    CGColorRef shadow2 = [UIColor blackColor].CGColor;
    CGSize shadow2Offset = CGSizeMake(0, 1);
    CGFloat shadow2BlurRadius = 0;
    
    
    // Oval Drawing
    UIBezierPath *ovalPath = [UIBezierPath bezierPathWithOvalInRect:CGRectMake(4, 3, 24, 24)];
    CGContextSaveGState(context);
    [ovalPath addClip];
    CGContextDrawLinearGradient(context, gradient, CGPointMake(16, 3), CGPointMake(16, 27), 0);
    CGContextRestoreGState(context);
    
    CGContextSaveGState(context);
    CGContextSetShadowWithColor(context, shadowOffset, shadowBlurRadius, shadow);
    [[UIColor whiteColor] setStroke];
    ovalPath.lineWidth = 2;
    [ovalPath stroke];
    CGContextRestoreGState(context);
    
    
    // Bezier Drawing
    UIBezierPath *bezierPath = [UIBezierPath bezierPath];
    [bezierPath moveToPoint:CGPointMake(22.36, 11.46)];
    [bezierPath addLineToPoint:CGPointMake(18.83, 15)];
    [bezierPath addLineToPoint:CGPointMake(22.36, 18.54)];
    [bezierPath addLineToPoint:CGPointMake(19.54, 21.36)];
    [bezierPath addLineToPoint:CGPointMake(16, 17.83)];
    [bezierPath addLineToPoint:CGPointMake(12.46, 21.36)];
    [bezierPath addLineToPoint:CGPointMake(9.64, 18.54)];
    [bezierPath addLineToPoint:CGPointMake(13.17, 15)];
    [bezierPath addLineToPoint:CGPointMake(9.64, 11.46)];
    [bezierPath addLineToPoint:CGPointMake(12.46, 8.64)];
    [bezierPath addLineToPoint:CGPointMake(16, 12.17)];
    [bezierPath addLineToPoint:CGPointMake(19.54, 8.64)];
    [bezierPath addLineToPoint:CGPointMake(22.36, 11.46)];
    [bezierPath closePath];
    CGContextSaveGState(context);
    CGContextSetShadowWithColor(context, shadow2Offset, shadow2BlurRadius, shadow2);
    [[UIColor whiteColor] setFill];
    [bezierPath fill];
    CGContextRestoreGState(context);
    
    
    // Cleanup
    CGGradientRelease(gradient);
    CGColorSpaceRelease(colorSpace);
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}

@end


@implementation GTTextView

@synthesize delegate = _delegate;

- (id)initWithFrame:(CGRect)frame
{
	self = [super initWithFrame:frame];
	if ( self )
	{
		self.backgroundColor = [UIColor clearColor];
		
		CGRect contentFrame = frame;
		contentFrame.origin = CGPointZero;
		contentFrame = CGRectInset(contentFrame, 10.0f, 10.0f);
		
		_content = [[UITextView alloc] initWithFrame:contentFrame];
		_content.font = [UIFont systemFontOfSize:12.0f];
		_content.textColor = [UIColor blackColor];
		_content.textAlignment = NSTextAlignmentLeft;
		_content.editable = NO;
		_content.dataDetectorTypes = UIDataDetectorTypeLink;
		_content.scrollEnabled = YES;
		_content.backgroundColor = [UIColor whiteColor];
		_content.layer.borderColor = [UIColor grayColor].CGColor;
		_content.layer.borderWidth = 2.0f;
		[self addSubview:_content];
        
		CGRect closeFrame;
		closeFrame.size.width = 40.0f;
		closeFrame.size.height = 40.0f;
		closeFrame.origin.x = frame.size.width - closeFrame.size.width + 5.0f;
		closeFrame.origin.y = -0.0f;
		
		_close = [[UIButton alloc] initWithFrame:closeFrame];
        [_close setImage:[GTCloseButton closeButtonImage:closeFrame.size] forState:UIControlStateNormal];
        [_close addTarget:self action:@selector(onClose:) forControlEvents:UIControlEventTouchUpInside];
		[self addSubview:_close];
	}
	return self;
}

- (void)setFilePath:(NSString *)path
{
	if ( [path hasSuffix:@".plist"] || [path hasSuffix:@".strings"] )
	{
		_content.text = [[NSDictionary dictionaryWithContentsOfFile:path] description];
	}
	else
	{
		NSData * data = [NSData dataWithContentsOfFile:path];
        _content.text = [[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] autorelease];
	}
}

- (void)setContentText:(NSString *)text
{
    _content.text = text;
}

- (void)dealloc
{
	M_GT_SAFE_RELEASE_SUBVIEW( _content );
	M_GT_SAFE_RELEASE_SUBVIEW( _close );
	
	[super dealloc];
}

- (void)onClose:(id)sender
{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.6f];
    [UIView setAnimationDelegate:self];
    [UIView setAnimationDidStopSelector:@selector(didDisappearingAnimationDone)];

    [UIView commitAnimations];
    
    if (_delegate && [_delegate respondsToSelector:@selector(onClose)]) {
        [_delegate onClose];
    }
}

- (void)didDisappearingAnimationDone
{
	[self removeFromSuperview];
}
@end

#pragma mark -

@implementation GTImageView

- (id)initWithFrame:(CGRect)frame
{
	self = [super initWithFrame:frame];
	if ( self )
	{
		self.backgroundColor = [UIColor clearColor];
		
		CGRect bounds = frame;
		bounds.origin = CGPointZero;
		bounds = CGRectInset( bounds, 10.0f, 10.0f );
        
		_imageView = [[UIImageView alloc] initWithFrame:bounds];
		_imageView.contentMode = UIViewContentModeCenter;
		
		
		_zoomView = [[UIView alloc] initWithFrame:bounds];
        [_zoomView addSubview:_imageView];
		_zoomView.backgroundColor = [UIColor whiteColor];
		_zoomView.layer.borderColor = [UIColor grayColor].CGColor;
		_zoomView.layer.borderWidth = 2.0f;
        
		[self addSubview:_zoomView];
        
		CGRect closeFrame;
		closeFrame.size.width = 40.0f;
		closeFrame.size.height = 40.0f;
		closeFrame.origin.x = frame.size.width - closeFrame.size.width + 5.0f;
		closeFrame.origin.y = -0.0f;
		_close = [[UIButton alloc] initWithFrame:closeFrame];
        [_close setImage:[GTCloseButton closeButtonImage:closeFrame.size] forState:UIControlStateNormal];
        [_close addTarget:self action:@selector(onClose:) forControlEvents:UIControlEventTouchUpInside];
		[self addSubview:_close];
	}
	return self;
}

- (void)setFilePath:(NSString *)path
{
    UIImage *img = [UIImage imageWithContentsOfFile:path];
    CGSize size = _imageView.bounds.size;
    size.height -= 30.0f;
    size.width -= 30.0f;
    _imageView.image = [GTUtility image:img scaleAspectFitSize:size];
}

- (void)setURL:(NSString *)url
{
}

- (void)dealloc
{
	M_GT_SAFE_RELEASE_SUBVIEW( _zoomView );
	M_GT_SAFE_RELEASE_SUBVIEW( _imageView );
	M_GT_SAFE_RELEASE_SUBVIEW( _close );
	
	[super dealloc];
}

- (void)onClose:(id)sender
{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.6f];
    [UIView setAnimationDelegate:self];
    [UIView setAnimationDidStopSelector:@selector(didDisappearingAnimationDone)];
    
    [UIView commitAnimations];
}

- (void)didDisappearingAnimationDone
{
	[self removeFromSuperview];
}

@end

#endif


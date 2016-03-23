//
//  GTUIAlertView.m
//  GTKit
//
//  Created   on 13-3-29.
// Tencent is pleased to support the open source community by making
// Tencent GT (Version 2.4 and subsequent versions) available.
//
// Notwithstanding anything to the contrary herein, any previous version
// of Tencent GT shall not be subject to the license hereunder.
// All right, title, and interest, including all intellectual property rights,
// in and to the previous version of Tencent GT (including any and all copies thereof)
// shall be owned and retained by Tencent and subject to the license under the
// Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
//
// Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the MIT License (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of the License at
//
// http://opensource.org/licenses/MIT
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.
//
//
#ifndef GT_DEBUG_DISABLE

#import "GTUIAlertView.h"
#import "GTDebugDef.h"
#import <QuartzCore/QuartzCore.h>
#import "GTUtility.h"


@implementation GTFullScreenOverlayView

@synthesize appKeyWindow = _appKeyWindow;

- (id)init
{
	return [self initWithImage:nil];
}

- (id)initWithFrame:(CGRect)frame
{
	return [self initWithImage:nil];
}

- (id)initWithImage:(UIImage*)img
{
    CGRect frame = [UIScreen mainScreen].fullScreenBounds;
    
//    if(self = [super initWithFrame:[UIScreen mainScreen].bounds]){
    if(self = [super initWithFrame:frame]){  // navy modified
		_shown = FALSE;
		self.appKeyWindow = [[UIApplication sharedApplication] keyWindow];

//		_imageView = [[UIImageView alloc] initWithFrame:[UIScreen mainScreen].bounds];
        _imageView = [[UIImageView alloc] initWithFrame:frame]; // navy modified
        
        _imageView.backgroundColor = [UIColor blackColor];
        _imageView.alpha = 0.5;
        [self makeKeyAndVisible];
		[self addSubview:_imageView];
        self.backgroundColor = [UIColor clearColor];
		self.windowLevel = UIWindowLevelStatusBar + 300;
		self.hidden = YES;
	}
	return self;
}

- (void)show
{
	// it should only be shown once and only once
	if(!_shown) {
		[self retain];
		self.hidden = NO;
		_shown = TRUE;
		[self makeKeyAndVisible];
	}
}

-(void)dismiss
{
	if(!self.hidden) {
		self.hidden = YES;
        [self release];
	}
}

- (void)dealloc
{
    [self.appKeyWindow makeKeyAndVisible];
    self.appKeyWindow = nil;
	[_imageView release];
	[super dealloc];
}

- (void)setFullscreenImage:(UIImage*)img
{
	_imageView.image = img;
}
@end

@implementation GTUIAlertView

@synthesize delegate = __delegate;
@synthesize title = _title;
@synthesize	message = _message;
@synthesize buttonArray = _buttonArray;
@synthesize maxMiddenHeight = _maxMiddenHeight ;
@synthesize object = _object ;
@synthesize state = _state;
@synthesize titleLabel = _titleLabel;
@synthesize msgLabel = _msgLabel;
@synthesize otherButtonTitle = _otherButtonTitle;

- (void)dealloc {
	[_title release];
	[_message release];
	[_backgroundView release];
	[_titleLabel release];
	[_bgView release];
	[_container release];
	[_msgLabel release];
	[_buttonArray release];
    [_textFieldArray release];
	[_object release];
	__delegate = nil;
    [super dealloc];
}

- (id)initWithTitle:(NSString *)title message:(NSString *)message delegate:(id /*<GTAlertViewDelegate>*/)delegate cancelButtonTitle:(NSString *)cancelButtonTitle otherButtonTitles:(NSString *)otherButtonTitles
{
	if (self = [super init]) {
		__delegate = delegate;
		
		_state = 0;
		_maxMiddenHeight = 0.0f;
		
		UIFont* titleFont = [UIFont systemFontOfSize:16];
		UIFont* msgFont = [UIFont systemFontOfSize:14];
        
		self.title = title;
		self.message = message;
		
        _backgroundView = [[UIImageView alloc]initWithFrame:CGRectZero];
		[self addSubview:_backgroundView];
		
		_container = [[UIView alloc] initWithFrame:CGRectZero];
        _container.backgroundColor   = M_GT_COLOR_WITH_HEX(0x29292D);
        _container.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
        _container.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
		[self addSubview:_container];
        
		_titleLabel = [[UILabel alloc]initWithFrame:CGRectZero];
		_titleLabel.textAlignment = NSTextAlignmentCenter;
		_titleLabel.backgroundColor = M_GT_COLOR_WITH_HEX(0x47474E);
		_titleLabel.text = title;
		_titleLabel.font = titleFont;
		_titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.alpha = 1.0f;
		_titleLabel.tag = 'x'+'y';
		
		[_container addSubview:_titleLabel];
		
		_msgLabel   = [[UILabel alloc]initWithFrame:CGRectZero];
		_msgLabel.textAlignment = NSTextAlignmentCenter;
		_msgLabel.backgroundColor = [UIColor clearColor];
		_msgLabel.text = message;
		_msgLabel.font = msgFont;
		_msgLabel.textColor = M_GT_LABEL_COLOR;
        _msgLabel.numberOfLines = 0;
        _msgLabel.alpha = 1.0f;
		
		[_container addSubview:_msgLabel];
		
        
		_buttonArray = [[NSMutableArray alloc] init];
        if (cancelButtonTitle) {
            [self addButtonWithTitle:cancelButtonTitle];
        }
        
        if (otherButtonTitles) {
            [self setOtherButtonTitle:otherButtonTitles];
            [self addButtonWithTitle:otherButtonTitles];
        }
        
        _textFieldArray = [[NSMutableArray alloc] init];
	}
	
	return self;
}

- (void)setTextAlignment:(NSTextAlignment)textAlignment
{
    _msgLabel.textAlignment = textAlignment;
}
- (void)selectBtnAtIndex:(NSInteger)index
{
	for (int i = 0; i < [_buttonArray count]; i++)
	{
		UIButton *b = [_buttonArray objectAtIndex:i];
		b.selected = NO;
		b.userInteractionEnabled = YES;
        //按钮正常态颜色#35353b  边框#1c1c21
        [b setBackgroundColor:[UIColor colorWithRed:0.208 green:0.208 blue:0.231 alpha:1.000]];
        b.layer.borderColor = [UIColor colorWithRed:0.110 green:0.110 blue:0.129 alpha:1.000].CGColor;
        b.layer.borderWidth = 1.0f;
	}
    
    if (index >= [_buttonArray count]) {
        return;
    }
	UIButton *btn = [_buttonArray objectAtIndex:index];
	btn.selected = YES;
	btn.userInteractionEnabled = NO;
    //按钮选中颜色#3c4a76  边框2px #22293f
    [btn setBackgroundColor:M_GT_SELECTED_COLOR];
    btn.layer.borderColor = [UIColor colorWithRed:0.133 green:0.161 blue:0.247 alpha:1.000].CGColor;
    btn.layer.borderWidth = 1.0f;
}


- (void)addButtonWithTitle:(NSString *)title
{
    UIButton* btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setTitle:title forState:UIControlStateNormal];
    btn.titleLabel.font=[UIFont systemFontOfSize:16];
    [btn setTitleColor:M_GT_LABEL_COLOR forState:UIControlStateNormal];
    [btn setTitleColor:M_GT_LABEL_COLOR forState:UIControlStateHighlighted];
    btn.backgroundColor = M_GT_COLOR_WITH_HEX(0x35353B);
    btn.layer.borderColor = M_GT_BTN_BORDER_COLOR.CGColor;
    btn.layer.borderWidth = M_GT_BTN_BORDER_WIDTH;
    [btn setTag:[_buttonArray count]];
    [btn addTarget:self action:@selector(alertViewButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    
    [_buttonArray addObject:btn];
    
    [_container addSubview:btn];
}

- (void)addTextFieldWithTag:(NSUInteger)tag
{
    UITextField* textField = [[UITextField alloc] init];
    [textField setBackgroundColor:[UIColor blackColor]];
    textField.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
    textField.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
    textField.textColor = [UIColor whiteColor];
    [textField setBorderStyle:UITextBorderStyleNone];
    textField.font = [UIFont systemFontOfSize:15];
    textField.clearButtonMode = UITextFieldViewModeAlways;
    textField.textAlignment = NSTextAlignmentLeft;
    textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    textField.delegate = self;
    textField.autocorrectionType = UITextAutocorrectionTypeNo;
    textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    textField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    textField.returnKeyType = UIReturnKeyDone;
    
    UIView * view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 8, 10)];
    textField.leftView = view;
    textField.leftViewMode = UITextFieldViewModeAlways;
    [view release];
    
    [textField setTag:tag];
    [_textFieldArray addObject:textField];
    
    [_container addSubview:textField];
    [textField release];
    
    return;
}

- (UITextField *)textFieldAtIndex:(NSInteger)textFieldIndex
{
    if (textFieldIndex < _textFieldArray.count) {
        UITextField* textField = [_textFieldArray objectAtIndex:textFieldIndex];
        return textField;
    }
    
    return nil;
}

- (UIButton *)btnAtTitle:(NSString *)title
{
    for (int i = 0; i < _buttonArray.count; ++i) {
		UIButton* btn = [_buttonArray objectAtIndex:i];
        if ([[[btn titleLabel] text] isEqualToString:title]) {
            return btn;
        }
	}
    
    return nil;
}

- (UITextField *)textFieldAtTag:(NSInteger)tag
{
    for (int i = 0; i < _textFieldArray.count; ++i) {
		UITextField* textField = [_textFieldArray objectAtIndex:i];
        if ([textField tag] == tag) {
            return textField;
        }
	}
    
    return nil;
}


- (void)closeKeyBoard
{
    for (int i = 0; i < _textFieldArray.count; ++i) {
		UITextField* textField = [_textFieldArray objectAtIndex:i];
        [textField resignFirstResponder];
	}
}
- (void)layoutSubviews
{
//    [super layoutSubviews];
	
	NSString* title = _title;
	NSString* message = _message;
	
	UIFont* titleFont = _titleLabel.font;
	UIFont* msgFont = _msgLabel.font;
	
	int width = 280;
	int maxWidth = M_GT_SCREEN_WIDTH;
	int offset_y = 160;
	int currentHeight = 180;
    
	CGRect backgroundRect = CGRectMake((maxWidth-width)/2, offset_y , width , currentHeight);
	CGRect buttonRect = CGRectZero;
	CGRect textFieldRect = CGRectZero;
	//
	// calculate rectangles
	//
    
	int marginLeft = 5;
	int hspace = 12;
	int vspace1 = (message == nil ? 8 : 10);
    int vspace2 = 15;
	int vspace3 = 10;
    
	int textFieldHeight = 36;
	int buttonHeight = 32;
    
	CGSize maxSize = CGSizeMake(width - marginLeft * 2, 280); // max size
	CGSize sz = [title sizeWithFont:titleFont constrainedToSize:maxSize lineBreakMode:NSLineBreakByWordWrapping];
    CGRect titleRect;
    if (sz.height > 36) {
        titleRect = CGRectMake(0, 0, width, sz.height);
    } else {
        titleRect = CGRectMake(0, 0, width, 36);
    }
    
	
	sz = [message sizeWithFont:msgFont constrainedToSize:maxSize lineBreakMode:NSLineBreakByWordWrapping];
	sz.height = (message == nil ? 0 : sz.height);
	CGRect msgRect = CGRectMake(marginLeft, titleRect.origin.y + titleRect.size.height + vspace1, maxSize.width, sz.height);
	
    int y = msgRect.origin.y + msgRect.size.height + vspace1 + _maxMiddenHeight ;
    y += [self heightForAddtion:marginLeft offsetY:y size:maxSize] + vspace1 + _maxMiddenHeight ;
    
    NSMutableArray* textFiledRectArray = [[NSMutableArray alloc] init];
    for(int i = 0; i < _textFieldArray.count; ++i) {
        textFieldRect = CGRectMake(marginLeft, y, maxSize.width, textFieldHeight);
        y += (textFieldHeight + vspace2);
        
        [textFiledRectArray addObject:[NSValue valueWithCGRect:textFieldRect]];
    }
    
    NSMutableArray* buttonRectArray = [[NSMutableArray alloc] init];
    int btnMarginLeft = 18;
    CGSize btnMaxSize = CGSizeMake(width - btnMarginLeft * 2, 260);
	if (_buttonArray) {
		switch (_buttonArray.count) {
			case 0:
				break;
			case 1:
				buttonRect = CGRectMake(btnMarginLeft, y, btnMaxSize.width, buttonHeight);
				[buttonRectArray addObject:[NSValue valueWithCGRect:buttonRect]];
				y += (buttonHeight + vspace3);
				break;
			case 2:
				// tile horizontally
				buttonRect = CGRectMake(btnMarginLeft, y, (btnMaxSize.width - hspace)/2, buttonHeight);
				[buttonRectArray addObject:[NSValue valueWithCGRect:buttonRect]];
				
				buttonRect.origin.x += (buttonRect.size.width + hspace);
				[buttonRectArray addObject:[NSValue valueWithCGRect:buttonRect]];
				
				y += (buttonHeight + vspace3);
				break;
			default:
				// tile vertically for more than 2 buttons
				for(int i = 0; i < _buttonArray.count; ++i) {
					buttonRect = CGRectMake(btnMarginLeft, y, btnMaxSize.width, buttonHeight);
					y += (buttonHeight + vspace3);
					
					[buttonRectArray addObject:[NSValue valueWithCGRect:buttonRect]];
				}
				break;
		}
	}
	
	//
	// update frames
	//
    
	backgroundRect.size.height = 5 + y;
    backgroundRect.origin.y = (480 - backgroundRect.size.height) / 2;
    if ([_textFieldArray count] > 0) {
        backgroundRect.origin.y = 30;
        UITextField * t = [_textFieldArray objectAtIndex:0];
        [t becomeFirstResponder];
    }
    
	_backgroundView.frame = backgroundRect;
	_container.frame = backgroundRect;
	_titleLabel.frame = titleRect;
	_msgLabel.frame = msgRect;
    for (int i = 0; i < _textFieldArray.count; ++i) {
		UITextField* textField = [_textFieldArray objectAtIndex:i];
        textField.frame = [[textFiledRectArray objectAtIndex:i] CGRectValue];
	}
    
    
	for (int i = 0; i < _buttonArray.count; ++i) {
		UIButton* btn = [_buttonArray objectAtIndex:i];
		btn.frame = [[buttonRectArray objectAtIndex:i] CGRectValue];
	}
	
	//
	// clean up
	//
	[textFiledRectArray release];
	[buttonRectArray release];
}

//补充高度，用于扩展视图
- (CGFloat)heightForAddtion:(CGFloat)marginLeft offsetY:(CGFloat)y size:(CGSize)maxSize
{
    return 0;
}

-(void)setTitle:(NSString*) title message:(NSString*)message
{
	self.title = title;
	self.message = message;
}


#pragma mark  - alertViewButtonClicked
-(void)alertViewButtonClicked:(id)sender
{
	
	if([__delegate respondsToSelector:@selector(alertView:clickedButtonAtIndex:)])
	{
		int i;
        UIButton* b;
		for (i = 0; i < _buttonArray.count; ++i){
			b = [_buttonArray objectAtIndex:i];
			if(b == sender){
				break;
			}
		}
		
        [self selectBtnAtIndex:i];
        [__delegate alertView:self clickedButtonAtIndex:i];
	}
	
    [self closeKeyBoard];
	[self dismiss];
	
}

#pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *newString = [textField.text stringByReplacingCharactersInRange:range withString:string];
    UIButton *btn = [self btnAtTitle:_otherButtonTitle];
    if ([newString length] == 0) {
        btn.userInteractionEnabled = NO;
        [btn setTitleColor:M_GT_BTN_BORDER_COLOR forState:UIControlStateNormal];
        [btn setTitleColor:M_GT_BTN_BORDER_COLOR forState:UIControlStateHighlighted];
        
    } else {
        btn.userInteractionEnabled = YES;
        [btn setTitleColor:M_GT_LABEL_COLOR forState:UIControlStateNormal];
        [btn setTitleColor:M_GT_LABEL_COLOR forState:UIControlStateHighlighted];
        
    }
    return YES;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField
{
    UIButton *btn = [self btnAtTitle:_otherButtonTitle];
    btn.userInteractionEnabled = NO;
    [btn setTitleColor:M_GT_BTN_BORDER_COLOR forState:UIControlStateNormal];
    [btn setTitleColor:M_GT_BTN_BORDER_COLOR forState:UIControlStateHighlighted];
    
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

@end

@implementation GTWebUILabel

- (void)drawRect:(CGRect)rect
{
    CGSize size;
    CGFloat width = 0;
    CGFloat viewWidth = self.bounds.size.width;
    CGFloat viewHeight = self.bounds.size.height;
    
    width = 0;
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_COLOR.CGColor);
    size = [@"Suggest to get it from" drawInRect:CGRectMake( 0.0f, 0.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_RED_COLOR.CGColor);
    width += size.width;
    NSMutableString *str = [NSMutableString stringWithCapacity:1];
    //一个空格占用四个width
    for (int i = 0; i < width/4 + 1; i++) {
        [str appendString:@" "];
    }
    [str appendString:@"gt.tencent.com"];
    size = [str drawInRect:CGRectMake( 0.0f, 0.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    
    CGContextSetFillColorWithColor(UIGraphicsGetCurrentContext(), M_GT_LABEL_COLOR.CGColor);
    width += size.width;
    str = [NSMutableString stringWithCapacity:1];
    
    
    //ios7上显示有异常，暂时不显示
    if ([[GTUtility sharedInstance] systemVersion] < 7)
    {
        //一个空格占用四个width
        for (int i = 0; i < width/4 + 1; i++) {
            [str appendString:@" "];
        }
        [str appendString:@"ASAP."];
        [str drawInRect:CGRectMake( 0.0f, 0.0f, viewWidth, viewHeight ) withFont:[UIFont systemFontOfSize:14.0]];
    }
    
    
    
}

@end

@implementation GTMsgAlertView
@synthesize webMsg = _webMsg;

- (void)dealloc {
    self.webMsg = nil;
    [_webMsgLabel release];
    [super dealloc];
}

- (id)initWithTitle:(NSString *)title message:(NSString *)message delegate:(id /*<GTAlertViewDelegate>*/)delegate cancelButtonTitle:(NSString *)cancelButtonTitle otherButtonTitles:(NSString *)otherButtonTitles
{
    self = [super initWithTitle:title message:message delegate:delegate cancelButtonTitle:cancelButtonTitle otherButtonTitles:otherButtonTitles];
	if (self) {
        self.webMsg = @"Suggest to get it from gt.tencent.com ASAP.";

        _webMsgLabel   = [[GTWebUILabel alloc] initWithFrame:CGRectZero];
		_webMsgLabel.textAlignment = NSTextAlignmentCenter;
		_webMsgLabel.backgroundColor = [UIColor clearColor];
		_webMsgLabel.text = self.webMsg;
		_webMsgLabel.font = _msgLabel.font;
		_webMsgLabel.textColor = M_GT_LABEL_RED_COLOR;
        _webMsgLabel.numberOfLines = 0;
        _webMsgLabel.alpha = 1.0f;
		
		[_container addSubview:_webMsgLabel];
        
        _msgLabel.textAlignment = NSTextAlignmentLeft;
	}
	
	return self;
}

//补充高度，用于扩展视图
- (CGFloat)heightForAddtion:(CGFloat)marginLeft offsetY:(CGFloat)y size:(CGSize)maxSize
{
    CGSize sz = [self.webMsg sizeWithFont:self.msgLabel.font constrainedToSize:maxSize lineBreakMode:NSLineBreakByWordWrapping];
	sz.height = (self.webMsg == nil ? 0 : sz.height);
	CGRect webMsgRect = CGRectMake(marginLeft, y , maxSize.width, sz.height);
    _webMsgLabel.frame = webMsgRect;
    return sz.height;
}

@end

@implementation GTProvisionsAlertView

@synthesize provisions = _provisions;
@synthesize tipsSelected = _tipsSelected;

- (void)dealloc {
    self.provisions = nil;
    [_provisionsView release];
    [_webView release];
    
    [_tipsBtn release];
    [_tipsLabel release];
    [super dealloc];
}

- (id)initWithTitle:(NSString *)title message:(NSString *)message delegate:(id /*<GTAlertViewDelegate>*/)delegate cancelButtonTitle:(NSString *)cancelButtonTitle otherButtonTitles:(NSString *)otherButtonTitles
{
    self = [super initWithTitle:title message:message delegate:delegate cancelButtonTitle:cancelButtonTitle otherButtonTitles:otherButtonTitles];
	if (self) {
        self.provisions = nil;
        
        _provisionsView   = [[UITextView alloc] initWithFrame:CGRectZero];
		_provisionsView.textAlignment = NSTextAlignmentLeft;
		_provisionsView.backgroundColor = [UIColor whiteColor];
		_provisionsView.text = self.provisions;
		_provisionsView.font = _msgLabel.font;
		_provisionsView.textColor = [UIColor blackColor];
        _provisionsView.alpha = 1.0f;
		_provisionsView.editable = NO;
        
		[_container addSubview:_provisionsView];
        
        _webView = [[UIWebView alloc] initWithFrame:CGRectZero];
        
        _webView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
//        [_webView loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:[[NSBundle frameworkBundle] pathForResource:@"EULA" ofType:@"html"]]]];
        [_webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"http://gt.qq.com/wp-content/EULA_EN.html"]]];
        
        _webView.delegate = self;
        [_provisionsView addSubview:_webView];

        _tipsLabel = [[UILabel alloc] initWithFrame:CGRectZero];
		_tipsLabel.textAlignment = NSTextAlignmentLeft;
		_tipsLabel.backgroundColor = [UIColor clearColor];
		_tipsLabel.text = @"I have read and accept all the terms of this Agreement";
        _tipsLabel.font = [UIFont boldSystemFontOfSize:13.0];
		_tipsLabel.textColor = M_GT_LABEL_COLOR;
        _tipsLabel.numberOfLines = 0;
        _tipsLabel.alpha = 1.0f;
        
        [_container addSubview:_tipsLabel];
        
        _tipsSelected = YES;
        
        _tipsBtn = [[UIButton alloc] initWithFrame:CGRectZero];
        [_tipsBtn addTarget:self action:@selector(selButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_tipsBtn setImageEdgeInsets:UIEdgeInsetsMake(5, 5, 5, 5)];
        [self updateTipsButton];
        
        [_container addSubview:_tipsBtn];
	}
	
	return self;
}

//补充高度，用于扩展视图
- (CGFloat)heightForAddtion:(CGFloat)marginLeft offsetY:(CGFloat)y size:(CGSize)maxSize
{
//    CGSize sz = [self.provisions sizeWithFont:self.msgLabel.font constrainedToSize:maxSize lineBreakMode:NSLineBreakByWordWrapping];
//	CGFloat height = (self.provisions == nil ? 0 : sz.height);
    CGFloat height = 252;
	CGRect frame = CGRectMake(marginLeft, y , maxSize.width, height);
    _provisionsView.frame = frame;
    
    _webView.frame = CGRectMake(0, 0 , frame.size.width, frame.size.height);
    
    frame = CGRectMake(marginLeft + 10, y + height + 10, 30, 30);
    _tipsBtn.frame = frame;
    
    frame = CGRectMake(marginLeft + 40, y + height + 8, maxSize.width - 40, 40);
    _tipsLabel.frame = frame;
    return height + 45;
}


- (void)updateTipsButton
{
    if (_tipsSelected) {
        [_tipsBtn setImage:[GTImage imageNamed:@"gt_checkbox_sel" ofType:@"png"] forState:UIControlStateNormal];
    } else {
        [_tipsBtn setImage:[GTImage imageNamed:@"gt_checkbox" ofType:@"png"] forState:UIControlStateNormal];
    }
}

- (void)selButtonClicked:(id)sender
{
    _tipsSelected = !_tipsSelected;
    [self updateTipsButton];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    [_webView loadRequest:[NSURLRequest requestWithURL:[NSURL fileURLWithPath:[[NSBundle frameworkBundle] pathForResource:@"EULA" ofType:@"html"]]]];
}

@end

#endif

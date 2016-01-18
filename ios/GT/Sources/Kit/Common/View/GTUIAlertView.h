//
//  GTUIAlertView.h
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
#import <UIKit/UIKit.h>

@class GTUIAlertView;

@protocol GTUIAlertViewDelegate<NSObject>

@optional
- (void)alertView:(GTUIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;

@end

@interface GTFullScreenOverlayView : UIWindow {
	UIWindow        *_appKeyWindow;
	UIImageView     *_imageView;
	BOOL            _shown;
}

@property (nonatomic, retain) UIWindow *appKeyWindow;

- (void)setFullscreenImage:(UIImage*)img;
- (id)initWithImage:(UIImage*)img;
- (void)show;
- (void)dismiss;

@end


@interface GTUIAlertView : GTFullScreenOverlayView <UITextFieldDelegate>
{
	UIView      * _bgView ;//背景图片
	
	NSString	* _title ;	//标题
	NSString	* _message ;//消息
	
	UILabel		* _titleLabel ;     //名称
	UILabel		* _msgLabel;
	
	UIView      * _container;// container is the same size as _backgroundView
	UIImageView * _backgroundView ;  // 最底层背景
	
	float _maxMiddenHeight ;
	
	id<GTUIAlertViewDelegate> __delegate ;
	
    NSString   * _otherButtonTitle; //用作textfiled没有输入内容时设置不可点击
	NSMutableArray* _buttonTitleArray;
	NSMutableArray* _buttonArray;
    NSMutableArray* _textFieldArray;
	
	id _object;
	int _state;
}

@property (nonatomic, assign)	id<GTUIAlertViewDelegate> delegate ;
@property (nonatomic, retain)   NSString * title;
@property (nonatomic, retain)	NSString * message;
@property (nonatomic, retain)	NSArray * buttonArray;
@property (nonatomic, assign)	float maxMiddenHeight ;
@property (nonatomic, retain)	id object ;
@property (nonatomic, assign)	int state;
@property (nonatomic, readonly) UILabel* titleLabel;
@property (nonatomic, readonly) UILabel* msgLabel;
@property (nonatomic, retain)   NSString * otherButtonTitle;

- (id)initWithTitle:(NSString *)title message:(NSString *)message delegate:(id /*<GTAlertViewDelegate>*/)delegate cancelButtonTitle:(NSString *)cancelButtonTitle otherButtonTitles:(NSString *)otherButtonTitles;

- (void)addButtonWithTitle:(NSString *)title;
- (void)addTextFieldWithTag:(NSUInteger)tag;

- (UITextField *)textFieldAtIndex:(NSInteger)textFieldIndex;
- (UITextField *)textFieldAtTag:(NSInteger)tag;

- (void) setTextAlignment:(NSTextAlignment)textAlignment;

//补充高度，用于扩展视图
- (CGFloat)heightForAddtion:(CGFloat)marginLeft offsetY:(CGFloat)y size:(CGSize)maxSize;
@end

@interface GTWebUILabel : UILabel

@end

@interface GTMsgAlertView : GTUIAlertView
{
    NSString	*_webMsg;       //补充网站信息
    UILabel     *_webMsgLabel;
}

@property(nonatomic,retain) NSString *webMsg;

@end

@interface GTProvisionsAlertView : GTUIAlertView <UIWebViewDelegate>
{
    NSString	*_provisions;      //声明信息
    UITextView	*_provisionsView;
    UIWebView   *_webView;
    
    BOOL         _tipsSelected;
    UIButton    *_tipsBtn;
    UILabel     *_tipsLabel;
}

@property(nonatomic, retain) NSString *provisions;
@property(nonatomic, assign) BOOL tipsSelected;

@end


#endif

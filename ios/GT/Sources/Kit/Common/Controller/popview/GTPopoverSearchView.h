//
//  GTPopoverSearchView.h
//  GTKit
//
//  Created   on 13-4-3.
//  Copyright (c) 2013年 Tencent. All rights reserved.
//
#ifndef GT_DEBUG_DISABLE
#import <UIKit/UIKit.h>

@interface GTPopoverSearchView : UITableViewController
{
    UITextField     *_textField;
    NSMutableArray  *_list;
}

@property (nonatomic, retain) UITextField *textField;
@property (nonatomic, retain) NSMutableArray *list;


- (id)initWithArray:(NSMutableArray *)array;

@end
#endif

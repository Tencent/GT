//
//  GTPopoverTableView.h
//  GTKit
//
//  Created   on 13-4-2.
//  Copyright (c) 2013年 Tencent. All rights reserved.
//
#ifndef GT_DEBUG_DISABLE
#import <UIKit/UIKit.h>

@class GTPopoverTableView;

@protocol GTPopoverTableViewDelegate

- (void) popoverTable:(GTPopoverTableView*)tableView didSelectRow:(NSUInteger)row;

@end


@interface GTPopoverTableView : UITableViewController
{
    NSArray         *_list;
    NSString        *_selectedTitle;
    NSIndexPath     *_lastIndexPath;
    
    id <GTPopoverTableViewDelegate> _popDelegate;
}

@property (nonatomic, retain) NSArray  *list;
@property (nonatomic, retain) NSString *selectedTitle;
@property (nonatomic, retain) NSIndexPath *lastIndexPath;
@property (nonatomic, assign) id <GTPopoverTableViewDelegate> popDelegate;

- (id)initWithArray:(NSArray *)array;


@end
#endif

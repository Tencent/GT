//
//  GTPopoverTableView.m
//  GTKit
//
//  Created   on 13-4-2.
//  Copyright (c) 2013年 Tencent. All rights reserved.
//
#ifndef GT_DEBUG_DISABLE
#import "GTPopoverTableView.h"
#import "GTDebugDef.h"
#import <QuartzCore/QuartzCore.h>

@interface GTPopoverTableView ()

@end

@implementation GTPopoverTableView

@synthesize list = _list;
@synthesize selectedTitle = _selectedTitle;
@synthesize lastIndexPath = _lastIndexPath;
@synthesize popDelegate = _popDelegate;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (id)initWithArray:(NSArray *)array
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wnonnull"
        CGFloat height = [self tableView:self.tableView heightForRowAtIndexPath:nil];
#pragma clang diagnostic pop
        height = [array count] * height;
        if (height > 300) {
            height = 300;
        }
        self.view.frame = CGRectMake(0, 0, M_GT_SCREEN_WIDTH, height);
        self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
        [self.tableView setSeparatorColor:[UIColor blackColor]];
        self.tableView.layer.borderColor = [UIColor blackColor].CGColor;
        self.tableView.layer.borderWidth = 1.0f;
        [self setList:array];
    }
    return self;
    
}


- (void)dealloc
{
    [super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.tableView.bounces = NO;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [self.list count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    // Configure the cell...
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier] autorelease];
        cell.textLabel.font = [UIFont systemFontOfSize:15];
        cell.textLabel.textAlignment = NSTextAlignmentLeft;
    }
    cell.textLabel.text =[_list objectAtIndex:indexPath.row];
    cell.textLabel.textColor = [UIColor whiteColor];
    
    UIView * v = [[[UIView alloc] init] autorelease];
    v.backgroundColor = M_GT_SELECTED_COLOR;
    cell.selectedBackgroundView = v;
    return cell;
}


#pragma mark - Table view delegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 40.0f;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    
    if (_popDelegate) {
        [_popDelegate popoverTable:self didSelectRow:indexPath.row];
    }
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([self.selectedTitle isEqualToString:cell.textLabel.text] ) {
        cell.backgroundColor = M_GT_SELECTED_COLOR;
        [cell.textLabel setTextColor:[UIColor whiteColor]];
    } else {
        cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
        [cell.textLabel setTextColor:[UIColor grayColor]];
    }

}

@end
#endif

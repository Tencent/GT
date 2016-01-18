//
//  GTSandboxBoard.m
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

#include <mach/mach.h>
#include <malloc/malloc.h>
#import <QuartzCore/QuartzCore.h>
#import "GTSandboxBoard.h"
#import "GTUtility.h"
#import "GTDetailView.h"
#import "GTImage.h"
#import "GTUIAlertView.h"
#import "GTLang.h"
#import "GTLangDef.h"

#pragma mark -

@implementation GTToolSandboxCell

+ (CGSize)cellSize:(NSObject *)data bound:(CGSize)bound
{
	return CGSizeMake( bound.width, 40.0f );
}

- (void)cellLayout
{
	CGRect iconFrame;
    CGFloat height = 40.0f;
    
	iconFrame.size.width = height/2;
	iconFrame.size.height = height/2;
	iconFrame.origin.x = 5.0f;
	iconFrame.origin.y = height/4 + 5.0;
	_iconView.frame = iconFrame;
    
    height = 50.0f;
	CGRect nameFrame;
	nameFrame.size.width = self.bounds.size.width - iconFrame.size.width - 4.0f - 60.0f;
	nameFrame.size.height = height;
	nameFrame.origin.x = iconFrame.size.width + 8.0f;
	nameFrame.origin.y = 0.0f;
	_nameLabel.frame = nameFrame;
    
	CGRect sizeFrame;
	sizeFrame.size.width = 50.0f;
	sizeFrame.size.height = height;
	sizeFrame.origin.x = self.bounds.size.width - sizeFrame.size.width - 5.0f;
	sizeFrame.origin.y = 0.0f;
	_sizeLabel.frame = sizeFrame;
}

- (void)load
{
	[super load];
	
	self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6f];
	self.layer.borderColor = [UIColor colorWithWhite:0.2f alpha:1.0f].CGColor;
	self.layer.borderWidth = 1.0f;
    
	_iconView = [[UIImageView alloc] init];
	[self addSubview:_iconView];
    
	_nameLabel = [[UILabel alloc] init];
	_nameLabel.textAlignment = NSTextAlignmentLeft;
	_nameLabel.font = [UIFont boldSystemFontOfSize:13.0];
	_nameLabel.lineBreakMode = NSLineBreakByTruncatingHead;
	_nameLabel.numberOfLines = 2;
    _nameLabel.backgroundColor = [UIColor clearColor];
    _nameLabel.textColor = [UIColor whiteColor];
	[self addSubview:_nameLabel];
	
	_sizeLabel = [[UILabel alloc] init];
	_sizeLabel.textAlignment = NSTextAlignmentRight;
	_sizeLabel.font = [UIFont boldSystemFontOfSize:12.0];
	_sizeLabel.lineBreakMode = NSLineBreakByClipping;
	_sizeLabel.numberOfLines = 1;
    _sizeLabel.backgroundColor = [UIColor clearColor];
    _sizeLabel.textColor = [UIColor whiteColor];
	[self addSubview:_sizeLabel];
}

- (void)bindData:(NSObject *)data
{
	NSString * filePath = (NSString *)data;
	if ( [filePath isEqualToString:@".."] )
	{
		_nameLabel.text = @"..";
		_iconView.image = [GTImage imageNamed:@"gt_folder" ofType:@"png"];
		_sizeLabel.text = @"";
	}
	else
	{
		BOOL isDirectory = NO;
		NSDictionary * attributes = [[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:NULL];
		if ( attributes )
		{
			if ( [[attributes fileType] isEqualToString:NSFileTypeDirectory] )
			{
				isDirectory = YES;
			}
		}
		
		_nameLabel.text = [(NSString *)data lastPathComponent];
		_iconView.image = [GTImage imageNamed: isDirectory ? @"gt_folder" : @"gt_file" ofType:@"png"];
		_sizeLabel.text = @"";
		
		if ( NO == isDirectory )
		{
			NSDictionary * attribs = [[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:NULL];
			if ( attribs )
			{
				NSNumber * size = [attribs objectForKey:NSFileSize];
				_sizeLabel.text = [GTUtility number2String:[size integerValue]];
			}
		}
	}
}

- (void)unload
{
	M_GT_SAFE_FREE( _iconView );
	M_GT_SAFE_FREE( _nameLabel );
	M_GT_SAFE_FREE( _sizeLabel );
	
	[super unload];
}

@end

#pragma mark -

@implementation GTSandboxBoard

M_GT_DEF_SINGLETION( GTSandboxBoard )

@synthesize folderDepth = _folderDepth;
@synthesize filePath = _filePath;
@synthesize tableView = _tableView;


- (id) init
{
    self = [super init];
    if (self) {
        [self load];

    }
    
    return self;
}

- (void)dealloc
{
	[self unload];
    [super dealloc];
}

- (void)load
{
	_filePath = [NSHomeDirectory() retain];
}

- (void)unload
{
	[_filePath release];
	[_fileArray release];
    [_tableView release];
}

- (void)initUI
{
    [self.view setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.75]];
    [self createTopBar];
    [self setNavTitle:[self.filePath lastPathComponent]];
    
    CGRect frame = M_GT_BOARD_FRAME;
    
    _tableView = [[UITableView alloc] initWithFrame:frame style:UITableViewStylePlain];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.backgroundView = nil;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.rowHeight = 44;
    _tableView.showsVerticalScrollIndicator = NO;
    _tableView.showsHorizontalScrollIndicator = NO;
    _tableView.bounces = NO;
    [_tableView.tableHeaderView setNeedsLayout];
    [_tableView.tableHeaderView setNeedsDisplay];
    [self.view addSubview:_tableView];
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self initUI];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if ( nil == _fileArray )
    {
        _fileArray = [[NSMutableArray alloc] init];
        [_fileArray addObject:@".."];
        [_fileArray addObjectsFromArray:[[NSFileManager defaultManager] contentsOfDirectoryAtPath:self.filePath error:NULL]];
    }
}

#pragma mark - UITableViewDataSource

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    //得到当前文件的完整路径
    //获取到document下面的文件：
    NSString * file = [_fileArray objectAtIndex:indexPath.row];
	NSString * path = [NSString stringWithFormat:@"%@/%@", self.filePath, file];
    
    NSError *error;
	if ([[NSFileManager defaultManager] removeItemAtPath:path error:&error] != YES) {
        NSLog(@"GT Unable to delete file:%@ error:%@", path, [error localizedDescription]);
    } else {
        [_fileArray removeObjectAtIndex:indexPath.row];
    }
    
    [_tableView reloadData];
    
}

#pragma mark - UITableViewDelegate

//列表横向滑动产生delete操作
- (UITableViewCellEditingStyle)tableView:(UITableView *)tv editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
	return UITableViewCellEditingStyleDelete;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
	CGSize bound = CGSizeMake( tableView.bounds.size.width, 0.0f );
	return [GTToolSandboxCell cellSize:nil bound:bound].height;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return [_fileArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSString* cellIdentifier = [NSStringFromClass([self class]) stringByAppendingFormat:@"[%ld,%ld]", (long)indexPath.section, (long)indexPath.row];
    
    GTToolSandboxCell * cell = (GTToolSandboxCell *)[_tableView  dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
		cell = [[[GTToolSandboxCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:cellIdentifier withFrame:_tableView.frame] autorelease];
	}
    
    NSString * file = [_fileArray objectAtIndex:indexPath.row];
    if ( [file isEqualToString:@".."] )
    {
        [cell bindData:file];
    }
    else
    {
        NSString * path = [NSString stringWithFormat:@"%@/%@", self.filePath, file];
        [cell bindData:path];
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [_tableView deselectRowAtIndexPath:indexPath animated:YES];
	NSString * file = [_fileArray objectAtIndex:indexPath.row];
	if ( [file isEqualToString:@".."] )
	{
        [self.navigationController popViewControllerAnimated:YES];
		return;
	}
	
	BOOL isDirectory = NO;
	NSString * path = [NSString stringWithFormat:@"%@/%@", self.filePath, file];
	NSDictionary * attributes = [[NSFileManager defaultManager] attributesOfItemAtPath:path error:NULL];
	if ( attributes )
	{
		if ( [[attributes fileType] isEqualToString:NSFileTypeDirectory] )
		{
			isDirectory = YES;
		}
	}
    
	if ( isDirectory )
	{
		GTSandboxBoard * board = [[GTSandboxBoard alloc] init];
		board.folderDepth = _folderDepth + 1;
		board.filePath = path;
        [self.navigationController pushViewController:board animated:YES];
		[board release];
	}
	else
	{
		if ( [path hasSuffix:@".png"]  || [path hasSuffix:@".PNG"]
          || [path hasSuffix:@".jpg"]  || [path hasSuffix:@".JPG"]
          || [path hasSuffix:@".jpeg"] || [path hasSuffix:@".JPEG"]
          || [path hasSuffix:@".gif"]  || [path hasSuffix:@".GIF"] )
		{
			CGRect detailFrame = _tableView.bounds;
			detailFrame.size.height -= 44.0f;
			
			GTImageView * detailView = [[GTImageView alloc] initWithFrame:detailFrame];
			[detailView setFilePath:path];
            [self.view addSubview:detailView];
            [self.view bringSubviewToFront:detailView];
			[detailView release];
		}
		else if ( [path hasSuffix:@".strings"] || [path hasSuffix:@".plist"] || [path hasSuffix:@".txt"] || [path hasSuffix:@".log"] || [path hasSuffix:@".csv"] )
		{
            CGRect bounds = _tableView.bounds;
			CGRect detailFrame = bounds;
			detailFrame.size.height -= 44.0f;
			
			GTTextView * detailView = [[GTTextView alloc] initWithFrame:detailFrame];
			[detailView setFilePath:path];
            [self.view addSubview:detailView];
            [self.view bringSubviewToFront:detailView];
			[detailView release];
		}
        else
        {
            CGRect bounds = _tableView.bounds;
			CGRect detailFrame = bounds;
			detailFrame.size.height -= 44.0f;
			
			GTTextView * detailView = [[GTTextView alloc] initWithFrame:detailFrame];
            NSString *str = [NSString stringWithFormat:@"Not support %@ file!", [path pathExtension]];
			[detailView setContentText:str];
            [self.view addSubview:detailView];
            [self.view bringSubviewToFront:detailView];
			[detailView release];
        }
	}
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    cell.backgroundColor = M_GT_CELL_BKGD_COLOR;
    cell.layer.borderColor = M_GT_CELL_BORDER_COLOR.CGColor;
	cell.layer.borderWidth = M_GT_CELL_BORDER_WIDTH;
}

@end

#endif

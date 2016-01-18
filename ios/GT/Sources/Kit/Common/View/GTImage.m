//
//  GTImage.m
//  GTKit
//
//  Created   on 13-2-27.
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
#import "GTImage.h"

@implementation NSBundle(GT)

+ (NSBundle *)frameworkBundle {
    static NSBundle* frameworkBundle = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        NSString* mainBundlePath = [[NSBundle mainBundle] resourcePath];
        NSString* frameworkBundlePath = [mainBundlePath stringByAppendingPathComponent:@"GT.bundle"];
//        frameworkBundle = [[NSBundle bundleWithPath:frameworkBundlePath] retain];
        frameworkBundle = [NSBundle bundleWithPath:frameworkBundlePath];
    });
    return frameworkBundle;
}

@end

@implementation GTImage

M_GT_DEF_SINGLETION( GTImage )

-(id) init
{
    self = [super init];
    if (self) {
        [self load];
    }
    
    return self;
}

-(void) dealloc
{
    [self unload];
    [super dealloc];
}

- (void)load
{
    _dict = [[NSMutableDictionary dictionaryWithCapacity:50] retain];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(clearMemory)
                                                 name:UIApplicationDidReceiveMemoryWarningNotification
                                               object:nil];
}

- (void)unload
{
    [_dict release];
}


- (void)clearMemory
{
    [_dict removeAllObjects];
}

- (UIImage *)imageForNamed:(NSString *)name ofType:(NSString *)ext
{
    NSString *imageKey = [NSString stringWithFormat:@"%@.%@", name, ext];
    
    UIImage *image = [_dict objectForKey:imageKey];
    if (image == nil) {
        image = [UIImage imageWithContentsOfFile:[[NSBundle frameworkBundle] pathForResource:name ofType:ext]];
        
        if (image == nil) {
            return nil;
        }
        //保存图像，节省重新加载资源
        [_dict setObject:image forKey:imageKey];
    }
    return image;
}

+ (UIImage *)imageNamed:(NSString *)name ofType:(NSString *)ext
{
    return [UIImage imageWithContentsOfFile:[[NSBundle frameworkBundle] pathForResource:name ofType:ext]];
    return [[GTImage sharedInstance] imageForNamed:name ofType:ext];
}

+ (UIImage *)image:(UIImage *)image scaleAspectFitSize:(CGSize)size
{
    if (!image) {
		return nil;
	}
    
    // 重设size
    UIImage *newImage;
    CGFloat width = size.width;
    CGFloat height = size.height;
    
    UIGraphicsBeginImageContext(size);
    [image drawInRect:CGRectMake(0, 0, width, height)];
    newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext(); 
    
    return newImage;
}

@end
#endif

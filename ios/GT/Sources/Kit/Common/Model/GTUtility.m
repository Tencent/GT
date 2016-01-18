//
//  GTUtility.m
//  GTKit
//
//  Created   on 12-12-13.
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
#import "GTUtility.h"
#import <sys/sysctl.h>

@implementation GTUtility

M_GT_DEF_SINGLETION(GTUtility);

@synthesize systemVersion = _systemVersion;

- (id)init
{
    self = [super init];
    if (self) {
        _systemVersion = [[[UIDevice currentDevice] systemVersion] doubleValue];
    }
    
    return self;
}

- (void)dealloc
{
    [super dealloc];
}

+ (NSTimeInterval)timeIntervalSince1970
{
    struct  timeval    tv;
    struct  timezone   tz;
    gettimeofday(&tv,&tz);
    return (tv.tv_sec + tv.tv_usec/1000000.0);
}

+ (NSString *)number2String:(int64_t)n
{
	if ( n < M_GT_KB )
	{
		return [NSString stringWithFormat:@"%lldB", n];
	}
	else if ( n < M_GT_MB )
	{
		return [NSString stringWithFormat:@"%.2fK", (float)n / (float)M_GT_KB];
	}
	else if ( n < M_GT_GB )
	{
		return [NSString stringWithFormat:@"%.2fM", (float)n / (float)M_GT_MB];
	}
	else
	{
		return [NSString stringWithFormat:@"%.2fG", (float)n / (float)M_GT_GB];
	}
}


+ (UIImage *)scaleImage:(UIImage *)image toScale:(float)scaleSize
{
    UIGraphicsBeginImageContext(CGSizeMake(image.size.width * scaleSize, image.size.height * scaleSize));
    [image drawInRect:CGRectMake(0, 0, image.size.width * scaleSize, image.size.height * scaleSize)];
    UIImage *scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return scaledImage;
}

+ (UIImage*) image:(UIImage*) image scaleAspectFitSize:(CGSize)size
{
	if (!image) {
		return nil;
	}
	
	CGSize imageSize = image.size;
	
	//如果原图大小已经比目标size 小，就不缩放了.
    if ((imageSize.width <=  size.width) && (imageSize.height <= size.height) ) {
        [image retain];
		return [image autorelease];
    }
    float s_width = imageSize.width / size.width;
    float s_height = imageSize.height / size.height;
    float hv = (s_width > s_height) ? s_width : s_height;
   
    //	if (hv <= 1.0f) return image;
	if (hv <= 1.0f) {
		hv = 1.0f;
	}
	
	int width = image.size.width / hv;
	int height = image.size.height / hv;
	
	CGSize n_size = {width, height};
	
	UIGraphicsBeginImageContext(n_size);
	
	[image drawInRect:CGRectMake(0, 0, n_size.width, n_size.height)];
	
    UIImage* graphicsImage = UIGraphicsGetImageFromCurrentImageContext();
    UIImage* n_image = [graphicsImage retain];
    
    UIGraphicsEndImageContext();
    return [n_image autorelease];
}


@end
#endif

# 最新变动 #
    1，获取进程的内存快照，并支持设定阈值自动dump。

    2，支持对指定进程进行GC操作。

    3，更新图标。

	4，bugfix
    
# 项目介绍 #
    APT是一个eclipse插件，可以实时监控Android手机上多个应用的CPU、内存数据曲线，并保存数据；
	另外还支持自动获取内存快照、PMAP文件分析等，方便开发人员自测或者测试人员完成性能测试，快速
	发现产品问题。

# 功能摘要 #
    1.支持多进程的CPU测试，并提供top和dumpsys cpuinfo两种方式.
    
    2.支持多进程的内存测试，并支持9种内存类型，测试过程中可动态调整要显示的内存类型曲线.
    
    3.支持自动获取内存快照.
    
    4.支持PMAP内存分析对比.


# 编译方法 #
	下载Eclipse for RCP and RAP Developers版本的eclipse，将工程导入即可。

# 使用方法 #
    把下载的jar文件放到eclipse的plugins目录下面，重启eclipse，
	执行下面的操作【Window】-》【Open Perspective】-》【Other】选择APT透视图。

    
# 注意事项 #
    
    1.APT中的DumpHprof和GC功能由于用到了DDMLIB的功能，所以有两个限制：
	首先，确保手机系统或者被测应用是可调试的；
	其次，DDMLIB不允许同时有多个工具获取被调试手机上的进程信息，所以如果想用APT的这两个功能，
	最好把eclipse的透视图切换到APT，然后重启eclipse。

    2.获取PMAP文件需要root权限，并且需要默认root。如果进入adb shell默认不是root权限，
	可以优先执行adb root即可。 

    3.最好将adb路径加入到path中。
	
	4.eclipse需要安装ADT插件

  


# <font color=black>性能测试--基础性能</font>


### <font color=#436EEE>基础性能维度：</font>

**1.<u>CPU</u>：** 表示进程或线程的繁忙程度。

**2.<u>内存</u>：** 表示当前进程内存的使用情况，内存占用过高可能会引起内存抖动，或OutOfMemory异常。

**3.<u>流量</u>：** 表示当前进程网络的使用情况。


### <font color=#436EEE>一、CPU：</font>

/proc文件系统是一个伪文件系统，它只存在内存当中，而不占用外存空间。它以文件系统的方式为内核与进程提供通信的接口。用户和应用程序可以通过/proc得到系统的信息，并可以改变内核的某些参数。由于系统的信息，如进程，是动态改变的，所以用户或应用程序读取/proc目录中的文件时，proc文件系统是动态从系统内核读出所需信息并提交的。 从proc文件中可以获取系统、进程、线程的cpu时间片使用情况，所以两次采集时间片的数据就可以获取进程CPU占用率， CPU占用率 = (进程T2-进程T1)/(系统T2-系统T1) 的时间片比值。

**1.获取系统CPU时间片**

获取系统CPU时间片使用情况：读取proc/stat，文件的内容如下：

		cpu 2032004 102648 238344 167130733 758440 15159 17878 0
		cpu0 1022597 63462 141826 83528451 366530 9362 15386 0
		cpu1 1009407 39185 96518 83602282 391909 5796 2492 0
		intr 303194010 212852371 3 0 0 11 0 0 2 1 1 0 0 3 0 11097365 0 72615114 6628960 0 179 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
		ctxt 236095529
		btime 1195210746
		processes 401389
		procs_running 1
		procs_blocked 0

第一行各个字段的含义：

		user (14624) 从系统启动开始累计到当前时刻，处于用户态的运行时间，不包含 nice值为负进程。 
		nice (771) 从系统启动开始累计到当前时刻，nice值为负的进程所占用的CPU时间 
		system (8484) 从系统启动开始累计到当前时刻，处于核心态的运行时间 
		idle (283052) 从系统启动开始累计到当前时刻，除IO等待时间以外的其它等待时间 
		iowait (0) 从系统启动开始累计到当前时刻，IO等待时间(since 2.5.41) 
		irq (0) 从系统启动开始累计到当前时刻，硬中断时间(since 2.6.0-test4) 
		softirq (62) 从系统启动开始累计到当前时刻，软中断时间(since 2.6.0-test4) 

总的cpu时间totalCpuTime = user + nice + system + idle + iowait + irq + softirq 

**2.获取进程和线程的CPU时间片**

获取进程CPU时间片使用情况：读取proc/pid/stat，获取线程CPU时间片使用情况：读取proc/pid/task/tid/stat，这两个文件的内容相同，如下

		6873 (a.out) R 6723 6873 6723 34819 6873 8388608 77 0 0 0 41958 31 0 0 25 0 3 0 5882654 1409024 56 4294967295 134512640 134513720 3215579040 0 2097798 0 0 0 0 0 0 0 17 0 0 0

各个字段的含义：

        pid=6873 进程(包括轻量级进程，即线程)号
		comm=a.out 应用程序或命令的名字
		task_state=R 任务的状态，R:runnign, S:sleeping (TASK_INTERRUPTIBLE), D:disk sleep (TASK_UNINTERRUPTIBLE), T: stopped, T:tracing stop,Z:zombie, X:dead
		ppid=6723 父进程ID
		pgid=6873 线程组号
		sid=6723 c该任务所在的会话组ID
		tty_nr=34819(pts/3) 该任务的tty终端的设备号，INT（34817/256）=主设备号，（34817-主设备号）=次设备号
		tty_pgrp=6873 终端的进程组号，当前运行在该任务所在终端的前台任务(包括shell 应用程序)的PID。
		task->flags=8388608 进程标志位，查看该任务的特性
		min_flt=77 该任务不需要从硬盘拷数据而发生的缺页（次缺页）的次数
		cmin_flt=0 累计的该任务的所有的waited-for进程曾经发生的次缺页的次数目
		maj_flt=0 该任务需要从硬盘拷数据而发生的缺页（主缺页）的次数
		cmaj_flt=0 累计的该任务的所有的waited-for进程曾经发生的主缺页的次数目
		utime=1587 该任务在用户态运行的时间，单位为jiffies
		stime=1 该任务在核心态运行的时间，单位为jiffies
		cutime=0 累计的该任务的所有的waited-for进程曾经在用户态运行的时间，单位为jiffies
		cstime=0 累计的该任务的所有的waited-for进程曾经在核心态运行的时间，单位为jiffies
		priority=25 任务的动态优先级
		nice=0 任务的静态优先级
		num_threads=3 该任务所在的线程组里线程的个数
		it_real_value=0 由于计时间隔导致的下一个 SIGALRM 发送进程的时延，以 jiffy 为单位.
		start_time=5882654 该任务启动的时间，单位为jiffies
		vsize=1409024（page） 该任务的虚拟地址空间大小
		rss=56(page) 该任务当前驻留物理地址空间的大小
		rlim=4294967295（bytes） 该任务能驻留物理地址空间的最大值
		start_code=134512640 该任务在虚拟地址空间的代码段的起始地址
		end_code=134513720 该任务在虚拟地址空间的代码段的结束地址
		start_stack=3215579040 该任务在虚拟地址空间的栈的结束地址
		kstkesp=0 esp(32 位堆栈指针) 的当前值, 与在进程的内核堆栈页得到的一致.
		kstkeip=2097798 指向将要执行的指令的指针, EIP(32 位指令指针)的当前值.
		pendingsig=0 待处理信号的位图，记录发送给进程的普通信号
		block_sig=0 阻塞信号的位图
		sigign=0 忽略的信号的位图
		sigcatch=082985 被俘获的信号的位图
		wchan=0 如果该进程是睡眠状态，该值给出调度的调用点
		nswap 被swapped的页数，当前没用
		cnswap 所有子进程被swapped的页数的和，当前没用
		exit_signal=17 该进程结束时，向父进程所发送的信号
		task_cpu(task)=0 运行在哪个CPU上
		task_rt_priority=0 实时进程的相对优先级别
		task_policy=0 进程的调度策略，0=非实时进程，1=FIFO实时进程；2=RR实时进程

进程的总Cpu时间processCpuTime = utime + stime + cutime + cstime

线程的总Cpu时间threadCpuTime = utime + stime + cutime + cstime

我们统计的CPU使用，已经将GT引入线程的损耗在总体的CPU使用中排除，因此结果可靠；


### <font color=#436EEE>二、内存：</font>


**1.系统内存**

1）系统内存总容量：只需要读取“/proc/meminfo”文件的第一个字段“MemTotal”就可以了，文件的内容如下：

		MemTotal:          94096 kB
		MemFree:            1684 kB
		Buffers:              16 kB
		Cached:            27160 kB
		SwapCached:            0 kB
		Active:            35392 kB
		Inactive:          44180 kB
		Active(anon):      26540 kB
		Inactive(anon):    28244 kB
		Active(file):       8852 kB
		Inactive(file):    15936 kB
		Unevictable:         280 kB
		Mlocked:               0 kB
		SwapTotal:             0 kB
		SwapFree:              0 kB
		Dirty:                 0 kB
		Writeback:             0 kB
		AnonPages:         52688 kB
		Mapped:            17960 kB
		Slab:               3816 kB
		SReclaimable:        936 kB
		SUnreclaim:         2880 kB
		PageTables:         5260 kB
		NFS_Unstable:          0 kB
		Bounce:                0 kB
		WritebackTmp:          0 kB
		CommitLimit:       47048 kB
		Committed_AS:    1483784 kB
		VmallocTotal:     876544 kB
		VmallocUsed:       15456 kB
		VmallocChunk:     829444 kB

各个字段的含义：

		MemTotal: 所有可用RAM大小。
		MemFree: LowFree与HighFree的总和，被系统留着未使用的内存。
		Buffers: 用来给文件做缓冲大小。
		Cached: 被高速缓冲存储器（cache memory）用的内存的大小（等于diskcache minus SwapCache）。
		SwapCached:被高速缓冲存储器（cache memory）用的交换空间的大小。已经被交换出来的内存，仍然被存放在swapfile中，用来在需要的时候很快的被替换而不需要再次打开I/O端口。
		Active: 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要，否则不会被移作他用。
		Inactive: 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径。
		SwapTotal: 交换空间的总大小。
		SwapFree: 未被使用交换空间的大小。
		Dirty: 等待被写回到磁盘的内存大小。
		Writeback: 正在被写回到磁盘的内存大小。
		AnonPages：未映射页的内存大小。
		Mapped: 设备和文件等映射的大小。
		Slab: 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。
		SReclaimable:可收回Slab的大小。
		SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）。
		PageTables：管理内存分页页面的索引表的大小。
		NFS_Unstable:不稳定页表的大小。

2）系统空闲的内存：只需要通过ActivityManager即可获取

	    //系统空闲内存
	    public static long getSysFreeMemory(Context context) {
	        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
	        am.getMemoryInfo(mi);
	        return mi.availMem;
	    }

2）系统已用内存：
	



**3.进程内存**

1）进程内存上限：

		//进程内存上限
	    public static int getMemoryMax() {
	        return (int) (Runtime.getRuntime().maxMemory()/1024);
	    }

2）进程总内存：

	    //进程总内存
	    public static int getPidMemorySize(int pid, Context context) {
	        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        int[] myMempid = new int[] { pid };
	        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
	        int memSize = memoryInfo[0].getTotalPss();
			//        dalvikPrivateDirty： The private dirty pages used by dalvik。
			//        dalvikPss ：The proportional set size for dalvik.
			//        dalvikSharedDirty ：The shared dirty pages used by dalvik.
			//        nativePrivateDirty ：The private dirty pages used by the native heap.
			//        nativePss ：The proportional set size for the native heap.
			//        nativeSharedDirty ：The shared dirty pages used by the native heap.
			//        otherPrivateDirty ：The private dirty pages used by everything else.
			//        otherPss ：The proportional set size for everything else.
			//        otherSharedDirty ：The shared dirty pages used by everything else.
	        return memSize;
	    }



### <font color=#436EEE>三、流量：</font>

TrafficStats类是由Android提供的一个从你的手机开机开始，累计到现在使用的流量总量，或者统计某个或多个进程或应用所使用的流量，当然这个流量包括的Wifi和移动数据网Gprs。

		//系统流量统计：
		TrafficStats.getTotalRxBytes() ——获取从此次开机起总接受流量（流量是分为上传与下载两类的，当然其实这里还有本地文件之间数据交换的流量，这个暂且不说，等下说明一下我遇到的问题）；
		TrafficStats.getTotalTxBytes()——获取从此次开机起总发送流量；
		TrafficStats.getMobileRxBytes()——获取从此次开机起不包括Wifi的接受流量，即只统计数据网Gprs接受的流量；
		TrafficStats.getMobileTxBytes()——获取从此次开机起不包括Wifi的发送流量，即只统计数据网Gprs发送的流量；
		//进程流量统计：
		TrafficStats.getUidRxBytes(mUid)
	    TrafficStats.getUidTxBytes(mUid)	
		
获取进程流量的方法：

	 	public  static TrafficInfo collect(int mUid) {
	        long upload  = TrafficStats.getUidRxBytes(mUid);
	        long download = TrafficStats.getUidTxBytes(mUid);
	
	        TrafficInfo trafficInfo = new TrafficInfo();
	        trafficInfo.upload = upload;
	        trafficInfo.download = download;
	
	       return  trafficInfo;
	    }


### <font color=#436EEE>四、基础性能检测的结果：</font>

下图是基础性能检测的结果：


<div  align="center">   
<img src="http://ww4.sinaimg.cn/large/a15b4afegy1fh7udabexej20tn0otgpj" width = "80%" alt="图片名称" align=center />
</div>


package com.tencent.wstt.gt.analysis4.obj;


/** dalvikvm 日志格式： **/
// D/dalvikvm:<GC_Reason><Amount_freed>,<Heap_stats>,<Pause_time>,<Total_time>
/** Art 日志格式： **/
/*  I/ art :< GC_Reason><Amount_freed>,<LOS_Space_Status>,<Heap_stats>,<Pause_time >,<Total_time>:  */
//Alloc sticky concurrent mark sweep GC freed 2527(552KB) AllocSpace objects, 6(120KB) LOS objects, 4% free, 222MB/233MB, paused 887us total 177.619ms
//Alloc partial concurrent mark sweep GC freed 713(83KB) AllocSpace objects, 1(20KB) LOS objects, 6% free, 222MB/238MB, paused 59.746ms total 505.132ms
//Alloc concurrent mark sweep GC freed 183(34KB) AllocSpace objects, 0(0B) LOS objects, 6% free, 222MB/238MB, paused 928us total 500.274ms
//Background sticky  concurrent mark sweep GC freed 53627(11MB) AllocSpace objects, 226(4MB) LOS objects, 75% free, 5MB/20MB, paused 3.554ms total 104.372ms
//Background partial concurrent mark sweep GC freed 53627(11MB) AllocSpace objects, 226(4MB) LOS objects, 75% free, 5MB/20MB, paused 3.554ms total 104.372ms
//Background full concurrent mark sweep GC freed 53627(11MB) AllocSpace objects, 226(4MB) LOS objects, 75% free, 5MB/20MB, paused 3.554ms total 104.372ms
//Explicit  concurrent mark sweep GC freed 53627(11MB) AllocSpace objects, 226(4MB) LOS objects, 75% free, 5MB/20MB, paused 3.554ms total 104.372ms
//HomogeneousSpaceCompact marksweep + semispace GC freed 100(33KB) AllocSpace objects, 0(0B) LOS objects, 6% free, 222MB/238MB, paused 1.298s total 1.298s
/** dalvikvm gcReason: 什么触发了GC，以及属于哪种类型的垃圾回收，可能出现的值包括 **/
//1.GC_CONCURRENT: heap快满了引起的并发GC
//2.GC_FOR_MALLOC: heap已经满了，此时app尝试分配内存，所以系统只好阻止app并回收内存，导致内存分配失败
//3.GC_HPROF_DUMP_HEAP: 请求创建一个用来分析heap的HPROF文件，引发GCP.S.hprof文件描述了heap使用情况，类似于Chrome的heapsnapshot文件
//4.GC_EXPLICIT: 显式GC，例如调用gc()（这种方式应该避免，要相信GC会在需要的时候自动执行）
//5.GC_EXTERNAL_ALLOC: 外部内存分配（比如像素数据存储在native memory里或者NIO byte buffer）引起的GC，只会在API 10和更低版本中出现（新一点的版本所有东西都在Dalvik heap里分配）
//1.GC_FOR_MALLOC: 表示是在堆上分配对象时内存不足触发的GC。
//2.GC_CONCURRENT: 当我们应用程序的堆内存达到一定量，或者可以理解为快要满的时候，系统会自动触发GC操作来释放内存。
//3.GC_EXPLICIT: 表示是应用程序调用System.gc、VMRuntime.gc接口或者收到SIGUSR1信号时触发的GC。
//4.GC_BEFORE_OOM: 表示是在准备抛OOM异常之前进行的最后努力而触发的GC。
/** Art gcReason: 什么触发了GC，以及属于哪种类型的垃圾回收，可能出现的值包括一下几种 **/
//1.Concurrent:并发GC，不会挂起app线程，这种GC在后台线程中运行，不会阻止内存分配
//2.Alloc: GC被初始化，app在heap已满的时候请求分配内存，此时，GC会在当前线程（请求分配内存的线程）执行
//3.Explicit: GC被app显式请求，例如，通过调用 System.gc() 或者 runtime.gc() 。和Dalvik一样，ART建议相信GC，尽可能地避免请求显式GC。不建议使用显式GC，因为会阻塞当前线程，并引起不必要的CPU周期。如果GC导致其它线程被抢占的话，显式GC还会引发jankP.S.jank是指第n帧绘制过后，本该绘制第n+1帧，但因为CPU被抢占，数据没有准备好，只好再显示一次第n帧，下一次绘制时显示第n+1帧
//4.NativeAlloc: 来自native分配的native memory压力引起的GC，比如Bitmap或者RenderScript对象
//5.CollectorTransition: heap变迁引起的GC，运行时动态切换GC造成的，垃圾回收器变迁过程包括从free-list backed space复制所有对象到bump pointer space（反之亦然）。当前垃圾回收器过渡只会在低RAM设备的app改变运行状态时发生，比如从可察觉的停顿态到非可察觉的停顿态（反之亦然）
//6.HomogeneousSpaceCompact: Homogeneous space compaction是free-list space与free-list space的合并，经常在app变成不可察觉的停顿态时发生，这样做的主要原因是减少RAM占用并整理heap碎片
//7.DisableMovingGc: 不是一个真正的GC原因，正在整理碎片的GC被GetPrimitiveArrayCritical阻塞，一般来说，因为GetPrimitiveArrayCritical会限制垃圾回收器过渡，强烈建议不要使用
//8.HeapTrim: 不是一个真正的GC原因，但GC被阻塞，直到heap trim结束
//1.kGcCauseExplicit，显示调用的时候进行的gc，如果 ART 打开了这个选项的情况下，在system.gc的时候会进行GC.
//2.kGcCauseBackground: 当内存达到一定的阀值的时候会去出发GC，这个时候是一个后台GC，不会引起Stop World.
//3.kGcCauseForAlloc: 当要分配内存的时候发现内存不够的情况下引起的GC，这种情况下的GC会Stop World.

/** Art gcType：ART有几种不同的GC
 *      art虚拟机有6个GC回收器，分为两组（一组并发，一组非并发），每组有一下三个kGcTypeSticky、kGcTypePartial和kGcTypeFul，回收力度依次加大
 *      依次执行三种类型的GC。每次GC执行完毕，都尝试调用Heap类的成员函数TryToAllocate在不增长当前堆大小的前提下再次尝试分配请求的内存。
 *      如果分配内存成功，则返回得到的内存起始地址给调用者，并且不再执行下一种类型的GC。
 **/
//1.Concurrent mark sweep (CMS): 全堆垃圾收集器，负责收集释放除image外的所有空间
//2.Concurrent partial mark sweep: 差不多是全堆垃圾收集器，负责收集除image和zygote外的所有空间
//3.Concurrent sticky mark sweep: 分代垃圾收集器，只负责释放从上次GC到现在分配的对象，该GC比全堆和部分标记清除（mark sweep）执行得更频繁，因为它更快而且停顿更短
//4.Marksweep + semispace: 非并发的，复制堆过渡和homogeneous space compaction（用来整理heap碎片）使用的GC
//1.kGcTypeSticky:只回收上次GC后在Allocation Space中新分配的垃圾对象
//2.kGcTypePartial:只回收Allocation Space的垃圾对象
//3.kGcTypeFull:同时回收Zygote Space和Allocation Space的垃圾对象


//dalvikvm虚拟机：
//格式： D/dalvikvm: <GC_Reason> <Amount_freed>, <Heap_stats>, <External_memory_stats>, <Pause_time>
//格式： D/dalvikvm: <GC触发原因> <GC释放空间>, <堆统计信息>, <外部内存统计>, <暂停时间>
//样例： D/dalvikvm( 9050): GC_CONCURRENT freed 2049K, 65% free 3571K/9991K, external 4703K/5261K, paused 2ms+2ms
//ART虚拟机：
//格式： I/art: <GC_Reason> <GC_Name> <Objects_freed>(<Size_freed>) AllocSpace Objects, <Large_objects_freed>(<Large_object_size_freed>) <Heap_stats> LOS objects, <Pause_time(s)>
//格式： I/art: <GC触发原因> <GC名称> <释放对象个数>(<释放字节数>) AllocSpace Objects, <释放大对象个数>(<释放大对象字节数>)  LOS objects, <堆统计>,<暂停时间>
//样例： I/art : Explicit concurrent mark sweep GC freed 104710(7MB) AllocSpace objects, 21(416KB) LOS objects, 33% free, 25MB/38MB, paused 1.230ms total 67.216ms




/**
 * Created by p_hongjcong on 2017/4/6.
 *
 */
public class GCInfo   {


    public String vm = "";                  //虚拟机类型（art、dalvikvm）
    public String gcLog = "";               //GC的原始Log
    public long gcLogTime = 0;              //GC的原始Log时间

    public String gcResult = "";            //GC原因（alloc、concurrent、explicit、oom）
    public String freedInfo = "";           //释放情况
    public String freedLargeInfo = "";      //释放情况（大对象）
    public String heapStatistics = "";      //堆统计
    public long pauseTime = 0;              //非GC线程挂起时长 um
    public long totalTime = 0;              //GC总时长 um

    public GCInfo(){

    }


    public static GCInfo initGcInfo(String gcTag,String gcString ,long time) {

        if(gcTag.contains("art")){
            return analysisArtGCLog(gcString,time);
        }else if(gcTag.contains("dalvik")){
            return analysisDalvikGCLog(gcString,time);
        }else {
            return null;
        }
    }
    private static GCInfo analysisArtGCLog(String gcString ,long gcLogTime) {
        GCInfo gcInfo = new GCInfo();
        gcInfo.gcLog = gcString;
        gcInfo.gcLogTime = gcLogTime;
        gcInfo.vm = "art";
        String[] sss = gcString.split(" GC freed ");
        String[] ddd = sss[0].split(" ");
        gcInfo.gcResult = ddd[1];
        String[] fff = sss[1].split(",");
        gcInfo.freedInfo = fff[0].replace(" AllocSpace objects","");
        gcInfo.freedLargeInfo = fff[1].replace(" LOS objects","");
        gcInfo.heapStatistics = fff[2] + fff[3];
        String[] ggg = fff[4].split(" ");
        gcInfo.pauseTime = getUSTimeFromString(ggg[2]);
        gcInfo.totalTime = getUSTimeFromString(ggg[4]);
        return gcInfo;
    }


    private static GCInfo analysisDalvikGCLog(String gcString ,long gcLogTime) {
        GCInfo gcInfo = new GCInfo();
        gcInfo.gcLog = gcString;
        gcInfo.gcLogTime = gcLogTime;
        gcInfo.vm = "dalvik";

        return gcInfo;
    }


    private static long getUSTimeFromString(String timeString) {
        double time = 0;
        long base = 1;//单位换算基数
        if(timeString.contains("us")){
            time = Double.parseDouble(timeString.replace("us",""));
            base = 1;
        }else if(timeString.contains("ms")){
            time = Double.parseDouble(timeString.replace("ms",""));
            base = 1000;
        }else if(timeString.contains("s")){
            time = Double.parseDouble(timeString.replace("s",""));
            base = 1000000;
        }
        return (long) (time*base);
    }

}

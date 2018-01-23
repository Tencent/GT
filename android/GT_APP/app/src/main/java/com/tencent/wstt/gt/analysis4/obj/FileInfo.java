package com.tencent.wstt.gt.analysis4.obj;




/**
 * 一条文件数据
 * @author p_hongjcong
 *
 */
public class FileInfo {
	
	public static final String OPEN = "open";
	public static final String WRITE = "initBufferedWriter";
	public static final String READ = "read";
	
	
	public int fd;//文件ID
	public String filePath;//文件路径
	public String fileName;//文件名

	public String actionName;//执行函数名 open、read、initBufferedWriter
	public long actionStart;//执行时间
	public long actionEnd;//执行时间
	public int actionSize;//数据大小
	
	public int threadID;//执行的线程
	public String threadName;//执行的线程
	
	
	
	
	
	
	
	
}

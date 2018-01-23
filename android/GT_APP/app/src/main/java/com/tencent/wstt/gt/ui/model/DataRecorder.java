/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 * 
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 * 
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tencent.wstt.gt.ui.model;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 保存历史记录的数据结构，GT版本为性能优化剥离出的新设计
 * @since 2.1
 */
public class DataRecorder<T> {
	private int size = 0; // 数据源的总长度
	private ArrayList<ArrayList<T>> dataSet; // 数据源中分桶
	private ArrayList<T> curBucket; // 当前桶，避免总从dataSet计算获取影响效率
	
	// 锁
	private ReadWriteLock lock = new ReentrantReadWriteLock();

	// 单个存储列表的长度，主要为性能考虑，需要是2的n次方，且方便位移计算
	private static final int CAPACITY = 4096;
	
	public DataRecorder()
	{
		dataSet = new ArrayList<ArrayList<T>>();
	}

	/**
	 * @param start 起始数据序号
	 * @param end 结束数据序号+1，和subList的使用方式保持一致
	 * @return 数据记录列表
	 */
	public ArrayList<T> getRecordList(int start, int end) {
		if (end <= start)
		{
			return new ArrayList<T>(0);
		}
		
		ArrayList<T> result = new ArrayList<T>(end - start);

		int startBucketSeq = start / CAPACITY; // start所在桶的序号
		int startLocal = start % CAPACITY; // start在桶中的位置
		int endBucketSeq = (end - 1) / CAPACITY; // end所在桶的序号
		int endLocal = (end - 1) % CAPACITY; // end在桶中的位置

		if (startBucketSeq == endBucketSeq)
		{
			ArrayList<T> bucket = dataSet.get(startBucketSeq);
			lock.readLock().lock();
			result.addAll(bucket.subList(startLocal, endLocal + 1));
			lock.readLock().unlock();
		}
		else
		{
			// 加第一桶
			ArrayList<T> startBucket = dataSet.get(startBucketSeq);
			lock.readLock().lock();
			result.addAll(startBucket.subList(startLocal, startBucket.size() - 1));
			lock.readLock().unlock();
			// 加中间的桶
			for (int i = startBucketSeq + 1; i < endBucketSeq; i++)
			{
				result.addAll(dataSet.get(i));
			}
			// 加最后的桶
			ArrayList<T> endBucket = dataSet.get(endBucketSeq);
			lock.readLock().lock();
			result.addAll(endBucket.subList(0, endLocal + 1));
			lock.readLock().unlock();
		}

		return result;
	}

	public ArrayList<T> getRecordList() {
		ArrayList<T> result = new ArrayList<T>();
		for (ArrayList<T> list : dataSet)
		{
			lock.readLock().lock();
			result.addAll(list);
			lock.readLock().unlock();
		}
		return result;
	}

	public T getRecord(int seq) {
		if (seq > size)
		{
			return null;
		}
		
		int bucketSeq = seq / CAPACITY; // 所在桶的序号
		int local = seq % CAPACITY; // 在桶中的位置
		return dataSet.get(bucketSeq).get(local);
	}

	public void add(T entry) {
		lock.writeLock().lock();
		int local = size % CAPACITY; // 新加的entry如在桶中位置应该在的位置
		
		if (local == 0)
		{
			// 新桶第一个记录
			curBucket = new ArrayList<T>();
			curBucket.add(entry);
			dataSet.add(curBucket);
		}
		else
		{
			// 可以在当前桶中存放
			curBucket.add(entry);
		}
		
		size++;
		lock.writeLock().unlock();
	}

	// FIXME 加锁可能更安全一些
	public int size() {
		return size;
	}

	public void clear() {
		lock.writeLock().lock();
		for (ArrayList<T> list : dataSet)
		{
			list.clear();
		}
		dataSet.clear();
		curBucket = null;
		size = 0;
		lock.writeLock().unlock();
	}
}

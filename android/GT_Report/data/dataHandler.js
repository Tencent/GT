

////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// 公共函数库：////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

/* 栈定位 */
function getEffectiveCode(stack){

	var effectiveCode = null;
	var lineArray = stack.split('\n');
	for (var i = lineArray.length - 2; i >= 0; i--) {
		if (!lineArray[i].startsWith("com.android.internal.") 
				&& !lineArray[i].startsWith("android.os.")
				&& !lineArray[i].startsWith("android.view.") 
				&& !lineArray[i].startsWith("android.widget.")
				&& !lineArray[i].startsWith("android.support.") 
				&& !lineArray[i].startsWith("android.app.")
				&& !lineArray[i].startsWith("java.io.") 
				&& !lineArray[i].startsWith("libcore.io.")
				&& !lineArray[i].startsWith("java.lang.") 
				&& !lineArray[i].startsWith("java.util.")
				&& !lineArray[i].startsWith("sun.misc.Unsafe.") 
				&& !lineArray[i].startsWith("com.kunpeng.")
				&& !lineArray[i].startsWith("com.matt.") 
				&& !lineArray[i].startsWith("com.tencent.bugly.")
				&& !lineArray[i].startsWith("com.tencent.wstt.gt")
				&& !lineArray[i].startsWith("com.utest.pdm.")) {
			effectiveCode = lineArray[i];
			break;
		}
	}
	if (effectiveCode == null) {
		if (lineArray.length > 0) {
			effectiveCode = lineArray[0];
		} else {
			effectiveCode = "";
		}
	}
	return effectiveCode;
}
function isEffectiveCode(code){
	if(code==null || code ==""){
		return false;
	}
	
	if (!code.startsWith("com.android.internal.") 
			&& !code.startsWith("android.")
			&& !code.startsWith("java.") 
			&& !code.startsWith("org.") 
			&& !code.startsWith("libcore.io.")
			&& !code.startsWith("sun.misc.Unsafe.") 
			&& !code.startsWith("com.android.") 
			&& !code.startsWith("com.google.") 
			&& !code.startsWith("com.kunpeng.")
			&& !code.startsWith("com.matt.") 
			&& !code.startsWith("com.tencent.bugly.")
			&& !code.startsWith("com.tencent.wstt.gt")
			&& !code.startsWith("com.utest.pdm.")) 
	{
		return true;
	}else{
		return false;
	}
}
//定位策略：
//先把每个栈的有效代码找出来，如果发现频率最高的代码的频率>50%,则返回此代码
//否则，找到出现频率大于50%的最深代码（无论是否有效）
function getMostEffectiveCode_1(stackInfoList) {
	//先把每个栈的有效代码找出来，如果发现频率最高的代码的频率>50%,则返回此代码
	var tempCodeArray = new Array();
	var tempCodeNumberArray = new Array();
	for(var i=0;i<stackInfoList.length;i++){
		var effcode = getEffectiveCode(stackInfoList[i].stack);
		if(effcode!=null &&　effcode!=""){
			var isExists = false;
			for(var t=0; t<tempCodeArray.length; t++){
				if(tempCodeArray[t] == effcode){
					tempCodeNumberArray[t] = tempCodeNumberArray[t]+1;
					isExists = true;
				}
			}
			if(!isExists){
				tempCodeArray.push(effcode);
				tempCodeNumberArray.push(1);
			}
		}
	}
	for(var i=0;i<tempCodeNumberArray.length;i++){
		if(tempCodeNumberArray[i]>stackInfoList.length/2){
			return tempCodeArray[i];
		}
	}
	return "";
	
	
}

function getMostEffectiveCode_2(stackInfoList) {
	if(stackInfoList.length==1){
		return getEffectiveCode(stackInfoList[0].stack);
	}
	//分解数据
	var stackArray = new Array();
	var maxStackDeep = 0;
	for(var i=0;i<stackInfoList.length;i++){
		var lineArray = stackInfoList[i].stack.split('\n');
		stackArray.push(lineArray);
		if(lineArray.length>maxStackDeep){
			maxStackDeep = lineArray.length;
		}
	}
	//找到出现频率最多的代码 >50%
	var mostTimeCode = null;
	for(var i=1;i<maxStackDeep;i++){
		//遍历每个栈的此行，将此行加入到temp中
		var tempCodeArray = new Array();
		var tempCodeNumberArray = new Array();
		for(var k=0;k<stackArray.length;k++){
			var code = "";
			if(stackArray[k].length>i){
				code = stackArray[k][stackArray[k].length-1-i];
			}else{
				code = "";
			}
			var isExists = false;
			for(var t=0; t<tempCodeArray.length; t++){
				if(tempCodeArray[t] == code){
					tempCodeNumberArray[t] = tempCodeNumberArray[t]+1;
					isExists = true;
				}
			}
			if(!isExists){
				tempCodeArray.push(code);
				tempCodeNumberArray.push(1);
			}
		}
		var maxLocal = -1;
		var maxCode = "";
		for(var t=0; t<tempCodeNumberArray.length; t++){
			if(tempCodeNumberArray[t]>stackInfoList.length/2 &&　tempCodeArray[t]!=""){
				mostTimeCode = tempCodeArray[t];
				maxLocal = t;
			}
		}
		if(maxLocal == -1){
			break;
		}
	}
	//通过出现频率最多的代码，向上找到最近的有效代码
	for(var i=0;i<stackInfoList.length;i++){
		if(stackInfoList[i].stack.indexOf(mostTimeCode)!=-1){
			var lineArray = stackInfoList[i].stack.split('\n');
			var isFind = false;
			for(var k=0;k<lineArray.length;k++){
				if(lineArray[k].indexOf(mostTimeCode)!=-1){
					isFind = false;
				}
				if(isFind){
					if(isEffectiveCode(lineArray[k])){
						return lineArray[k];
					}
				}
			}
		}
		
	}
	return mostTimeCode;
}

function getMostEffectiveCode(stackInfoList) {
	//遍历每个Code,如果发现有效且出现频率大于50%
	for(var i=0;i<stackInfoList.length;i++){
		var lineArray = stackInfoList[i].stack.split('\n');
		for(var k=0;k<lineArray.length;k++){
			if(isEffectiveCode(lineArray[k])){
				var times = 0;
				for(var g=0;g<stackInfoList.length;g++){
					if(stackInfoList[g].stack.indexOf(lineArray[k])!=-1){
						times++;
					}
				}
				if(times>stackInfoList.length/2){
					return lineArray[k];
				}
			}
		}
	}
	//否则，遍历每个Code,如果发现出现频率大于50%
	for(var i=0;i<stackInfoList.length;i++){
		var lineArray = stackInfoList[i].stack.split('\n');
		for(var k=0;k<lineArray.length;k++){
			if(lineArray[k]!=""){
				var times = 0;
				for(var g=0;g<stackInfoList.length;g++){
					if(stackInfoList[g].stack.indexOf(lineArray[k])!=-1){
						times++;
					}
				}
				if(times>stackInfoList.length/2){
					return lineArray[k];
				}
			}
		}
	}
	
	
}




/* 时间函数 */
function getRealTime(time){//获取真实时间：
	var realTime = "";
	var time = time - appInfo.startTestTime;
	if(time>3600000){
		var hour = parseInt(time/3600000);
		if(hour<10){
			realTime = realTime+"0";
		}
		realTime = realTime+hour+":";
		time = time-hour*3600000;
	}else{
		realTime = realTime+"00:";
	}
	if(time>60000){
		var min = parseInt(time/60000);
		if(min<10){
			realTime = realTime+"0";
		}
		realTime = realTime+min+":";
		time = time-min*60000;
	}else{
		realTime = realTime+"00:";
	}
	if(time>0){
		var sec = time/1000;
		if(sec<10){
				realTime = realTime+"0";
		}
		realTime = realTime+sec;
	}else{
		realTime = realTime+"00";
	}
	
	return realTime;
	
	
	
	
}
function getRealTime_Format2(ms){				
	var tempMS = ms-appInfo.startTestTime;
	var time = "";
	if(tempMS>86400000){
		var day = parseInt(tempMS/86400000);
		time = time+day+"天";
		tempMS = tempMS-day*86400000;
	}
	if(tempMS>3600000){
		var hour = parseInt(tempMS/3600000);
		time = time+hour+"时";
		tempMS = tempMS-hour*3600000;
	}
	if(tempMS>60000){
		var min = parseInt(tempMS/60000);
		time = time+min+"分";
		tempMS = tempMS-min*60000;
	}
	var sec = tempMS/1000;
	time = time+sec+"秒" ;
	return time;
}

/* SM */
function getMinSMInArea(startTime,endTime){//获取区间的最小SM值
	if(startTime>endTime){
		return 60;
	}
	var minSM = 60;
	var moreThanOneSecond =false;
	var frameInOneSecond = new Array();
	for(var i=0;i<frames.length;i++){
		if(frames[i]>startTime-1000 && frames[i]<endTime+1000){
			frameInOneSecond.push(frames[i]);
			while(frameInOneSecond.length>0 && frames[i]-frameInOneSecond[0]>1000){
				frameInOneSecond.splice(0,1);
				moreThanOneSecond= true;
				
			}
		}
		if(frames[i]>startTime && frames[i]<endTime+1000){
			if(moreThanOneSecond && frameInOneSecond.length<minSM){
				minSM = frameInOneSecond.length;
			}
		}
	}
	return minSM;
}
function getSMAtTime(time){//获取指定时间的SM值
	var sm = 0;
	for(var i=0;i<frames.length;i++){
		if(frames[i]>time-500 && frames[i]<=time+500){
			sm++;
		}
	}

	return sm;
}
function getFrameNum(startTime,endTime){
	var num = 0;
	for(var i=0;i<frames.length;i++){
		if(frames[i]>startTime&& frames[i]<endTime){
			num++;
		}
	}
	return num;
}
/* 判断前后台 */
function isFront(time){
	var isF = false;
	var lastChangeTime = 0;
	for(var i=0;i<frontBackStates.length;i++){
		if(frontBackStates[i].time<time &&(lastChangeTime=0||frontBackStates[i].time>lastChangeTime)){
			lastChangeTime = frontBackStates[i].time;
			isF = frontBackStates[i].isFront;
		}
	}
	return isF;
}


/* 页面时间 */
function getPageStartTime(pageLoadInfo){		//页面的开始时间 
	var begin=0;
	if(pageLoadInfo!=null){
		var lifecycleMethodList = pageLoadInfo.lifecycleMethodList;
		for(var d=0; d<lifecycleMethodList.length; d++){
			if(begin==0 || lifecycleMethodList[d].methodStartTime<begin){
				begin = lifecycleMethodList[d].methodStartTime;
			}
		}
	}
	return begin;
}
function getPageStartFinishTime(pageLoadInfo){	//页面的加载完成时间
	var startFinishTime=0;
	if(pageLoadInfo!=null){
		var lifecycleMethodList = pageLoadInfo.lifecycleMethodList;
		for(var d=0; d<lifecycleMethodList.length; d++){
			if(lifecycleMethodList[d].methodName=="onResume" && lifecycleMethodList[d].methodEndTime>=startFinishTime){
				 startFinishTime = lifecycleMethodList[d].methodEndTime;
			}
		}
		var drawInfoList = pageLoadInfo.drawInfoList;
		if(drawInfoList!=null && drawInfoList.length>0){
			if(drawInfoList[0].drawEnd>=startFinishTime){
				startFinishTime = drawInfoList[0].drawEnd;
			}
		}
	}
	return startFinishTime;
}
function getPageEndTime(pageLoadInfo){			//页面的结束时间 
	var end=0;
	if(pageLoadInfo!=null){
		var lifecycleMethodList = pageLoadInfo.lifecycleMethodList;
		for(var d=0; d<lifecycleMethodList.length; d++){
			if(end==0 || lifecycleMethodList[d].methodEndTime+5>end){
				end = lifecycleMethodList[d].methodEndTime+5;
			}
		}
	}
	return end;
}
function isPageLoadCold(pageLoadInfo){			//判断页面启动是否为冷启动
	var lifecycleMethodList = pageLoadInfo.lifecycleMethodList;
	for(var d=0; d<lifecycleMethodList.length; d++){//execStart的时间，如果存在则表明是冷启动
		if(lifecycleMethodList[d].methodName=="onCreate"){
			return true;
		}
	}
	return false;
}
function getPageLoadInfoByFragment(fragmentInfo){ //根据fragmentInfo来获取所属页面ID
	var startTime = getFragmentStartTime(fragmentInfo);
	var startFinishTime = getFragmentStartFinishTime(fragmentInfo);
	for(var d=0; d<pageLoadInfos.length;d++){
		var pageBegin = getPageStartTime(pageLoadInfos[d]);
		var pageEnd = getPageEndTime(pageLoadInfos[d]);
		if(pageBegin<=startTime && startFinishTime<=pageEnd){
				return pageLoadInfos[d];
			}
	}
	return null;
}
function getPageLoadInfoByTime(startTime,endTime){ //根据时间来获取所属页面ID
	for(var d=0; d<pageLoadInfos.length;d++){
		var pageBegin = getPageStartTime(pageLoadInfos[d]);
		var pageEnd = getPageEndTime(pageLoadInfos[d]);
		if(pageBegin<=startTime && endTime<=pageEnd){
				return pageLoadInfos[d];
			}
	}
	return null;
}
function getPageLoadInfoByTimeViewDraw(viewDrawInfo){
	var ss= "";
	for(var d=0; d<pageLoadInfos.length;d++){
		for(var i=0; i<pageLoadInfos[d].drawInfoList.length;i++){
			ss = ss+pageLoadInfos[d].drawInfoList.length +"\n";
			var tempDraw = pageLoadInfos[d].drawInfoList[i];
			if(viewDrawInfo.drawClassName == tempDraw.drawClassName
				&&viewDrawInfo.objectHashCode == tempDraw.objectHashCode
				&&viewDrawInfo.drawBegin == tempDraw.drawBegin
				&&viewDrawInfo.drawEnd == tempDraw.drawEnd
				&&viewDrawInfo.drawPath == tempDraw.drawPath
				){
				return pageLoadInfos[d];
			}
		}
	}
	return null;
}

/* Fragment时间 */
function getFragmentStartTime(fragmentInfo){		//Fragment的开始时间 
	var begin=0;
	if(fragmentInfo!=null){
		var fragmentLifecycleMethodList = fragmentInfo.fragmentLifecycleMethodList;
		for(var d=0; d<fragmentLifecycleMethodList.length; d++){
			if(begin==0 || fragmentLifecycleMethodList[d].methodStartTime<begin){
				begin = fragmentLifecycleMethodList[d].methodStartTime;
			}
		}
	}
	return begin;
}
function getFragmentEndTime(fragmentInfo){			//Fragment的结束时间 
	var end=0;
	if(fragmentInfo!=null){
		var fragmentLifecycleMethodList = fragmentInfo.fragmentLifecycleMethodList;
		for(var d=0; d<fragmentLifecycleMethodList.length; d++){
			if(end==0 || fragmentLifecycleMethodList[d].methodEndTime>end){
				end = fragmentLifecycleMethodList[d].methodEndTime;
			}
		}
	}
	return end;
}
function getFragmentStartFinishTime(fragmentInfo){	//Fragment的加载完成时间
	var startFinishTime=0;
	if(fragmentInfo!=null){
		var fragmentLifecycleMethodList = fragmentInfo.fragmentLifecycleMethodList;
		for(var d=0; d<fragmentLifecycleMethodList.length; d++){
			if(startFinishTime==0 || fragmentLifecycleMethodList[d].methodName=="onResume"){
				 startFinishTime = fragmentLifecycleMethodList[d].methodEndTime;
				 break;
			}
		}
	}
	return startFinishTime;
}
function isFragmentCold(fragmentInfo){				//判断Fragment启动是否为冷启动
	var fragmentLifecycleMethodList = fragmentInfo.fragmentLifecycleMethodList;
	for(var d=0; d<fragmentLifecycleMethodList.length; d++){
		if(fragmentLifecycleMethodList[d].methodName=="onCreateView"){
			return true;
		}
	}
	return false;
}




/* 图-区间数据 */
function getChartDataInArea_xAxis(startTime, endTime){				//图-区间X轴数据															
	var xAxis = new Array();
	for(var i=0; i<endTime-startTime; i++){
		xAxis.push(i+startTime-appInfo.startTestTime);
	}
	return xAxis;
}

function getChartDataInArea_smData(startTime, endTime){
	//图-区间sm数据
	let lastSM = new Array();
	let smData = new Array();
	let lastScanned = -1;
	
	for (let i = 0; i < endTime - startTime; i++) {
		let time = i + startTime;
		if (lastSM.length != 0) {
			if (frames[lastSM[0]] <= time - 500) {
				lastSM.shift();
			}
		}
		
		if (lastSM.length != 0) {
			let high = lastSM[lastSM.length - 1];
			if (high + 1 < frames.length && frames[high + 1] - time <= 500) {
				lastSM.push(high + 1);
				lastScanned = high + 1;
			}
		} else {
			for (let j = lastScanned + 1; j < frames.length && frames[j] - 500 <= time; j++) {
				lastScanned = j;
				if (frames[j] <= time - 500) {
					continue;
				}
				lastSM.push(j);				
			}
		}
		
		smData.push(lastSM.length);
	}
	
	return smData;
}
function getChartDataInArea_blockData(startTime, endTime){			//图-区间block数据
	var blockData = new Array();
	for(var i=0; i<endTime-startTime; i++){
		var blockTime = 0;
		for(var k=0;k<allBlockInfos.length;k++){
			if(allBlockInfos[k].startTime<startTime+i && allBlockInfos[k].endTime>=startTime+i){
				blockTime = allBlockInfos[k].endTime - allBlockInfos[k].startTime;
			}
		}
		blockData.push(blockTime);
	}
	return blockData;
}
function getChartDataInArea_drawData(pageLoadInfo,startTime,endTime){//图-区间绘制数据
	//初始化
	var array = new Array();
	for(var k=0;k< endTime-startTime;k++){
		array.push(0);
	}
	//赋值
	var drawInfoList = pageLoadInfo.drawInfoList;
	for(var i = 0;i<drawInfoList.length;i++){
		for(var x = 0;x<drawInfoList[i].drawEnd-drawInfoList[i].drawBegin;x++){
			array[drawInfoList[i].drawBegin-startTime+x] = drawInfoList[i].drawDeep;
		}
	}
	return array;
}
function getChartDataInArea_Lifecycle(pageLoadInfo){				//图-区间生命周期
	var startTime = getPageStartTime(pageLoadInfo);
	var array = new Array();
	for(var k =0;k<pageLoadInfo.lifecycleMethodList.length;k++){
		var page = new Array();
		var start = pageLoadInfo.lifecycleMethodList[k].methodStartTime-startTime;
		var end = pageLoadInfo.lifecycleMethodList[k].methodEndTime+1-startTime;
		page.push({xAxis: start});
		page.push({xAxis: end});
		array.push(page);
	}
	return array;
}
function getChartDataInArea_FragmentLifecycle(fragmentInfo){
	var startTime = getFragmentStartTime(fragmentInfo);
	var array = new Array();
	for(var k =0;k<fragmentInfo.fragmentLifecycleMethodList.length;k++){
		var page = new Array();
		var start = fragmentInfo.fragmentLifecycleMethodList[k].methodStartTime-startTime;
		var end = fragmentInfo.fragmentLifecycleMethodList[k].methodEndTime+1-startTime;
		page.push({xAxis: start});
		page.push({xAxis: end});
		array.push(page);
	}
	return array;
}
function getChartDataInArea_operation(startTime, endTime){			//图-区间操作区间数据    
	var array = new Array();
	for(var i=0;i<operationInfos.length;i++){
		var page = new Array();
		var start = operationInfos[i].operationBegin;
		var end = operationInfos[i].operationEnd;
		start = start - startTime;
		end = end - startTime;
		page.push({xAxis: start});
		page.push({xAxis: end});
		array.push(page);
	}
	return array;
}
function getChartDataInArea_viewBuild(startTime,endTime){							//函数：View曲线数据    
	var array = new Array();
	for(var k =0;k<viewBuildInfos.length;k++){
		if(viewBuildInfos[k].startTime>=startTime&&viewBuildInfos[k].endTime<=endTime){
			var view = new Array();
			var start = viewBuildInfos[k].startTime-startTime;
			var end = viewBuildInfos[k].endTime-startTime;
			view.push({xAxis: start});
			view.push({xAxis: end});
			array.push(view);
		}
	}
	return array;
}
function getChartDataInArea_gc(startTime,endTime){							//函数：View曲线数据    
	var array = new Array();
	for(var k =0;k<allGCInfos.length;k++){
		var gcInfo = allGCInfos[k];
		var gcStart = gcInfo.gcLogTime-(gcInfo.totalTime/1000).toFixed(0);
		var gcEnd = gcInfo.gcLogTime;
		if(gcStart>=startTime&&gcEnd<=endTime){
			var gcArea = new Array();
			var start = gcStart-startTime;
			var end = gcEnd-startTime;
			gcArea.push({xAxis: start});
			gcArea.push({xAxis: end});
			array.push(gcArea);
		}
	}
	return array;
}



/* 生成栈信息的table的HTML */
function makeStackListTableHTML(stackInfoList){
	
	var effectiveCode = getMostEffectiveCode(stackInfoList);
	var html = '<table class="display" style="word-break:break-all; word-wrap:break-word;" >'
		+'<thead>'
			+'<tr>'
				+'<th>时间</th>'
				+'<th>调用栈</th>'
			+'</tr>'
		+'</thead>'
		+'<tbody>';
		for(var i=0;i<stackInfoList.length;i++){
			html = html 
			+'<tr>'
			+'	<td>';
				html = html +getRealTime(stackInfoList[i].time);
			html = html 
			+'	</td>'
			+'	<td>';
			var lineArray = stackInfoList[i].stack.split('\n');
			for(var k=lineArray.length-1;k>=0;k--){
				if(lineArray[k] == effectiveCode){
					html = html + '<b style="color:red">'+lineArray[k]+"</b><br />"
				}else{
					html = html + lineArray[k]+"<br />"
				}
			}
			html = html 
			+'	</td>'
			+'</tr>';
		}
		html = html
		+'</tbody>'
	+'</table>';
	return html;
}


//" ------------------ <br />"
function getToolTip_Time(time){
	var tip = "时间："+getRealTime_Format2(time+appInfo.startTestTime)+"("+time+")"+" <br />"
	return tip;
}
function getToolTip_SM(time){
	time = time + appInfo.startTestTime;
	var tip = "S M："+getSMAtTime(time)+"帧/s <br />"
	return tip;
}
function getToolTip_CPU(time){
	time = time + appInfo.startTestTime;
	var normalBefore = null;
	var normalAfter = null;
	for(var i=0; i<normalInfos.length;i++){
		if(normalInfos[i].time<time){
			if(normalBefore==null || normalBefore.time<normalInfos[i].time){
				normalBefore = normalInfos[i];
			}
		}
		if(normalInfos[i].time>time){
			if(normalAfter==null || normalAfter.time>normalInfos[i].time){
				normalAfter = normalInfos[i];
			}
		}
	}
	if(normalBefore!=null && normalAfter!=null ){
		var tip = "CPU："+((normalAfter.cpuApp-normalBefore.cpuApp)*100/(normalAfter.cpuTotal-normalBefore.cpuTotal)).toFixed(1)+"% <br />"
		return tip;
	}
	
	return "";
}
function getToolTip_Mem(time){
	time = time + appInfo.startTestTime;
	for(var i=0; i<normalInfos.length;i++){
		if(normalInfos[i].time>time){
			var tip = "内存："+normalInfos[i].memory+"MB <br />"
			return tip;
		}
	}
	return "";
}
function getToolTip_Flow(time){
	time = time + appInfo.startTestTime;
	for(var i=0; i<normalInfos.length;i++){
		if(normalInfos[i].time>time){
			var tip = "流量："+(normalInfos[i].flowUpload+normalInfos[i].flowDownload)+" <br />"
			return tip;
		}
	}
	return "";
}

function getToolTip_Page(time){
	time = time + appInfo.startTestTime;
	var tip ="";
	for(var i=0;i<pageLoadInfos.length;i++){
		var pageLoadInfo = pageLoadInfos[i];
		var start = 0;
		var end =0;
		for(var d=0;d<pageLoadInfo.lifecycleMethodList.length;d++){
			if(pageLoadInfo.lifecycleMethodList[d].methodName== "onResume" || pageLoadInfo.lifecycleMethodList[d].methodName== "onStart" || pageLoadInfo.lifecycleMethodList[d].methodName== "onCreate"){
				if(start==0 || pageLoadInfo.lifecycleMethodList[d].methodStartTime<start){
					start = pageLoadInfo.lifecycleMethodList[d].methodStartTime;
				}
			}
			if(pageLoadInfo.lifecycleMethodList[d].methodName== "onPause" || pageLoadInfo.lifecycleMethodList[d].methodName== "onStop" ){
				if(end==0 || pageLoadInfo.lifecycleMethodList[d].methodEndTime>end){
					end = pageLoadInfo.lifecycleMethodList[d].methodEndTime;
				}
			}
		}
		if(start<=time && end>=time){
			tip = tip + "页面："+pageLoadInfos[i].activityClassName+"@"+pageLoadInfos[i].objectHashCode+"（第"+pageLoadInfos[i].startOrderId+"个页面）" +" <br />";
		}
	}
	if(tip==""){
		return "应用处于后台！ <br />";
	}
	return tip;
}
function getToolTip_Operation(time){
	time = time + appInfo.startTestTime;
	var tip ="";
	for(var i=0;i<operationInfos.length;i++){
		var start = operationInfos[i].operationBegin;
		var end = operationInfos[i].operationEnd;
		if(start<=time && end>=time){
			tip = tip + "操作："+operationInfos[i].operationClassName+" -- '"+operationInfos[i].operationCode+"' <br />";
		}
	}
	return tip;
}
function getToolTip_Block(time){
	time = time + appInfo.startTestTime;
	var tip ="";
	for(var i=0;i<allBlockInfos.length;i++){
		var start = allBlockInfos[i].startTime;
		var end = allBlockInfos[i].endTime;
		if(start<=time && end>=time){
			tip = tip + "卡顿："+getMostEffectiveCode(allBlockInfos[i].stackInfoList)+" -- "+(end-start)+"ms <br />";
		}
	}
	return tip;
}
function getToolTip_Draw(pageLoadInfo,time){
	time = time + appInfo.startTestTime;
	var tip = "";
	var drawInfoList = pageLoadInfo.drawInfoList;
	for(var i = 0;i<drawInfoList.length;i++){
		if(time>=drawInfoList[i].drawBegin && time<=drawInfoList[i].drawEnd){
			tip = tip+"绘制："+drawInfoList[i].drawClassName+"@"+drawInfoList[i].objectHashCode+" -- "+(drawInfoList[i].drawEnd-drawInfoList[i].drawBegin)+"ms <br />";
		}
	}
	return tip;
}
function getToolTip_ViewBuild(time){
	time = time + appInfo.startTestTime;
	var tip = "";
	for(var i=0;i<viewBuildInfos.length;i++){
		if(viewBuildInfos[i].startTime<=time && time<=viewBuildInfos[i].endTime){
			tip = tip+"View构建："+viewBuildInfos[i].viewName +" -- "+(viewBuildInfos[i].endTime-viewBuildInfos[i].startTime)+"ms <br />";
		}
	}
	return tip;
}
function getToolTip_Lifecycle(pageLoadInfo,time){
	time = time + appInfo.startTestTime;
	var tip = "";
	for(var i=0;i<pageLoadInfo.lifecycleMethodList.length;i++){
		if(time>=pageLoadInfo.lifecycleMethodList[i].methodStartTime && time<=pageLoadInfo.lifecycleMethodList[i].methodEndTime+1){
			tip = tip+"函数：Activity."+pageLoadInfo.lifecycleMethodList[i].methodName+" -- "+(pageLoadInfo.lifecycleMethodList[i].methodEndTime -pageLoadInfo.lifecycleMethodList[i].methodStartTime)+"ms <br />";
		}
	}
	return tip;
}
function getToolTip_FragmentLifecycle(fragmentInfo,time){
	time = time + appInfo.startTestTime;
	var tip = "";
	for(var i=0;i<fragmentInfo.fragmentLifecycleMethodList.length;i++){
		if(time>=fragmentInfo.fragmentLifecycleMethodList[i].methodStartTime && time<=fragmentInfo.fragmentLifecycleMethodList[i].methodEndTime+1){
			tip = tip+"函数：Fragment."+fragmentInfo.fragmentLifecycleMethodList[i].methodName+" -- "+(fragmentInfo.fragmentLifecycleMethodList[i].methodEndTime -fragmentInfo.fragmentLifecycleMethodList[i].methodStartTime)+"ms<br />";
		}
	}
	return tip;
}
function getToolTip_Fragment(time){
	time = time + appInfo.startTestTime;
	var tip = "";
	for(var i=0;i<fragmentInfos.length;i++){
		var fragmentVisibleList = fragmentInfos[i].fragmentVisibleList;
		for(var d=0; d<fragmentVisibleList.length; d++){
			if(time>=fragmentVisibleList[d].begin && time<=fragmentVisibleList[d].end){
				tip = tip +"Fragment："+fragmentInfos[i].fragmentClassName+"@"+fragmentInfos[i].fragmentHashCode+"（第"+fragmentInfos[i].startOrderId+"个Fragment）"+" <br />"
			}
		}
	}
	return tip;
}
function getToolTip_gc(time){
	time = time + appInfo.startTestTime;
	var tip = "";
	for(var i=0;i<allGCInfos.length;i++){
		var gcInfo = allGCInfos[i];
		var gcStart = gcInfo.gcLogTime-(gcInfo.totalTime/1000).toFixed(0);
		var gcEnd = gcInfo.gcLogTime;
		if(gcStart<=time && gcEnd>=time){
			
			var gcLog =gcInfo.gcLog;
			var line1 = gcLog.substring(0,									Math.floor(gcLog.length/2)+3);
			var line2 = gcLog.substring(Math.floor(gcLog.length/2)+3,		gcLog.length);
			tip = tip + "G C：" + line1 +"<br />";
			tip = tip + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + line2 +"<br />";
		}
	}
	return tip;
}










////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// 基础性能页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

function getTableData_base(){
	var tableData_base = new Object();
	//前台CPU、内存、流量:
	if(tableBaseData_base.frontCpuArray.length==0 && tableBaseData_base.frontMemoryArray.length==0){
		tableData_base.front_cpu_max = "暂无数据";
		tableData_base.front_cpu_average = "暂无数据";
		tableData_base.front_memory_max = "暂无数据";
		tableData_base.front_memory_average = "暂无数据";
		tableData_base.front_flow_up = "暂无数据";
		tableData_base.front_flow_down = "暂无数据";
	}else{
		tableData_base.front_cpu_max = 0;
		for(var i=0; i<tableBaseData_base.frontCpuArray.length;i++){
			if(tableBaseData_base.frontCpuArray[i]>tableData_base.front_cpu_max){
				tableData_base.front_cpu_max = tableBaseData_base.frontCpuArray[i].toFixed(1);
			}
		}
		tableData_base.front_cpu_max = tableData_base.front_cpu_max +"%";
		tableData_base.front_cpu_average = (tableBaseData_base.frontCpuApp*100/tableBaseData_base.frontCpuTotal).toFixed(1);
		tableData_base.front_cpu_average = tableData_base.front_cpu_average +"%";

		var frontMemorySum = 0;
		tableData_base.front_memory_max = 0;
		for(var i=0; i<tableBaseData_base.frontMemoryArray.length;i++){
			frontMemorySum = frontMemorySum+tableBaseData_base.frontMemoryArray[i];
			if(tableBaseData_base.frontMemoryArray[i]>tableData_base.front_memory_max){
				tableData_base.front_memory_max = tableBaseData_base.frontMemoryArray[i];
			}
		}
		tableData_base.front_memory_max = tableData_base.front_memory_max +"MB";
		tableData_base.front_memory_average = (frontMemorySum/tableBaseData_base.frontMemoryArray.length).toFixed(1);
		tableData_base.front_memory_average = tableData_base.front_memory_average + "MB";
		tableData_base.front_flow_up = tableBaseData_base.frontFlowUpload.toFixed(1)+"KB";
		tableData_base.front_flow_down = tableBaseData_base.frontFlowDownload.toFixed(1)+"KB";

	}
	
	//后台CPU、内存、流量:
	if(tableBaseData_base.backCpuArray.length==0 && tableBaseData_base.backMemoryArray.length==0){
		tableData_base.back_cpu_max = "暂无数据";
		tableData_base.back_cpu_average = "暂无数据";
		tableData_base.back_memory_max = "暂无数据";
		tableData_base.back_memory_average = "暂无数据";
				tableData_base.back_flow_up = "暂无数据";
		tableData_base.back_flow_down = "暂无数据";
	}else{
		tableData_base.back_cpu_max = 0;
		for(var i=0; i<tableBaseData_base.backCpuArray.length;i++){
			if(tableBaseData_base.backCpuArray[i]>tableData_base.back_cpu_max){
				tableData_base.back_cpu_max = tableBaseData_base.backCpuArray[i];
			}
		}
		tableData_base.back_cpu_max = tableData_base.back_cpu_max +"%";
		tableData_base.back_cpu_average = (tableBaseData_base.backCpuApp*100/tableBaseData_base.backCpuTotal).toFixed(1);
		tableData_base.back_cpu_average = tableData_base.back_cpu_average +"%";
		var backMemorySum = 0;
		tableData_base.back_memory_max = 0;
		for(var i=0; i<tableBaseData_base.backMemoryArray.length;i++){
			backMemorySum = backMemorySum+tableBaseData_base.backMemoryArray[i];
			if(tableBaseData_base.backMemoryArray[i]>tableData_base.back_memory_max){
				tableData_base.back_memory_max = tableBaseData_base.backMemoryArray[i];
			}
		}
		tableData_base.back_memory_max = tableData_base.back_memory_max +"MB";
		tableData_base.back_memory_average = (backMemorySum/tableBaseData_base.backMemoryArray.length).toFixed(1);
		tableData_base.back_memory_average = tableData_base.back_memory_average +"MB";
		tableData_base.back_flow_up = tableBaseData_base.backFlowUpload.toFixed(1)+"KB";
		tableData_base.back_flow_down = tableBaseData_base.backFlowDownload.toFixed(1)+"KB";
	}
	//前台SM：
	//遍历所有前台区间，获取流畅值，获取最高最低流畅值
	tableData_base.front_sm_totalTime = 0;
	tableData_base.front_sm_num = 0;
	tableData_base.front_sm_average = 0;
	tableData_base.front_sm_min = 60;
	
	var frontStartTime = frontBackStates[0].time;
	for(var i=0;i<frontBackStates.length;i++){
		if(frontBackStates[i].isFront == false){
			tableData_base.front_sm_totalTime = tableData_base.front_sm_totalTime +frontBackStates[i].time-frontStartTime;
			var frameNum = getFrameNum(frontStartTime,frontBackStates[i].time);
			tableData_base.front_sm_num = tableData_base.front_sm_num +frameNum;
			var minSM = getMinSMInArea(frontStartTime,frontBackStates[i].time);
			if(minSM<tableData_base.front_sm_min){
				tableData_base.front_sm_min = minSM;
			}
		}else{
			frontStartTime = frontBackStates[i].time;
		}
	}
	if(frontBackStates[frontBackStates.length-1].isFront == true){
		tableData_base.front_sm_totalTime = tableData_base.front_sm_totalTime +frames[frames.length-1]-frontStartTime;
		var frameNum = getFrameNum(frontStartTime,frames[frames.length-1]);
		tableData_base.front_sm_num = tableData_base.front_sm_num +frameNum;
		var minSM = getMinSMInArea(frontStartTime,frames[frames.length-1]);
		if(minSM<tableData_base.front_sm_min){
			tableData_base.front_sm_min = minSM;
		}
	}
	tableData_base.front_sm_average = (tableData_base.front_sm_num*1000/tableData_base.front_sm_totalTime).toFixed(1)+"帧/s";
	tableData_base.front_sm_min = tableData_base.front_sm_min.toFixed(1)+"帧/s";
	
	
	
	return tableData_base;
}
//基础性能模块：
function getTableData_baseChartData(){	
	//封装每条曲线的数据
	var xAxis = new Array();
	var cpuData = new Array();
	var memoryData = new Array();
	var flowData = new Array();
	var smData = new Array();
	
	var tempLastCpuApp = -1;
	var tempLastCpuTotal = -1;
	for(var i=0;i<normalInfos.length;i++){
		if(tempLastCpuApp!=-1){
			var cpu = (normalInfos[i].cpuApp-tempLastCpuApp)*100/(normalInfos[i].cpuTotal-tempLastCpuTotal).toFixed(1);
			var memory = normalInfos[i].memory;
			var flow = (normalInfos[i].flowUpload+normalInfos[i].flowDownload)/1024;
		
			cpuData.push(cpu);
			memoryData.push(memory);
			flowData.push(flow);
			
			//var sm = getSMAtTime(normalInfos[i].time);
			//smData.push(sm);
			
			xAxis.push(normalInfos[i].time-appInfo.startTestTime);
		}
		tempLastCpuApp = normalInfos[i].cpuApp;
		tempLastCpuTotal = normalInfos[i].cpuTotal;
	}
	
	// oscar -------------------------------------------------------------
	if (normalInfos != undefined && normalInfos.length > 0) {
		let allSM = getChartDataInArea_smData(normalInfos[0].time, normalInfos[normalInfos.length - 1].time);
		if (allSM.length > 0) {
			let smStartTime = normalInfos[0].time;
			for (let i = 1; i < normalInfos.length; i++) {
				smData.push(allSM[normalInfos[i].time - smStartTime]);
			}
		}
	}
	// --------------------------------------------

	var data = new Object();
	data.xAxis = xAxis;
	data.cpuData = cpuData;
	data.memoryData = memoryData;
	data.flowData = flowData;
	data.smData = smData;
	//数据分析（平均值、最高值、最低值）
	var cpuSum = 0;
	var cpuMax = 0;
	var cpuMin = 100;
	for(var i=0; i<data.cpuData.length;i++){
		cpuSum = cpuSum + data.cpuData[i];
		if(data.cpuData[i]>cpuMax){
			cpuMax = data.cpuData[i];
		}
		if(data.cpuData[i]<cpuMin){
			cpuMin = data.cpuData[i];
		}
	}
	data.cpuAverage = (cpuSum/data.cpuData.length).toFixed(0);
	data.cpuMax = (cpuMax).toFixed(0);
	data.cpuMin = (cpuMin).toFixed(0);
	
	var memorySum = 0;
	var memoryMax = 0;
	var memoryMin = 100;
	for(var i=0; i<data.memoryData.length;i++){
		memorySum = memorySum + data.memoryData[i];
		if(data.memoryData[i]>memoryMax){
			memoryMax = data.memoryData[i];
		}
		if(data.memoryData[i]<memoryMin){
			memoryMin = data.memoryData[i];
		}
	}
	data.memoryAverage = (memorySum/data.memoryData.length).toFixed(0);
	data.memoryMax = (memoryMax).toFixed(0);
	data.memoryMin = (memoryMin).toFixed(0);
	

	var flowTime = normalInfos[normalInfos.length-1].time - normalInfos[0].time;
	var flowSum = data.flowData[data.flowData.length-1] - data.flowData[0];
	data.flowSum = (flowSum).toFixed(0);
	data.flowTime = (flowTime/1000).toFixed(0);
	
	var smSum = 0;
	var smMax = 0;
	var smMin = 100;
	for(var i=0; i<data.smData.length;i++){
		smSum = smSum + data.smData[i];
		if(data.smData[i]>smMax){
			smMax = data.smData[i];
		}
		if(data.smData[i]<smMin){
			smMin = data.smData[i];
		}
	}
	data.smAverage = (smSum/data.smData.length).toFixed(0);
	data.smMax = (smMax).toFixed(0);
	data.smMin = (smMin).toFixed(0);
	
	PDMTemp_BaseData = data;
	return data;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// 卡顿检测页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////


/* 获取低流畅值表格的数据：*/
function getTableData_lowSM(){
	//根据 tableBaseData_lowSM 计算视图数据：
	var tableData_lowSM = new Array();
	for(var i = 0; i <tableBaseData_lowSM.length; i++){
		var thisData = new Object();
		var pageLoadInfo = getPageLoadInfoByTime(tableBaseData_lowSM[i].startTime,tableBaseData_lowSM[i].startTime+1);
		if(pageLoadInfo==null){
			pageLoadInfo = getPageLoadInfoByTime(tableBaseData_lowSM[i].endTime-1,tableBaseData_lowSM[i].endTime);
		}
		thisData.dataID = tableData_lowSM.length;
		thisData.startTime = tableBaseData_lowSM[i].startTime;
		thisData.endTime = tableBaseData_lowSM[i].endTime;
		thisData.realStartTime = getRealTime(tableBaseData_lowSM[i].startTime);
		thisData.totalTime = tableBaseData_lowSM[i].endTime - tableBaseData_lowSM[i].startTime;
		thisData.minSM = getMinSMInArea(tableBaseData_lowSM[i].startTime,tableBaseData_lowSM[i].endTime);
		thisData.pageName = pageLoadInfo==null?"":pageLoadInfo.activityClassName;
		tableData_lowSM.push(thisData);
	}
	
	return tableData_lowSM;
}
function getLowSMTableDetail_tableData(startTime,endTime){
	
	var lowSMTableDetail_tableData = new Array();
	for(var k=0;k<allBlockInfos.length;k++){
		if(allBlockInfos[k].startTime>endTime || allBlockInfos[k].endTime<startTime){
			continue;
		}else{
			var newData = new Object();
			newData.startTime = allBlockInfos[k].startTime;
			newData.endTime = allBlockInfos[k].endTime;
			newData.totalTime = allBlockInfos[k].endTime-allBlockInfos[k].startTime;
			newData.realStartTime = allBlockInfos[k].startTime-appInfo.startTestTime;
			newData.minSM = getMinSMInArea(allBlockInfos[k].startTime,allBlockInfos[k].endTime);
			newData.code = getMostEffectiveCode(allBlockInfos[k].stackInfoList);
			newData.stackInfoList = allBlockInfos[k].stackInfoList;
			lowSMTableDetail_tableData.push(newData);
		}
	}
	return lowSMTableDetail_tableData;
}

/* 获取大卡顿表格的数据：*/
function getTableData_bigBlock(){
	var tableData_bigBlock = new Array();
	for(var i=0;i<tableBaseData_bigBlock.length;i++){
		var thisData = new Object();
		var blockInfo = allBlockInfos[tableBaseData_bigBlock[i]];
		var newData = new Object();
		newData.startTime = blockInfo.startTime;
		newData.endTime = blockInfo.endTime;
		newData.totalTime = blockInfo.endTime-blockInfo.startTime;
		newData.realStartTime = getRealTime(blockInfo.startTime);
		newData.minSM = getMinSMInArea(blockInfo.startTime,blockInfo.endTime);
		newData.code = getMostEffectiveCode(blockInfo.stackInfoList);
		newData.stackInfoList = blockInfo.stackInfoList;
		tableData_bigBlock.push(newData);
	}
	return tableData_bigBlock;
}

//卡顿详细图数据：
function getChartDataInArea_forBlock(startTime,endTime){
	
	var chartDataInArea_forBlock = new Object();
	chartDataInArea_forBlock.xAxis = getChartDataInArea_xAxis(startTime,endTime);
	chartDataInArea_forBlock.smData = getChartDataInArea_smData(startTime,endTime);
	chartDataInArea_forBlock.blockData = getChartDataInArea_blockData(startTime,endTime);
	chartDataInArea_forBlock.operationData = getChartDataInArea_operation(startTime, endTime);
	chartDataInArea_forBlock.viewBuildData = getChartDataInArea_viewBuild(startTime, endTime);
	
	return chartDataInArea_forBlock;
	
	
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// 页面测速页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
function getTableData_overActivity(){
	var tableData_overActivity = new Array();
	for(var i = 0; i <tableBaseData_overActivity.length; i++){
		var pageInfo = pageLoadInfos[tableBaseData_overActivity[i]];
		var thisData = new Object();
		thisData.dataID = tableData_overActivity.length;
		thisData.pageLoadInfo = pageInfo;
		thisData.activityClassName = pageInfo.activityClassName;
		thisData.loadTime = getPageStartFinishTime(pageInfo)-getPageStartTime(pageInfo);
		thisData.loadMinSM = getMinSMInArea(getPageStartTime(pageInfo),getPageStartFinishTime(pageInfo));
		thisData.pageMinSM = getMinSMInArea(getPageStartFinishTime(pageInfo)+1000,getPageEndTime(pageInfo)-1000);
		tableData_overActivity.push(thisData);
	}
	
	return tableData_overActivity;
}
function getTableData_allPage(){
	var tableData_allPage = new Array();
	for(var i = 0; i <tableBaseData_allPage.length; i++){
		var pageInfo = tableBaseData_allPage[i];
		var thisData = new Object();
		thisData.dataID = tableData_allPage.length+1;
		thisData.pageLoadInfo = pageInfo;
		thisData.activityClassName = pageInfo.activityClassName;
		thisData.loadTime = getPageStartFinishTime(pageInfo)-getPageStartTime(pageInfo);
		thisData.coldHot = isPageLoadCold(pageInfo)?"冷":"热";
		thisData.loadMinSM = getMinSMInArea(getPageStartTime(pageInfo),getPageStartFinishTime(pageInfo));
		thisData.pageMinSM = getMinSMInArea(getPageStartFinishTime(pageInfo)+1000,getPageEndTime(pageInfo)-1000);
		tableData_allPage.push(thisData);
	}
	
	return tableData_allPage;
}

//页面详细图数据：
function getChartDataInArea_forPage(pageLoadInfo){
	
	var startTime = getPageStartTime(pageLoadInfo);
	var endTime = getPageEndTime(pageLoadInfo);
	
	var chartDataInArea_forPage = new Object();
	chartDataInArea_forPage.xAxis = getChartDataInArea_xAxis(startTime,endTime);
	chartDataInArea_forPage.smData = getChartDataInArea_smData(startTime,endTime);
	chartDataInArea_forPage.blockData = getChartDataInArea_blockData(startTime,endTime);
	chartDataInArea_forPage.drawData = getChartDataInArea_drawData(pageLoadInfo,startTime,endTime);
	chartDataInArea_forPage.lifecycle = getChartDataInArea_Lifecycle(pageLoadInfo);
	chartDataInArea_forPage.operationMarkArea = getChartDataInArea_operation(startTime,endTime);
	chartDataInArea_forPage.viewBuild = getChartDataInArea_viewBuild(startTime,endTime);
	
	
	return chartDataInArea_forPage;
	
	
}





////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// Fragment测速页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

function getTableData_overFragment(){
	var tableData_overFragment = new Array();
	for(var i = 0; i <tableBaseData_overFragment.length; i++){
		var fragmentInfo = fragmentInfos[tableBaseData_overFragment[i]];
		var thisData = new Object();
		thisData.dataID = tableData_overFragment.length;
		thisData.fragmentInfo = fragmentInfo;
		thisData.pageLoadInfo = getPageLoadInfoByFragment(fragmentInfo);
		thisData.fragmentClassName = fragmentInfo.fragmentClassName;
		thisData.loadTime = getFragmentStartFinishTime(fragmentInfo)-getFragmentStartTime(fragmentInfo);
		thisData.loadMinSM = getMinSMInArea(getFragmentStartTime(fragmentInfo),getFragmentStartFinishTime(fragmentInfo));
		thisData.fragMinSM = getMinSMInArea(getFragmentStartFinishTime(fragmentInfo)+1000,getFragmentEndTime(fragmentInfo)-1000);
		tableData_overFragment.push(thisData);
	}
	
	return tableData_overFragment;
}
function getTableData_allFragment(){
	var tableData_allFragment = new Array();
	for(var i = 0; i <tableBaseData_allFragment.length; i++){
		var fragmentInfo = tableBaseData_allFragment[i];
		var thisData = new Object();
		thisData.dataID = tableData_allFragment.length;
		thisData.fragmentInfo = fragmentInfo;
		thisData.pageLoadInfo =  getPageLoadInfoByFragment(fragmentInfo);
		thisData.fragmentClassName = fragmentInfo.fragmentClassName;
		thisData.coldHot = isFragmentCold(fragmentInfo)?"冷":"热";
		thisData.loadTime = getFragmentStartFinishTime(fragmentInfo)-getFragmentStartTime(fragmentInfo);
		thisData.loadMinSM = getMinSMInArea(getFragmentStartTime(fragmentInfo),getFragmentStartFinishTime(fragmentInfo));
		thisData.fragMinSM = getMinSMInArea(getFragmentStartFinishTime(fragmentInfo)+1000,getFragmentEndTime(fragmentInfo)-1000);
		tableData_allFragment.push(thisData);
	}
	
	return tableData_allFragment;
}


//页面详细图数据：
function getChartDataInArea_forFragment(fragmentInfo,pageLoadInfo){
	
	var startTime = getFragmentStartTime(fragmentInfo);
	var endTime = getFragmentEndTime(fragmentInfo);
	var chartDataInArea_forPage = new Object();
	chartDataInArea_forPage.xAxis = getChartDataInArea_xAxis(startTime,endTime);
	chartDataInArea_forPage.smData = getChartDataInArea_smData(startTime,endTime);
	chartDataInArea_forPage.blockData = getChartDataInArea_blockData(startTime,endTime);
	//chartDataInArea_forPage.drawData = getChartDataInArea_drawData(pageLoadInfo,startTime,endTime);
	chartDataInArea_forPage.lifecycle = getChartDataInArea_FragmentLifecycle(fragmentInfo);
	chartDataInArea_forPage.operationMarkArea = getChartDataInArea_operation(startTime,endTime);
	chartDataInArea_forPage.viewBuild = getChartDataInArea_viewBuild(startTime,endTime);
	
	
	return chartDataInArea_forPage;
	
	
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// 布局检测页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////


function getTableData_overViewBuild(){
	
	var tableData_overViewBuild = new Array();
	for(var i = 0; i <tableBaseData_overViewBuild.length; i++){
		var viewBuildInfo = viewBuildInfos[tableBaseData_overViewBuild[i]];
		var pageLoadInfo = getPageLoadInfoByTime(viewBuildInfo.startTime,viewBuildInfo.endTime);
		var thisData = new Object();
		thisData.dataID = tableData_overViewBuild.length;
		thisData.viewBuildInfo = viewBuildInfo;
		thisData.viewName = viewBuildInfo.viewName;
		thisData.buildTime = viewBuildInfo.endTime-viewBuildInfo.startTime;
		thisData.page = pageLoadInfo==null?"前台无页面":pageLoadInfo.activityClassName;
		thisData.pageLoadInfo = pageLoadInfo;
		tableData_overViewBuild.push(thisData);
	}
	
	return tableData_overViewBuild;
	
}

function getTableData_overViewDraw(){
	var tableData_overViewDraw = new Array();
	for(var i = 0; i <tableBaseData_overViewDraw.length; i++){
		var viewDrawInfo = tableBaseData_overViewDraw[i];
		var pageLoadInfo = getPageLoadInfoByTimeViewDraw(viewDrawInfo);
		var thisData = new Object();
		thisData.dataID = tableData_overViewDraw.length;
		thisData.viewDrawInfo = viewDrawInfo;
		thisData.drawClassName = viewDrawInfo.drawClassName;
		thisData.realStartTime = getRealTime(viewDrawInfo.drawBegin);
		thisData.drawTime = viewDrawInfo.drawEnd-viewDrawInfo.drawBegin;
		thisData.drawDeep = viewDrawInfo.drawDeep;
		thisData.page = pageLoadInfo==null?"":pageLoadInfo.activityClassName;
		thisData.pageLoadInfo = pageLoadInfo;
		tableData_overViewDraw.push(thisData);
	}
	
	return tableData_overViewDraw;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// G C检测页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////	


//GC详细图数据：
function getChartDataInArea_forGC(gcInfo){
	
	var startTime = gcInfo.gcLogTime-(gcInfo.totalTime/1000).toFixed(0)-1000;
	var endTime = gcInfo.gcLogTime+1000;
	var chartDataInArea_forGC = new Object();
	chartDataInArea_forGC.xAxis = getChartDataInArea_xAxis(startTime,endTime);
	chartDataInArea_forGC.smData = getChartDataInArea_smData(startTime,endTime);
	chartDataInArea_forGC.blockData = getChartDataInArea_blockData(startTime,endTime);
	chartDataInArea_forGC.operationMarkArea = getChartDataInArea_operation(startTime,endTime);
	chartDataInArea_forGC.gcMarkArea = getChartDataInArea_gc(startTime,endTime);
	
	return chartDataInArea_forGC;
	
	
}


function getTableData_explicitGC(){
	var tableData_explicitGC = new Array();
	for(var i = 0; i <tableBaseData_explicitGC.length; i++){
		var gcInfo = allGCInfos[tableBaseData_explicitGC[i]];
		var pageLoadInfo = getPageLoadInfoByTime(gcInfo.gcLogTime-(gcInfo.totalTime/1000).toFixed(0),gcInfo.gcLogTime-(gcInfo.totalTime/1000).toFixed(0)+1);
		var thisData = new Object();
		thisData.dataID = tableData_explicitGC.length;
		thisData.gcInfo = gcInfo;
		thisData.startTime = gcInfo.gcLogTime-(gcInfo.totalTime/1000).toFixed(0);
		thisData.realStartTime = getRealTime(gcInfo.gcLogTime-(gcInfo.totalTime/1000).toFixed(0));
		thisData.page = pageLoadInfo==null?"前台无页面":pageLoadInfo.activityClassName;
		thisData.pauseTime = (gcInfo.pauseTime/1000).toFixed(0);
		thisData.totalTime = (gcInfo.totalTime/1000).toFixed(0);
		thisData.heapStatistics = gcInfo.heapStatistics;
		tableData_explicitGC.push(thisData);
	}
	return tableData_explicitGC;
}


//TODO GC暂停的时间超过5ms或者GC执行的总时间超过100ms




////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// I O检测页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////	


function getTableData_fileActionInMainThread(){
	var tableData_fileActionInMainThread = new Array();
	for(var i = 0; i <tableBaseData_fileActionInMainThread.length; i++){
		var fileAction = fileActionInfos[tableBaseData_fileActionInMainThread[i]];
		var thisData = new Object();
		thisData.dataID = tableData_fileActionInMainThread.length;
		thisData.fileAction = fileAction;
		thisData.startTime = fileAction.startTime;
		thisData.realStartTime = getRealTime(fileAction.startTime);
		thisData.filePath = fileAction.filePath;
		thisData.totalTime = fileAction.endTime-fileAction.startTime;
		thisData.readNum = fileAction.readNum;
		thisData.readSize = fileAction.readSize;
		thisData.writeNum = fileAction.writeNum;
		thisData.writeSize = fileAction.writeSize;
		thisData.isMutilThread = fileAction.isMutilThread;
		tableData_fileActionInMainThread.push(thisData);
	}
	return tableData_fileActionInMainThread;
}


function getTableData_dbActionInMainThread(){
	var tableData_dbActionInMainThread = new Array();
	for(var i = 0; i <tableBaseData_dbActionInMainThread.length; i++){
		var dbInfo = dbActionInfos[tableBaseData_dbActionInMainThread[i]];
		var thisData = new Object();
		thisData.dataID = tableData_dbActionInMainThread.length;
		thisData.dbInfo = dbInfo;
		thisData.startTime = dbInfo.startTime;
		thisData.realStartTime = getRealTime(dbInfo.startTime);
		thisData.dbName = dbInfo.dbName;
		thisData.actionName = dbInfo.actionName;
		thisData.totalTime = dbInfo.endTime-dbInfo.startTime;
		thisData.sql = dbInfo.sql;
		thisData.threadId = dbInfo.threadId;
		
		thisData.readNum = 0;
		thisData.readSize = 0;
		thisData.writeNum = 0;
		thisData.writeSize = 0;
		
		tableData_dbActionInMainThread.push(thisData);
	}
	return tableData_dbActionInMainThread;
}


function getTableData_db(){
	var tableData_db = new Array();
	for(var i = 0; i <tableBaseData_db.length; i++){
		var dbInfo = tableBaseData_db[i];
		var thisData = new Object();
		thisData.dataID = tableData_db.length;
		thisData.dbInfo = dbInfo;
		thisData.startTime = dbInfo.startTime;
		thisData.realStartTime = getRealTime(dbInfo.startTime);
		thisData.dbName = dbInfo.dbName;
		thisData.actionName = dbInfo.actionName;
		thisData.totalTime = dbInfo.endTime-dbInfo.startTime;
		thisData.sql = dbInfo.sql;
		thisData.threadId = dbInfo.threadId;
		
		thisData.readNum = 0;
		thisData.readSize = 0;
		thisData.writeNum = 0;
		thisData.writeSize = 0;
		
		tableData_db.push(thisData);
	}
	return tableData_db;
}




////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// 线程分析页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

function getTableData_thread(){
	
	//拿到所有线程信息
	var threads = new Array();
	var threadCpus = normalInfos[normalInfos.length-1].threadCpus;
	for(k=0; k<threadCpus.length; k++){
		var isGTRThread = false;
		for(var gt=0; gt<gtrThreadInfos.length; gt++){
			if(threadCpus[k].threadId == gtrThreadInfos[gt]){
				isGTRThread = true;
			}
		}
		if(!isGTRThread){
			var thread = new Object();
			thread.threadId= threadCpus[k].threadId;
			thread.threadName= threadCpus[k].threadName;
			thread.threadCpu= threadCpus[k].threadCpu;
			threads.push(thread);
		}
	}

	return threads;
	
}
function getChartData_thread(threadId){
	
	//拿到所有线程信息
	var xAxis = new Array();
	var threadCpuData = new Array();
	var lastCpuVal = 0;
	var lastTime = 0;
	for(var i=0; i<normalInfos.length;i++){
		var cpuVal = 0;
		if(lastTime!=0){
			//xAxis:
			xAxis.push(normalInfos[i].time-appInfo.startTestTime);
			//threadCpuData:
			for(k=0; k<normalInfos[i].threadCpus.length; k++){
				if(normalInfos[i].threadCpus[k].threadId == threadId){
					cpuVal = normalInfos[i].threadCpus[k].threadCpu;
				}
			}
			threadCpuData.push(((cpuVal-lastCpuVal)/(normalInfos[i].time-lastTime)*1000).toFixed(1));
		}
		lastTime = normalInfos[i].time;
		lastCpuVal = cpuVal;
	}
	//封装所有数据
	var data = new Object();
	data.xAxis = xAxis;
	data.threadCpuData = threadCpuData;
	
	return data;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// 关键日志页-表格数据：//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

function getTableData_logcat(gradeFilter,tagFilter,isIncludeGTR){
	var tableData_logcat = new Array();
	for(var i=0; i<logInfos.length; i++){
		if(isGrageRight(logInfos[i].grade,gradeFilter) 
			&& logInfos[i].tag.indexOf(tagFilter)!=-1
			&& !(logInfos[i].isGTR &&!isIncludeGTR)
			){
			logInfos[i].realTime = getRealTime(logInfos[i].time);
			tableData_logcat.push(logInfos[i]);
		}
	}
	return tableData_logcat;
}
function isGrageRight(logGrade,gradeFilter){
	if(logGrade == "A"){
		return true;
	}
	if(gradeFilter == "Assert"){
		return false;
	}
	if(logGrade == "E"){
		return true;
	}
	if(gradeFilter == "Error"){
		return false;
	}
	if(logGrade == "W"){
		return true;
	}
	if(gradeFilter == "Warn"){
		return false;
	}
	if(logGrade == "I"){
		return true;
	}
	if(gradeFilter == "Info"){
		return false;
	}
	if(logGrade == "D"){
		return true;
	}
	if(gradeFilter == "Debug"){
		return false;
	}
	if(logGrade == "V"){
		return true;
	}
	if(gradeFilter == "Verbose"){
		return false;
	}
}













// -------------------------------------------------------------------------------------
// -----------------------------<初始化相关>--------------------------------------------
// -------------------------------------------------------------------------------------

var isChildTableDetail = false;	//此字段解决Datatable两层Detail重复绑定监听器的BUG，在子table的detail中设置此字段为true	

//初始化页面
$(document).ready(function(){
	//初始化标题
	$('#page_title').html(appInfo.appName+"("+appInfo.versionName+")"+"-深度性能分析");
	//设置导航条点击事件：
	$('#page_nav li').unbind('click').click(function (){
		openPage($(this).index());
	});
	//打开总结页
	openPage(0);
}); 

//打开一个页面
function openPage(index){
	removeAllPage();													//移除所有页面
	setNowNev(index);													//设置导航栏
	//总结页
	if(index==0){
		document.getElementById("page_summary").style.display="block";	//显示Html
		showTestStateSummaryTable();											//加载测试状态表
		showTestResultSummaryTable();												//加载汇总表
	}
	//基础性能页
	else if(index==1){
		document.getElementById("page_base").style.display="block";	//显示Html
		show_baseTable();
		show_baseChart();
		
	}
	//卡顿检测页
	else if(index==2){
		document.getElementById("page_block").style.display="block";	//显示Html
		show_blockLowSMTable();											//加载卡顿表
		show_blockBigTable();
	}
	//页面测速页
	else if(index==3){
		document.getElementById("page_pageLoad").style.display="block";	//显示Html
		show_pageLoadOverTable();
		show_pageLoadAllTable();
	}
	//Fragment测速页
	else if(index==4){
		document.getElementById("page_fragment").style.display="block";	//显示Html
		show_fragmentOverTable();
		show_fragmentAllTable();
		
	}
	//布局检测页
	else if(index==5){
		document.getElementById("page_view").style.display="block";	//显示Html
		show_overViewBuildTable();
		show_overViewDrawTable();
		
	}
	//G C检测页
	else if(index==6){
		document.getElementById("page_memory").style.display="block";	//显示Html
		show_explicitGCTable();
	}
	// I O检测页
	else if(index==7){
		document.getElementById("page_io").style.display="block";	//显示Html
		//show_fileMainThread();
		show_dbMainThread();
	}
	//线程分析页
	else if(index==8){
		document.getElementById("page_thread").style.display="block";		//显示Html
		show_threadTable();
	}
	//关键日志页
	else if(index==9){
		document.getElementById("page_log").style.display="block";		//显示Html
		show_logcatTable();
	}
}
//移除所有的页面
function removeAllPage(){
	document.getElementById("page_summary").style.display="none";
	document.getElementById("page_base").style.display="none";
	document.getElementById("page_block").style.display="none";
	document.getElementById("page_pageLoad").style.display="none";
	document.getElementById("page_fragment").style.display="none";
	document.getElementById("page_view").style.display="none";
	document.getElementById("page_memory").style.display="none";
	document.getElementById("page_io").style.display="none";
	document.getElementById("page_thread").style.display="none";
	document.getElementById("page_log").style.display="none";
}
//设置导航条：
function setNowNev(ID){//0 1 2 
	var page_nav = document.getElementById("page_nav");
	var childs = page_nav.children; 
	for(var i = childs.length - 1; i >= 0; i--) { 
		if(i == ID){
			$(childs[i]).addClass("active"); 
		}else{
			$(childs[i]).removeClass("active"); 
		}
		
	}
}

	

// -------------------------------------------------------------------------------------
// -----------------------------<汇总页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

function showTestStateSummaryTable(){	
	//测试时长：
	var testTime =  getRealTime_Format2(normalInfos[normalInfos.length-1].time);
	$("#testState_testTime_value").html('<p style="color:black;">'+testTime+'</p>');
	//activity数
	var activitys = new Array();
	for(var i=0;i<pageLoadInfos.length;i++){
		var isExist = false;
		for(var k = 0;k<activitys.length;k++){
			if(activitys[k]==pageLoadInfos[i].activityClassName){
				isExist = true;
				break;
			}
		}
		if(!isExist){
			activitys.push(pageLoadInfos[i].activityClassName);
		}
	}
	$("#testState_activityNumber_value").html('<p style="color:black;">'+activitys.length+'</p>');
	//页面数
	$("#testState_pageNumber_value").html('<p style="color:black;">'+pageLoadInfos.length+'</p>');
	//测试机型
	$("#testState_testModel_value").html('<p style="color:black;">'+deviceInfo.vendor+" "+deviceInfo.model+'</p>');
	//测试系统
	$("#testState_testSystem_value").html('<p style="color:black;">'+"Android "+deviceInfo.sdkName+" ("+deviceInfo.sdkInt+")"+'</p>');
	
}
	
function showTestResultSummaryTable(){
	var blockTextHtml = "";
	//卡顿检测模块：
	if(tableBaseData_lowSM==null || tableBaseData_lowSM.length==0){
		blockTextHtml = blockTextHtml+'<p style="color:green">未发现低流畅值区间</p>';
	}else{
		blockTextHtml = blockTextHtml+'<p style="color:#EE4000">发现了'+tableBaseData_lowSM.length+'个低流畅值区间！</p>';
	}
	if(tableBaseData_bigBlock==null || tableBaseData_bigBlock.length==0){
		blockTextHtml = blockTextHtml+'<p style="color:green">未发现主线程阻塞</p>';
	}else{
		blockTextHtml = blockTextHtml+'<p style="color:#EE4000">发现了'+tableBaseData_bigBlock.length+'个主线程阻塞！</p>';
	}
	$('#result_block').html(blockTextHtml);
	//页面测速模块：
	if(tableBaseData_overActivity==null || tableBaseData_overActivity.length==0){
		$('#result_pageLoad').html('<p style="color:green">未发现超时的页面</p>');
	}else{
		$('#result_pageLoad').html('<p style="color:#EE4000">发现'+tableBaseData_overActivity.length+'个超时页面！</p>');
	}
	//Fragment测速模块：
	if(tableBaseData_overFragment==null || tableBaseData_overFragment.length==0){
		$('#result_fragment').html('<p style="color:green">未发现超时的Fragment</p>');
	}else{
		$('#result_fragment').html('<p style="color:#EE4000">发现'+tableBaseData_overFragment.length+'个超时Fragment！</p>');
	}
	//布局检测模块
	var viewTextHtml = "";
	if(tableBaseData_overViewBuild==null || tableBaseData_overViewBuild.length==0){
		viewTextHtml = viewTextHtml+'<p style="color:green">未发现View构建超时</p>';
	}else{
		viewTextHtml = viewTextHtml+'<p style="color:#EE4000">发现了'+tableBaseData_overViewBuild.length+'次Layout构建超时！</p>';
	}
	if(tableBaseData_overViewDraw==null || tableBaseData_overViewDraw.length==0){
		viewTextHtml = viewTextHtml+'<p style="color:green">未发现View绘制超时</p>';
	}else{
		viewTextHtml = viewTextHtml+'<p style="color:#EE4000">发现了'+tableBaseData_overViewDraw.length+'次页面绘制超时！</p>';
	}
	$('#result_view').html(viewTextHtml);
	
	
	
	//G C检测模块：
	if(tableBaseData_explicitGC==null || tableBaseData_explicitGC.length==0){
		$('#result_memory').html('<p style="color:green">未发现显示GC调用！</p>');
	}else{
		$('#result_memory').html('<p style="color:#EE4000">发现'+tableBaseData_explicitGC.length+'个显示GC调用！</p>');
	}
	
	//I O检测模块：
	if(tableBaseData_dbActionInMainThread==null || tableBaseData_dbActionInMainThread.length==0){
		$('#result_io').html('<p style="color:green">未发现主线程数据库操作！</p>');
	}else{
		$('#result_io').html('<p style="color:#EE4000">发现'+tableBaseData_dbActionInMainThread.length+'个主线程数据库操作！</p>');
	}
	
	//线程分析模块：
	$('#result_thread').html('<p style="color:green">已捕获所有线程信息</p>');
	//Logcat模块：
	$('#result_log').html('<p style="color:green">已捕获所有日志信息</p>');
	
	
	
}
		


// -------------------------------------------------------------------------------------
// -----------------------------<基础性能页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

//表格:基础性能
function show_baseTable(){
	var data = getTableData_base();
	
	//cpu：
	$("#baseTable_front_cpu_average").html('<p style="color:black;">'+data.front_cpu_average+'</p>');
	$("#baseTable_front_cpu_max").html('<p style="color:black;">'+data.front_cpu_max+'</p>');
	$("#baseTable_back_cpu_average").html('<p style="color:black;">'+data.back_cpu_average+'</p>');
	$("#baseTable_back_cpu_max").html('<p style="color:black;">'+data.back_cpu_max+'</p>');
	//memory：
	$("#baseTable_front_memory_average").html('<p style="color:black;">'+data.front_memory_average+'</p>');
	$("#baseTable_front_memory_max").html('<p style="color:black;">'+data.front_memory_max+'</p>');
	$("#baseTable_back_memory_average").html('<p style="color:black;">'+data.back_memory_average+'</p>');
	$("#baseTable_back_memory_max").html('<p style="color:black;">'+data.back_memory_max+'</p>');
	//flow：
	$("#baseTable_front_flow_up").html('<p style="color:black;">'+data.front_flow_up+'</p>');
	$("#baseTable_front_flow_down").html('<p style="color:black;">'+data.front_flow_down+'</p>');
	$("#baseTable_back_flow_up").html('<p style="color:black;">'+data.back_flow_up+'</p>');
	$("#baseTable_back_flow_down").html('<p style="color:black;">'+data.back_flow_down+'</p>');
	//sm：
	$("#baseTable_front_sm_average").html('<p style="color:black;">'+data.front_sm_average+'</p>');
	$("#baseTable_front_sm_min").html('<p style="color:black;">'+data.front_sm_min+'</p>');
	
}


//曲线图:基础性能
function show_baseChart(){
	var chartDivID = "div_baseSummaryChart";
	var data = getTableData_baseChartData();
	
	var option = {
		 title : {
			text: '基础性能检测',
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['CPU','内存','流量','流畅值'],
			x: 'left'
		},
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				triggerEvent  : true,
				axisLine: {onZero: false},
				data : data.xAxis
			}
		],
		yAxis: [
			{
				name: 'CPU(%)',
				type: 'value',
				position: 'left',
				offset: 0
			},
			{
				name: '内存(MB)',
				type: 'value',
				position: 'left',
				offset: 50
			},
			{
				name: '流量(KB)',
				type: 'value',
				position: 'right',
				offset: 0
			},
			{
				name: '流畅值(帧/s)',
				type: 'value',
				position: 'right',
				offset: 50
			}
		],
		series: [
			{
				name:'CPU',
				type:'line',
				yAxisIndex:0,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:data.cpuData
			},
			{
				name:'内存',
				type:'line',
				yAxisIndex:1,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:data.memoryData
			},
			{
				name:'流量',
				type:'line',
				yAxisIndex:2,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:data.flowData
			},
			{
				name:'流畅值',
				type:'line',
				yAxisIndex:3,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:data.smData
			}
		],
		//数据缩放模式
		dataZoom: [
			{
				show: true,
				realtime: true,
				start: 0,
				end: 100
			},
			{
				type: 'inside',
				realtime: true,
				start: 0,
				end: 100
			}
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_Page(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_CPU(xAxisValue);
				tip = tip + getToolTip_Mem(xAxisValue);
				tip = tip + getToolTip_Flow(xAxisValue);
				
				//tip = tip + getToolTip_Operation(xAxisValue);
				//tip = tip + getToolTip_Block(xAxisValue);
				//tip = tip + getToolTip_Draw(pageLoadInfo,xAxisValue);
				//tip = tip + getToolTip_ViewBuild(xAxisValue);
				//tip = tip + getToolTip_Lifecycle(pageLoadInfo,xAxisValue);
				return tip;
			}
		}
	};
	//配置：
	var chart =echarts.init(document.getElementById(chartDivID));
	//配置图像参数：
	chart.setOption(option);
	
}



// -------------------------------------------------------------------------------------
// -----------------------------<卡顿检测页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

//低流畅值
function show_blockLowSMTable(){
	var divID = "div_LowSM";
	
	var tableId = "table_LowSM"
	var data = getTableData_lowSM();
	
	$("#"+divID).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
					+'<th>发生时间(ms)</th>'
					+'<th>最低流畅值</th>'
					+'<th>区间时长(ms)</th>'
					+'<th>页面定位</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	//清空一下table
	var table = $('#'+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTablesObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime" },
			{ "data": "minSM" },
			{ "data": "totalTime" },
			{ "data": "pageName" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = dataTablesObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_LowSMTableDetail(row.data())).show();//绘制HTML
			show_LowSMTableDetail(row.data());//设置展示的数据
			tr.addClass('shown');
		}
	} );
}
function html_LowSMTableDetail(data){
	//ID：
	var dataID = data.dataID;
	var chartDivId = "lowSMTableDetail_chartDiv_"+dataID;
	var tableDivId = "lowSMTableDetail_tableDiv_"+dataID;
	//HTML：
	var html = ''
		+'<div class="row" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;padding:10px;">'
		+	'<div id="'+chartDivId+'" class="" style="height:350px" ></div>'
		+	'<div id="'+tableDivId+'"></div>'
		+'</div>';
	return html;
}
function show_LowSMTableDetail(data){
	//ID：
	var dataID = data.dataID;
	var chartDivId = "lowSMTableDetail_chartDiv_"+dataID;
	var tableDivId = "lowSMTableDetail_tableDiv_"+dataID;
	//Data:
	var chartData = getChartDataInArea_forBlock(data.startTime,data.endTime);
	var tableData = getLowSMTableDetail_tableData(data.startTime,data.endTime);
	
	
	
	var areaChartOption = {
		 title : {
			text: '',
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['流畅值（帧/s）','卡顿时长（ms）','View构建','操作'],
			x: 'left'
		},
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				triggerEvent  : true,
				axisLine: {onZero: false},
				data : chartData.xAxis
			}
		],
		yAxis: [
			{
				name: '流畅值（帧/s）',
				type: 'value',
				position: 'left',
				offset: 0
			},
			{
				name: '卡顿时长（ms）',
				type: 'value',
				position: 'right',
				offset: 0
			}
		],
		series: [
			{
				name:'流畅值（帧/s）',
				type:'line',
				yAxisIndex:0,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:chartData.smData
			},
			{
				name:'卡顿时长（ms）',
				type:'line',
				yAxisIndex:1,
				animation: false,
				areaStyle: {
					normal: {}
				},
				lineStyle: {
					normal: {
						width: 1
					}
				},
				data: chartData.blockData
			},
			{
				name:'View构建',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.viewBuildData
				}
			},
			{
				name:'操作',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.operationData
				}
			}
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_Page(xAxisValue);
				tip = tip + getToolTip_Operation(xAxisValue);
				tip = tip + getToolTip_Block(xAxisValue);
				//tip = tip + getToolTip_Draw(pageLoadInfo,xAxisValue);
				tip = tip + getToolTip_ViewBuild(xAxisValue);
				//tip = tip + getToolTip_Lifecycle(pageLoadInfo,xAxisValue);
				return tip;
			}
		}
	};
	var areaChart =echarts.init(document.getElementById(chartDivId));
	//配置图像参数：
	areaChart.setOption(areaChartOption);
	
	var divID = tableDivId;
	var tableID = tableDivId+"_table";
	var data = tableData;
		$("#"+divID).html(
		'<table id="'+tableID+'" class="display" style="word-break:break-all; word-wrap:break-word;" >'
			+'<thead>'
				+'<tr>'
					+'<th>发生时间(ms)</th>'
					+'<th>阻塞时长(ms)</th>'
					+'<th>最低流畅值</th>'
					+'<th>代码定位</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	//清空一下table
	var table = $('#'+tableID);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTablesObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime" },
			{ "data": "totalTime" },
			{ "data": "minSM" },
			{ "data": "code" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableID+' tbody').on( 'click', 'tr td.details-control', function () {  
		isChildTableDetail = true;	
		var tr = $(this).closest('tr');
		var row = dataTablesObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_LowSMTableDetailDetail(row.data())).show();//绘制HTML
			tr.addClass('shown');
		}
	} );

}
function html_LowSMTableDetailDetail(data){
	
	var stackInfoList = data.stackInfoList;
	
	var html = '<table class="display" style="word-break:break-all; word-wrap:break-word;" >'
		+'<thead>'
			+'<tr>'
				+'<th>时间</th>'
				+'<th>调用栈</th>'
			+'</tr>'
		+'</thead>'
		+'<tbody>';
		for(var i=0;i<stackInfoList.length;i++){
			html = html 
			+'<tr>'
			+'	<td>';
				html = html +getRealTime(stackInfoList[i].time);
			html = html 
			+'	</td>'
			+'	<td>';
			var lineArray = stackInfoList[i].stack.split('\n');
			for(var k=lineArray.length-1;k>=0;k--){
				html = html + lineArray[k]+"<br />";
			}
			html = html 
			+'	</td>'
			+'</tr>';
		}
		html = html
		+'</tbody>'
	+'</table>';
	return html;
}

//大卡顿
function show_blockBigTable(){
	
	var divID = "div_BigTable";
	var tableID = "table_BigTable"
	var data = getTableData_bigBlock();
	
	$("#"+divID).html(
		'<table id="'+tableID+'" class="display" style="word-break:break-all; word-wrap:break-word;" >'
			+'<thead>'
				+'<tr>'
					+'<th>发生时间(ms)</th>'
					+'<th>阻塞时长(ms)</th>'
					+'<th>最低流畅值</th>'
					+'<th>代码定位</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	//清空一下table
	var table = $('#'+tableID);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTablesObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime" },
			{ "data": "totalTime" },
			{ "data": "minSM" },
			{ "data": "code" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableID+' tbody').on( 'click', 'tr td.details-control', function () {  
		var tr = $(this).closest('tr');
		var row = dataTablesObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_bigTableDetail(row.data())).show();//绘制HTML
			show_bigTableDetail(row.data());//设置展示的数据
			tr.addClass('shown');
		}
	} );
}
function html_bigTableDetail(data){

	//ID：
	var dataID = data.dataID;
	var chartDivId = "bigBlockTableDetail_chartDiv_"+dataID;
	var tableDivId = "bigBlockTableDetail_tableDiv_"+dataID;
	//HTML：
	var html = ''
		+'<div class="row" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;padding:10px;">'
		+	'<div id="'+chartDivId+'" class="" style="height:350px" ></div>'
		+	'<div id="'+tableDivId+'" ></div>'
		+'</div>';
	return html;
}
function show_bigTableDetail(data){
	
	//ID：
	var dataID = data.dataID;
	var chartDivId = "bigBlockTableDetail_chartDiv_"+dataID;
	var tableDivId = "bigBlockTableDetail_tableDiv_"+dataID;
	//Data:
	var chartData = getChartDataInArea_forBlock(data.startTime,data.endTime);
	var tableData = data.stackInfoList;
	
	//图：
	var areaChartOption = {
		 title : {
			text: '',
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['流畅值（帧/s）','卡顿时长（ms）'],
			x: 'left'
		},
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				triggerEvent  : true,
				axisLine: {onZero: false},
				data : chartData.xAxis
			}
		],
		yAxis: [
			{
				name: '流畅值（帧/s）',
				type: 'value',
				position: 'left',
				offset: 0
			},
			{
				name: '卡顿时长（ms）',
				type: 'value',
				position: 'right',
				offset: 0
			}
		],
		series: [
			{
				name:'流畅值（帧/s）',
				type:'line',
				yAxisIndex:0,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:chartData.smData
			},
			{
				name:'卡顿时长（ms）',
				type:'line',
				yAxisIndex:1,
				animation: false,
				areaStyle: {
					normal: {}
				},
				lineStyle: {
					normal: {
						width: 1
					}
				},
				data: chartData.blockData
			}
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_Page(xAxisValue);
				tip = tip + getToolTip_Operation(xAxisValue);
				tip = tip + getToolTip_Block(xAxisValue);
				//tip = tip + getToolTip_Draw(pageLoadInfo,xAxisValue);
				tip = tip + getToolTip_ViewBuild(xAxisValue);
				//tip = tip + getToolTip_Lifecycle(pageLoadInfo,xAxisValue);
				return tip;
			}
		}
	};
	var areaChart =echarts.init(document.getElementById(chartDivId));
	areaChart.setOption(areaChartOption);
	
	//表：
	$("#"+tableDivId).html(makeStackListTableHTML(data.stackInfoList));
}


// -------------------------------------------------------------------------------------
// -----------------------------<页面测速页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

//页面测速结果表:
function show_pageLoadOverTable(){	//创建表格，并加载卡顿数据

	var data = getTableData_overActivity();
	var divTableId = "div_pageLoadOverTable";
	var tableId = "pageLoadOverTable";

	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
					+'<th>超时Activity</th>'
					+'<th>最大启动时长</th>'
					+'<th>启动区域最小SM</th>'
					+'<th>非启动区域最小SM</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "activityClassName" },
			{ "data": "loadTime" },
			{ "data": "loadMinSM" },
			{ "data": "pageMinSM" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {  
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_pageLoadDetail('over',row.data().pageLoadInfo)).show();//绘制HTML
			show_pageLoadDetail('over',row.data().pageLoadInfo)//设置展示的数据
			tr.addClass('shown');
		}
	} );
}
function show_pageLoadAllTable(){

	

	var data = getTableData_allPage();
	var divTableId = "div_pageLoadAllTable";
	var tableId = "pageLoadAllTable";
	

	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
		+'	<thead>'
		+'		<tr>'
		+'			<th>启动次序</th>'
		+'			<th>页面名</th>'
		+'			<th>冷热启动</th>'
		+'			<th>启动时长</th> '
		+'          <th>启动区域最小SM</th>'
		+'          <th>非启动区域最小SM</th>'
		+'			<th>详细</th>'
		+'		</tr>'
		+'	</thead>'
		+'</table>');


	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject = table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "dataID" },
			{ "data": "activityClassName" },
			{ "data": "coldHot" },
			{ "data": "loadTime" },
			{ "data": "loadMinSM" },
			{ "data": "pageMinSM" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );

	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {  
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child( html_pageLoadDetail('all',row.data().pageLoadInfo)).show();//绘制HTML
			show_pageLoadDetail('all',row.data().pageLoadInfo)//设置展示的数据
			tr.addClass('shown');
		}
	} );
	
}

//展开-详细数据：
function getPageLoadDetailBlock_tableData(pageLoadInfo){	//获取页面区域内的卡顿数据			
	var startTime = getPageStartTime(pageLoadInfo);
	var endTime = getPageEndTime(pageLoadInfo);
	
	var pageLoadDetailBlock_tableData = new Array();
	for(var k=0;k<allBlockInfos.length;k++){
		if(allBlockInfos[k].startTime>endTime || allBlockInfos[k].endTime<startTime){
			continue;
		}else{
			var newData = new Object();
			newData.startTime = allBlockInfos[k].startTime;
			newData.endTime = allBlockInfos[k].endTime;
			newData.totalTime = allBlockInfos[k].endTime-allBlockInfos[k].startTime;
			newData.realStartTime = allBlockInfos[k].startTime-appInfo.startTestTime;
			newData.minSM = getMinSMInArea(allBlockInfos[k].startTime,allBlockInfos[k].endTime);
			newData.code = getMostEffectiveCode(allBlockInfos[k].stackInfoList);
			newData.stackInfoList = allBlockInfos[k].stackInfoList;
			pageLoadDetailBlock_tableData.push(newData);
		}
	}
	return pageLoadDetailBlock_tableData;
}
function html_pageLoadDetail(fatherTable,pageLoadInfo){
	var idName = fatherTable+pageLoadInfo.startOrderId;
	var html = ""
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class=""  style="color:#4876FF"><b id="pageLoadMore_Title_'+idName+'">XXX页面：</b></h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">1)页面启动详细图 ：</h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;">'
		+'	<div id="pageLoadMore_Chart_'+idName+'" class="" style="height:350px" ></div>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">2)页面卡顿信息表 ：</h5>'
		+'</div>'
		+'<div class="row" id="div_pageLoadMore_Table_'+idName+'" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;padding:10px;word-break:break-all; word-wrap:break-word;">'
		+'	<table id="pageLoadMore_Table_'+idName+'" class="display" >'
		+'	</table>'
		+'</div>';

	return html;
}
function show_pageLoadDetail(fatherTable,pageLoadInfo){
	var idName = fatherTable+pageLoadInfo.startOrderId;
	var pageLoadMore_Title_id = 'pageLoadMore_Title_'+idName;
	var pageLoadMore_Chart_id = 'pageLoadMore_Chart_'+idName;
	var div_pageLoadMore_Table_id = 'div_pageLoadMore_Table_'+idName;
	var pageLoadMore_Table_id = 'pageLoadMore_Table_'+idName;
	
	//标题
	var sss = pageLoadInfo.activityClassName.split('.');
	var activityName = sss[sss.length-1];
	$("#"+pageLoadMore_Title_id).html("第"+(pageLoadInfo.startOrderId)+"个页面:"+activityName+"@"+pageLoadInfo.objectHashCode+"的详细启动数据：");
	//图
	var chartData = getChartDataInArea_forPage(pageLoadInfo);
	var chart_option= {
		title : {
			text: "",
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['Activity生命周期函数','卡顿数据','流畅值（帧/s）','绘制数据','View构建','操作'],
			x: 'left'
		},
		dataZoom: [
			{
				show: true,
				realtime: true,
				startValue: 0,
				endValue: 5000
			},
			{
				type: 'inside',
				realtime: true,
				startValue: 0,
				endValue: 5000
			}
		],
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				axisLine: {onZero: false},
				data : chartData.xAxis
			}
		],
		yAxis: [
			{
				name: '卡顿时长（ms）',
				type: 'value',
				position: 'left'
			},
			{
				name: '绘制深度',
				type: 'value',
				position: 'right',
				offset: 0
			},
			{
				name: '流畅值',
				type: 'value',
				position: 'right',
				offset: 40
			}
		],
		series: [
			{
				name:'Activity生命周期函数',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#e6a019'  
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.lifecycle
				}
			},
			{
				name:'流畅值（帧/s）',
				type:'line',
				yAxisIndex:2,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:chartData.smData
			},
			{
				name:'View构建',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.viewBuild
				}
			},
			{
				name:'操作',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.operationMarkArea
				}
			},
			{
				name:'卡顿数据',
				type:'line',
				yAxisIndex:0,
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#576058'   
					}  
				},  
				data: chartData.blockData
			},
			{
				name:'绘制数据',
				type:'line',
				yAxisIndex:1,
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#25f835'  
					}  
				},  
				data: chartData.drawData
			}
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_Page(xAxisValue);
				tip = tip + getToolTip_Operation(xAxisValue);
				tip = tip + getToolTip_Block(xAxisValue);
				tip = tip + getToolTip_Draw(pageLoadInfo,xAxisValue);
				tip = tip + getToolTip_ViewBuild(xAxisValue);
				tip = tip + getToolTip_Lifecycle(pageLoadInfo,xAxisValue);
				return tip;
			}
		}
		
	};
	var chart = echarts.init(document.getElementById(pageLoadMore_Chart_id));
	chart.setOption(chart_option);

	//表
	var tableData = getPageLoadDetailBlock_tableData(pageLoadInfo);
	var divID = div_pageLoadMore_Table_id;
	var tableID = pageLoadMore_Table_id;
	var data = tableData;
	$("#"+divID).html(
		'<table id="'+tableID+'" class="display" style="word-break:break-all; word-wrap:break-word;" >'
			+'<thead>'
				+'<tr>'
					+'<th>发生时间(ms)</th>'
					+'<th>阻塞时长(ms)</th>'
					+'<th>最低流畅值</th>'
					+'<th>代码定位</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	//清空一下table
	var table = $('#'+tableID);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTablesObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime" },
			{ "data": "totalTime" },
			{ "data": "minSM" },
			{ "data": "code" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableID+' tbody').on( 'click', 'tr td.details-control', function () {  
		isChildTableDetail = true;	
		var tr = $(this).closest('tr');
		var row = dataTablesObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(makeStackListTableHTML(row.data().stackInfoList)).show();//绘制HTML
			tr.addClass('shown');
		}
	} );
	
}



// -------------------------------------------------------------------------------------
// -----------------------------<Fragment测速页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

//Fragment测速结果表:
function show_fragmentOverTable(){	//创建表格，并加载卡顿数据

	var data = getTableData_overFragment();
	var divTableId = "div_overFragmentTable";
	var tableId = "overFragmentTable";

	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
					+'<th>超时Fragment</th>'
					+'<th>最大启动时长</th>'
					+'<th>启动区域最小SM</th>'
					+'<th>非启动区域最小SM</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "fragmentClassName" },
			{ "data": "loadTime" },
			{ "data": "loadMinSM" },
			{ "data": "fragMinSM" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {  
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_fragmentDetail('over',row.data().fragmentInfo)).show();//绘制HTML
			show_fragmentDetail('over',row.data().fragmentInfo,row.data().pageLoadInfo)//设置展示的数据
			tr.addClass('shown');
		}
	} );
}
function show_fragmentAllTable(){	//创建表格，并加载卡顿数据

	var data = getTableData_allFragment();
	var divTableId = "div_allFragmentTable";
	var tableId = "allFragmentTable";

	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
				+'			<th>启动次序</th>'
				+'			<th>Fragment名</th>'
				+'			<th>冷热启动</th>'
				+'			<th>启动时长</th> '	
				+'          <th>启动区域最小SM</th>'
				+'          <th>非启动区域最小SM</th>'
				+'			<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "dataID" },
			{ "data": "fragmentClassName" },
			{ "data": "coldHot" },
			{ "data": "loadTime" },
			{ "data": "loadMinSM" },
			{ "data": "fragMinSM" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {  
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_fragmentDetail('all',row.data().fragmentInfo)).show();//绘制HTML
			show_fragmentDetail('all',row.data().fragmentInfo,row.data().pageLoadInfo)//设置展示的数据
			tr.addClass('shown');
		}
	} );

	}

//展开-Fragment详细数据：
function html_fragmentDetail(fatherTable,fragmentInfo){
	var idName = fatherTable+fragmentInfo.startOrderId;
	var html = ""
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class=""  style="color:#4876FF"><b id="fragmentMore_Title_'+idName+'">XXXFragment：</b></h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">1)Fragment启动详细图 ：</h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;">'
		+'	<div id="fragmentMore_Chart_'+idName+'" class="" style="height:350px" ></div>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">2)Fragment卡顿信息表 ：</h5>'
		+'</div>'
		+'<div class="row" id="div_fragmentMore_Table_'+idName+'" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;padding:10px;word-break:break-all; word-wrap:break-word;">'
		+'	<table id="fragmentMore_Table_'+idName+'" class="display" >'
		+'	</table>'
		+'</div>';

	return html;
}
function show_fragmentDetail(fatherTable,fragmentInfo,pageLoadInfo){
	
	var idName = fatherTable+fragmentInfo.startOrderId;
	var fragmentMore_Title_id = 'fragmentMore_Title_'+idName;
	var fragmentMore_Chart_id = 'fragmentMore_Chart_'+idName;
	var div_fragmentMore_Table_id = 'div_fragmentMore_Table_'+idName;
	var fragmentMore_Table_id = 'fragmentMore_Table_'+idName;
	
	
	
	//标题
	var fff = fragmentInfo.fragmentClassName.split('.');
	var fragmentName = fff[fff.length-1];
	$("#"+fragmentMore_Title_id).html("第"+fragmentInfo.startOrderId+"个Fragment:"+fragmentName+"@"+fragmentInfo.fragmentHashCode+"的详细启动数据:");
	if(pageLoadInfo!=null){
		var sss = pageLoadInfo.activityClassName.split('.');
		var activityName = sss[sss.length-1];
		$("#"+fragmentMore_Title_id).html("第"+fragmentInfo.startOrderId+"个Fragment:"+fragmentName+"@"+fragmentInfo.fragmentHashCode+"的详细启动数据（"
										+"属于第"+pageLoadInfo.startOrderId+"个页面-"+activityName+"@"+pageLoadInfo.objectHashCode+"）；");
	}
	
	//图
	var chartData = getChartDataInArea_forFragment(fragmentInfo,pageLoadInfo);
	var chart_option= {
		title : {
			text: "",
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['Activity生命周期函数','卡顿数据','流畅值（帧/s）','View构建','操作'],
			x: 'left'
		},
		dataZoom: [
			{
				show: true,
				realtime: true,
				startValue: 0,
				endValue: 5000
			},
			{
				type: 'inside',
				realtime: true,
				startValue: 0,
				endValue: 5000
			}
		],
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				axisLine: {onZero: false},
				data : chartData.xAxis
			}
		],
		yAxis: [
			{
				name: '卡顿时长（ms）',
				type: 'value',
				position: 'left'
			},
			{
				name: '流畅值',
				type: 'value',
				position: 'right',
				offset: 40
			}
		],
		series: [
			{
				name:'Activity生命周期函数',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#e6a019'  
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.lifecycle
				}
			},
			{
				name:'流畅值（帧/s）',
				type:'line',
				yAxisIndex:1,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:chartData.smData
			},
			{
				name:'View构建',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.viewBuild
				}
			},
			{
				name:'操作',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.operationMarkArea
				}
			},
			{
				name:'卡顿数据',
				type:'line',
				yAxisIndex:0,
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#576058'   
					}  
				},  
				data: chartData.blockData
			}
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_Page(xAxisValue);
				tip = tip + getToolTip_Operation(xAxisValue);
				tip = tip + getToolTip_Block(xAxisValue);
				//tip = tip + getToolTip_Draw(pageLoadInfo,xAxisValue);
				tip = tip + getToolTip_ViewBuild(xAxisValue);
				//tip = tip + getToolTip_Lifecycle(pageLoadInfo,xAxisValue);
				tip = tip + getToolTip_FragmentLifecycle(fragmentInfo,xAxisValue)
				return tip;
			}
		}
		
	};
	var chart = echarts.init(document.getElementById(fragmentMore_Chart_id));
	chart.setOption(chart_option);

	//表
	var tableData = getFragmentDetailBlock_tableData(fragmentInfo);
	var divID = div_fragmentMore_Table_id;
	var tableID = fragmentMore_Table_id;
	var data = tableData;
	$("#"+divID).html(
		'<table id="'+tableID+'" class="display" style="word-break:break-all; word-wrap:break-word;" >'
			+'<thead>'
				+'<tr>'
					+'<th>发生时间(ms)</th>'
					+'<th>阻塞时长(ms)</th>'
					+'<th>最低流畅值</th>'
					+'<th>代码定位</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	//清空一下table
	var table = $('#'+tableID);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTablesObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime" },
			{ "data": "totalTime" },
			{ "data": "minSM" },
			{ "data": "code" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableID+' tbody').on( 'click', 'tr td.details-control', function () {  
		isChildTableDetail = true;	
		var tr = $(this).closest('tr');
		var row = dataTablesObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(makeStackListTableHTML(row.data().stackInfoList)).show();//绘制HTML
			tr.addClass('shown');
		}
	} );
	
}
function getFragmentDetailBlock_tableData(fragmentInfo){
	var startTime = getFragmentStartTime(fragmentInfo);
	var endTime = getFragmentEndTime(fragmentInfo);
	
	var pageLoadDetailBlock_tableData = new Array();
	for(var k=0;k<allBlockInfos.length;k++){
		if(allBlockInfos[k].startTime>endTime || allBlockInfos[k].endTime<startTime){
			continue;
		}else{
			var newData = new Object();
			newData.startTime = allBlockInfos[k].startTime;
			newData.endTime = allBlockInfos[k].endTime;
			newData.totalTime = allBlockInfos[k].endTime-allBlockInfos[k].startTime;
			newData.realStartTime = allBlockInfos[k].startTime-appInfo.startTestTime;
			newData.minSM = getMinSMInArea(allBlockInfos[k].startTime,allBlockInfos[k].endTime);
			newData.code = getMostEffectiveCode(allBlockInfos[k].stackInfoList);
			newData.stackInfoList = allBlockInfos[k].stackInfoList;
			pageLoadDetailBlock_tableData.push(newData);
		}
	}
	return pageLoadDetailBlock_tableData;
}





// -------------------------------------------------------------------------------------
// -----------------------------<布局检测页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

//View构建超时表:
function show_overViewBuildTable(){	
		$("#div_table_overViewBuildData").html(
			'<table id="table_overViewBuildData" class="display" >'
				+'<thead>'
					+'<tr>'
						+'<th>layout名字</th>'
						+'<th>构建时长(ms)</th>'
						+'<th>所属页面</th>'
						+'<th>详细</th>'
					+'</tr>'
				+'</thead>'
			+'</table>'
		);
		
		var overViewBuildData = getTableData_overViewBuild();
		var div_table = $('#table_overViewBuildData');
		if (div_table.hasClass('dataTable')) {
			var dttable = div_table.dataTable();
			dttable.fnClearTable(); //清空一下table
			dttable.fnDestroy(); //还原初始化了的datatable
		}
		var table_overViewBuildData =div_table.DataTable( {
			data: overViewBuildData,
			//每列所对应的数据字段：
			"columns": [
				{ "data": "viewName" },
				{ "data": "buildTime" },
				{ "data": "page" },
				{
					"class":          'details-control',
					"orderable":      false,
					"data":           null,
					"defaultContent": ''
				}
			],
			"order": [[1, 'desc']],
			"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
			"iDisplayLength": 10,
			"oLanguage": {
			   "oAria": {
				   "sSortAscending": " - click/return to sort ascending",
				   "sSortDescending": " - click/return to sort descending"
			   },
			   "sLengthMenu": "显示 _MENU_ 记录",
			   "sZeroRecords": "对不起，查询不到任何相关数据",
			   "sEmptyTable": "未有相关数据",
			   "sLoadingRecords": "正在加载数据-请等待...",
			   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
			   "sInfoEmpty": "当前显示0到0条，共0条记录",
			   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
			   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
			   "sSearch": "模糊查询：",
			   "sUrl": "",
			   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
			   "oPaginate": {
				   "sFirst": "首页",
				   "sPrevious": " 上一页 ",
				   "sNext": " 下一页 ",
				   "sLast": " 尾页 "
			   }
		   },
		} );
		$('#table_overViewBuildData tbody').on( 'click', 'tr td.details-control', function () {  
			if(isChildTableDetail){
				isChildTableDetail = false;
				return ;
			}
			var tr = $(this).closest('tr');
			var row = table_overViewBuildData.row(tr);
			if ( row.child.isShown() ) {
				// This row is already open - close it
				row.child.hide();
				tr.removeClass('shown');
			}
			else {
				// Open this row
				row.child( html_overViewTableDetail('viewBuild_',row.data())).show();//绘制HTML
				show_overViewTableDetail('viewBuild_',row.data())//设置展示的数据
				tr.addClass('shown');
			}
		} );
	}

		
//View绘制超时表:
function show_overViewDrawTable(){	
	$("#div_table_overViewDrawData").html(
		'<table id="table_overViewDrawData" class="display" >'
			+'<thead>'
				+'<tr>'
					+'<th>时间</th>'
					+'<th>Activity</th>'
					+'<th>View</th>'
					+'<th>绘制时长(ms)</th>'
					+'<th>绘制深度</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	var overViewDrawData = getTableData_overViewDraw();
	var div_table = $('#table_overViewDrawData');
	if (div_table.hasClass('dataTable')) {
		var dttable = div_table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var table_overViewDrawData =div_table.DataTable( {
		data: overViewDrawData,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime" },
			{ "data": "page" },
			{ "data": "drawClassName" },
			{ "data": "drawTime" },
			{ "data": "drawDeep" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	$('#table_overViewDrawData tbody').on( 'click', 'tr td.details-control', function () {  
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = table_overViewDrawData.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child( html_overViewTableDetail('draw_',row.data())).show();//绘制HTML
			show_overViewTableDetail('draw_',row.data())//设置展示的数据
			tr.addClass('shown');
		}
	} );
	
}
		
function html_overViewTableDetail(fatherTable,data){
	var idName = fatherTable+data.dataID;
	var pageLoadMore_Title_id = 'overViewTableDetail_Title_'+idName;
	var pageLoadMore_Chart_id = 'overViewTableDetail_Chart_'+idName;
	
	var html = ""
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class=""  style="color:#4876FF"><b id="overViewTableDetail_Title_'+idName+'">XXX页面：</b></h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">1)页面启动详细图 ：</h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;">'
		+'	<div id="overViewTableDetail_Chart_'+idName+'" class="" style="height:350px" ></div>'
		+'</div>';

	return html;
	
	
}
		
function show_overViewTableDetail(fatherTable,data){
	var pageLoadInfo = data.pageLoadInfo;
	var idName = fatherTable+data.dataID;
	var pageLoadMore_Title_id = 'overViewTableDetail_Title_'+idName;
	var pageLoadMore_Chart_id = 'overViewTableDetail_Chart_'+idName;
	
	//标题
	var sss = pageLoadInfo.activityClassName.split('.');
	var activityName = sss[sss.length-1];
	$("#"+pageLoadMore_Title_id).html("第"+(pageLoadInfo.startOrderId+1)+"个页面:"+activityName+"@"+pageLoadInfo.objectHashCode+"的详细启动数据：");
	
	//图
	var chartData = getChartDataInArea_forPage(pageLoadInfo);
	var chart_option= {
		title : {
			text: "",
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['Activity生命周期函数','卡顿数据','流畅值（帧/s）','绘制数据','View构建','操作'],
			x: 'left'
		},
		dataZoom: [
			{
				show: true,
				realtime: true,
				startValue: 0,
				endValue: 5000
			},
			{
				type: 'inside',
				realtime: true,
				startValue: 0,
				endValue: 5000
			}
		],
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				axisLine: {onZero: false},
				data : chartData.xAxis
			}
		],
		yAxis: [
			{
				name: '卡顿时长（ms）',
				type: 'value',
				position: 'left'
			},
			{
				name: '绘制深度',
				type: 'value',
				position: 'right',
				offset: 0
			},
			{
				name: '流畅值',
				type: 'value',
				position: 'right',
				offset: 40
			}
		],
		series: [
			{
				name:'Activity生命周期函数',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#e6a019'  
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.lifecycle
				}
			},
			{
				name:'流畅值（帧/s）',
				type:'line',
				yAxisIndex:2,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:chartData.smData
			},
			{
				name:'View构建',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.viewBuild
				}
			},
			{
				name:'操作',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.operationMarkArea
				}
			},
			{
				name:'卡顿数据',
				type:'line',
				yAxisIndex:0,
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#576058'   
					}  
				},  
				data: chartData.blockData
			},
			{
				name:'绘制数据',
				type:'line',
				yAxisIndex:1,
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#25f835'  
					}  
				},  
				data: chartData.drawData
			}
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_Page(xAxisValue);
				tip = tip + getToolTip_Operation(xAxisValue);
				tip = tip + getToolTip_Block(xAxisValue);
				tip = tip + getToolTip_Draw(pageLoadInfo,xAxisValue);
				tip = tip + getToolTip_ViewBuild(xAxisValue);
				tip = tip + getToolTip_Lifecycle(pageLoadInfo,xAxisValue);
				return tip;
			}
		}
	};
	var chart = echarts.init(document.getElementById(pageLoadMore_Chart_id));
	chart.setOption(chart_option);
	
}
		
		
		
// -------------------------------------------------------------------------------------
// -----------------------------<G C检测页相关>--------------------------------------------
// -------------------------------------------------------------------------------------		
		
function show_explicitGCTable(){
	
	var data = getTableData_explicitGC();
	var divTableId = "div_table_explicitGC";
	var tableId = "table_explicitGC";

	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
					+'<th>GC时间</th>'
					+'<th>暂停时长</th>'
					+'<th>总耗时</th>'
					+'<th>堆统计</th>'
					+'<th>所属页面</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime" },
			{ "data": "pauseTime" },
			{ "data": "totalTime" },
			{ "data": "heapStatistics" },
			{ "data": "page" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {  
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_gcTableDetail('explicitGC',row.data())).show();//绘制HTML
			show_gcTableDetail('explicitGC',row.data())//设置展示的数据
			tr.addClass('shown');
		}
	} );

}


function html_gcTableDetail(fatherTable,data){
	
	var id = data.dataID;
	var chartId = 'gcTableDetail_Chart_'+fatherTable+id;
	
	var html = ""
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">1)页面启动详细图 ：</h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;">'
		+'	<div id="'+chartId+'" class="" style="height:350px" ></div>'
		+'</div>';

	return html;
}

function show_gcTableDetail(fatherTable,data){
	var id = data.dataID;
	var chartId = 'gcTableDetail_Chart_'+fatherTable+id;
	
	var gcInfo = data.gcInfo;
	
	
	//图
	var chartData = getChartDataInArea_forGC(gcInfo);
	var chart_option= {
		title : {
			text: "",
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['卡顿数据','流畅值（帧/s）','操作'],
			x: 'left'
		},
		dataZoom: [
			{
				show: true,
				realtime: true,
				startValue: 0,
				endValue: 5000
			},
			{
				type: 'inside',
				realtime: true,
				startValue: 0,
				endValue: 5000
			}
		],
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				axisLine: {onZero: false},
				data : chartData.xAxis
			}
		],
		yAxis: [
			{
				name: '卡顿时长（ms）',
				type: 'value',
				position: 'left'
			},
			{
				name: '绘制深度',
				type: 'value',
				position: 'right',
				offset: 0
			},
			{
				name: '流畅值',
				type: 'value',
				position: 'right',
				offset: 40
			}
		],
		series: [
			{
				name:'流畅值（帧/s）',
				type:'line',
				yAxisIndex:2,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:chartData.smData
			},
			{
				name:'操作',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#33c6f0' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.operationMarkArea
				}
			},
			{
				name:'卡顿数据',
				type:'line',
				yAxisIndex:0,
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#576058'   
					}  
				},  
				data: chartData.blockData
			},
			{
				name:'GC',
				type:'line',
				animation: false,
				areaStyle: {
					normal: {}
				},
				itemStyle : {  
					normal : {  
						color:'#25f835' 
					}  
				},  
				markArea: {
					silent: true,
					data: chartData.gcMarkArea
				}
			}
			
			
			
			
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_gc(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_Operation(xAxisValue);
				tip = tip + getToolTip_Block(xAxisValue);
				
				return tip;
			}
		}
		
	};
	var chart = echarts.init(document.getElementById(chartId));
	chart.setOption(chart_option);
	
	
}



		
// -------------------------------------------------------------------------------------
// -----------------------------<I O检测页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

function show_fileMainThread(){
	
	var data = getTableData_fileActionInMainThread();
	var divTableId = "div_fileActionInMainThreadTable";
	var tableId = "fileActionInMainThreadTable";
	
	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
					+'<th>时间(ms)</th>'
					+'<th>文件路径</th>'
					+'<th>读次数</th>'
					+'<th>读大小</th>'
					+'<th>写次数</th>'
					+'<th>写大小</th>'
					+'<th>时长</th>'
					+'<th>多线程？</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime","width": "10%" },
			{ "data": "filePath","width": "20%" },
			{ "data": "readNum","width": "5%"  },
			{ "data": "readSize","width": "5%"  },
			{ "data": "writeNum" ,"width": "5%" },
			{ "data": "writeSize" ,"width": "5%" },
			{ "data": "totalTime","width": "5%"  },
			{ "data": "isMutilThread","width": "5%"  }
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {  
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child( memoryShakeHtml(row.data())).show();//绘制HTML
			drawMemoryShakeChart('chart_memoryShake_'+row.data().shakeID,row.data());//设置展示的数据
			tr.addClass('shown');
		}
	} );

	
}

function show_dbMainThread(){
	
	
	var data = getTableData_dbActionInMainThread();
	var divTableId = "div_dbActionInMainThreadTable";
	var tableId = "dbActionInMainThreadTable";
	
	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
					+'<th>时间</th>'
					+'<th>耗时</th>'
					+'<th>数据库</th>'
					+'<th>操作</th>'
					+'<th>sql</th>'
					+'<th>读次数</th>'
					+'<th>读大小</th>'
					+'<th>写次数</th>'
					+'<th>写大小</th>'
					+'<th>主线程ID</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realStartTime","width": "10%" },
			{ "data": "totalTime","width": "5%" },
			{ "data": "dbName","width": "10%" },
			{ "data": "actionName" ,"width": "15%" },
			{ "data": "sql" ,"width": "30%" },
			{ "data": "readNum","width": "5%"  },
			{ "data": "readSize" ,"width": "5%" },
			{ "data": "writeNum" ,"width": "5%" },
			{ "data": "writeSize","width": "5%"  },
			{ "data": "threadId" ,"width": "5%" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#dataTableObject tbody').on( 'click', 'tr td.details-control', function () {  
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child( dbFileHtml(row.data().localId)).show();//绘制HTML
			drawDBFileTable(row.data().localId,row.data().fileActions);//设置展示的数据
			tr.addClass('shown');
		}
	} );
	
	
}

//列表详细---HTML
function dbFileHtml(id){
	var html = ''
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">1.涉及的文件IO ：</h5>'
		+'</div>'
		+'<div class="row" id="div_table_dbFile_'+id+'" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;padding:10px;">'
		+'	<table id="table_dbFile_'+id+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
		+'	</table>'
		+'</div>'
	return html;
}

//列表详细---data
function drawDBFileTable(id,data){
	$("#div_table_dbFile_"+id).html(
	'<table id="table_dbFile_'+id+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
		+'<thead>'
			+'<tr>'
				+'<th>时间(ms)</th>'
				+'<th>文件名</th>'
				+'<th>操作</th>'
				+'<th>数据大小</th>'
				+'<th>线程ID</th>'
			+'</tr>'
		+'</thead>'
	+'</table>'
);
var div_table = $("#table_dbFile_"+id);
if (div_table.hasClass('dataTable')) {
	var dttable = div_table.dataTable();
	dttable.fnClearTable(); //清空一下table
	dttable.fnDestroy(); //还原初始化了的datatable
}
var table_dbFileIO =div_table.DataTable( {

	data: data,
	//每列所对应的数据字段：
	"columns": [
		{ "data": "actionTime","width": "10%" },
		{ "data": "fileName","width": "20%" },
		{ "data": "actionName" ,"width": "10%" },
		{ "data": "actionSize","width": "5%"  },
		{ "data": "threadID","width": "5%"  }
	],
	"order": [[0, 'asc']],
	"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
	"iDisplayLength": 10,
	"oLanguage": {
	   "oAria": {
		   "sSortAscending": " - click/return to sort ascending",
		   "sSortDescending": " - click/return to sort descending"
	   },
	   "sLengthMenu": "显示 _MENU_ 记录",
	   "sZeroRecords": "对不起，查询不到任何相关数据",
	   "sEmptyTable": "未有相关数据",
	   "sLoadingRecords": "正在加载数据-请等待...",
	   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
	   "sInfoEmpty": "当前显示0到0条，共0条记录",
	   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
	   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
	   "sSearch": "模糊查询：",
	   "sUrl": "",
	   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
	   "oPaginate": {
		   "sFirst": "首页",
		   "sPrevious": " 上一页 ",
		   "sNext": " 下一页 ",
		   "sLast": " 尾页 "
	   }
   },
} );
	
}


// -------------------------------------------------------------------------------------
// -----------------------------<线程分析页相关>--------------------------------------------
// -------------------------------------------------------------------------------------


function show_threadTable(){
	
	var data = getTableData_thread();
	var divTableId = "div_table_thread";
	var tableId = "table_thread";

	$("#"+divTableId).html(
		'<table id="'+tableId+'" class="display" style="word-break:break-all; word-wrap:break-word;">'
			+'<thead>'
				+'<tr>'
					+'<th>线程ID</th>'
					+'<th>线程名</th>'
					+'<th>总时间片(jiffies)</th>'
					+'<th>详细</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	var table = $("#"+tableId);
	if (table.hasClass('dataTable')) {
		var dttable = table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var dataTableObject =table.DataTable( {
		data: data,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "threadId" },
			{ "data": "threadName" },
			{ "data": "threadCpu" },
			{
				"class":          'details-control',
				"orderable":      false,
				"data":           null,
				"defaultContent": ''
			}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[5, 10, -1], ['5', '10', "所有"] ],// change per page values here
		"iDisplayLength": 10,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
	
	
	$('#'+tableId+' tbody').on( 'click', 'tr td.details-control', function () {  
		if(isChildTableDetail){
			isChildTableDetail = false;
			return ;
		}
		var tr = $(this).closest('tr');
		var row = dataTableObject.row(tr);
		if ( row.child.isShown() ) {
			// This row is already open - close it
			row.child.hide();
			tr.removeClass('shown');
		}
		else {
			// Open this row
			row.child(html_threadChartDetail(row.data())).show();//绘制HTML
			show_threadChartDetail(row.data())//设置展示的数据
			tr.addClass('shown');
		}
	} );

}

function html_threadChartDetail(thread){
	
	var threadId= thread.threadId;
	var threadName= thread.threadName;
	var threadCpu= thread.threadCpu;
	
	var idName = threadId;
	var Title_id = 'threadChartDetail_Title_'+idName;
	var Chart_id = 'threadChartDetail_Chart_'+idName;
	
	var html = ""
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class=""  style="color:#4876FF"><b id="'+Title_id+'">XXX页面：</b></h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;">'
		+'	<h5 class="" style="color:#4876FF">1)页面启动详细图 ：</h5>'
		+'</div>'
		+'<div class="row" style="margin-left:10px;margin-right:10px;border:1px solid #aaa;">'
		+'	<div id="'+Chart_id+'" class="" style="height:350px" ></div>'
		+'</div>';

	return html;
	
	
}

function show_threadChartDetail(thread){
	
	var threadId= thread.threadId;
	var threadName= thread.threadName;
	var threadCpu= thread.threadCpu;
	
	var idName = threadId;
	var Title_id = 'threadChartDetail_Title_'+idName;
	var Chart_id = 'threadChartDetail_Chart_'+idName;
	
	
	//标题
	$("#"+Title_id).html("线程"+threadName+"("+threadId+")"+"的时间片数据：");
	

	
	//图
	var data = getChartData_thread(threadId);
	var option = {
		 title : {
			text: '线程时间片统计',
			subtext: '---腾讯优测产品研发组',
			x: 'center',
			align: 'right'
		},
		grid: {
			bottom: 80
		},
		toolbox: {
			feature: {
				dataZoom: {
					yAxisIndex: 'none'
				},
				restore: {},
				saveAsImage: {}
			}
		},
		legend: {
			data:['时间片占用情况'],
			x: 'left'
		},
		xAxis : [
			{
				type : 'category',
				boundaryGap : false,
				triggerEvent  : true,
				axisLine: {onZero: false},
				data : data.xAxis
			}
		],
		yAxis: [
			{
				name: '时间片/jiffies',
				type: 'value',
				position: 'left',
				offset: 0
			}
		],
		series: [
			{
				name:'时间片占用情况',
				type:'line',
				yAxisIndex:0,
				animation: false,
				lineStyle: {
					normal: {
						width: 2
					}
				},
				data:data.threadCpuData
			}
		],
		//数据缩放模式
		dataZoom: [
			{
				show: true,
				realtime: true,
				start: 0,
				end: 100
			},
			{
				type: 'inside',
				realtime: true,
				start: 0,
				end: 100
			}
		],
		//设置提示框
		tooltip: {
			trigger: 'axis',
			formatter: function(params){
				//获取坐标轴值
				var xAxisValue = null;
				for(var i=0;i<20;i++){
					if(params[i].name!=null && params[i].name!=""){
						xAxisValue = params[i].name;
						break;
					}
				}
				if(xAxisValue == null){
					return "";
				}
				//获取tip
				var tip = "";
				tip = tip + getToolTip_Time(xAxisValue);
				tip = tip + getToolTip_Page(xAxisValue);
				tip = tip + getToolTip_SM(xAxisValue);
				tip = tip + getToolTip_CPU(xAxisValue);
				tip = tip + getToolTip_Mem(xAxisValue);
				tip = tip + getToolTip_Flow(xAxisValue);
				
				//tip = tip + getToolTip_Operation(xAxisValue);
				//tip = tip + getToolTip_Block(xAxisValue);
				//tip = tip + getToolTip_Draw(pageLoadInfo,xAxisValue);
				//tip = tip + getToolTip_ViewBuild(xAxisValue);
				//tip = tip + getToolTip_Lifecycle(pageLoadInfo,xAxisValue);
				return tip;
			}
		}
	};
	//配置：
	var chart =echarts.init(document.getElementById(Chart_id));
	//配置图像参数：
	chart.setOption(option);
}




// -------------------------------------------------------------------------------------
// -----------------------------<关键日志页相关>--------------------------------------------
// -------------------------------------------------------------------------------------

//更改logGrade,然后刷新Logcat表格
function switchLogGrade(logGrade){
	if(logGrade == 'Verbose'){
		$("#button_logGrage").html('Verbose<span class="caret"></span>');
	}else if(logGrade == 'Debug'){
		$("#button_logGrage").html('Debug<span class="caret"></span>');
	}else if(logGrade == 'Info'){
		$("#button_logGrage").html('Info<span class="caret"></span>');
	}else if(logGrade == 'Warn'){
		$("#button_logGrage").html('Warn<span class="caret"></span>');
	}else if(logGrade == 'Error'){
		$("#button_logGrage").html('Error<span class="caret"></span>');
	}else if(logGrade == 'Assert'){
		$("#button_logGrage").html('Assert<span class="caret"></span>');
	}
	show_logcatTable();
}

//捕获的关键日志:
var LOGS_TO_EXPORT = null;
function show_logcatTable(){	//创建表格，并加载卡顿数据

	$("#div_table_log").html(
		'<table id="table_log" class="display" style="WORD-BREAK: break-all" >'
			+'<thead>'
				+'<tr>'
					+'<th>时间</th>'
					+'<th>级别</th>'
					+'<th>标签</th>'
					+'<th>日志</th>'
				+'</tr>'
			+'</thead>'
		+'</table>'
	);
	
	var isUtest = false;
	if(document.getElementById("checkbox_isUtest").checked){
		isUtest = true;
	}
	var gradeName = document.getElementById('button_logGrage').innerHTML;
	var gradeFilter = "";
	if(gradeName.indexOf("Assert")!=-1){
		gradeFilter = 'Assert';
	}else if(gradeName.indexOf("Error")!=-1){
		gradeFilter = 'Error';
	}else if(gradeName.indexOf("Warn")!=-1){
		gradeFilter = 'Warn';
	}else if(gradeName.indexOf("Info")!=-1){
		gradeFilter = "Info";
	}else if(gradeName.indexOf("Debug")!=-1){
		gradeFilter = "Debug";
	}else if(gradeName.indexOf("Verbose")!=-1){
		gradeFilter = "Verbose";
	}
	
	var tagFilter = "";
	tagFilter = document.getElementById('tagFilter').value;
	
	
	
	var logData = getTableData_logcat(gradeFilter,tagFilter,isUtest);
	LOGS_TO_EXPORT = logData;
	
	var div_table = $('#table_log');
	if (div_table.hasClass('dataTable')) {
		var dttable = div_table.dataTable();
		dttable.fnClearTable(); //清空一下table
		dttable.fnDestroy(); //还原初始化了的datatable
	}
	var table_log =div_table.DataTable( {
		data: logData,
		//每列所对应的数据字段：
		"columns": [
			{ "data": "realTime" ,"width": "10%"},
			{ "data": "grade" ,"width": "5%"},
			{ "data": "tag" ,"width": "20%"},
			{ "data": "logContent" ,"width": "53%"}
		],
		"order": [[0, 'asc']],
		"aLengthMenu": [[50, 200, -1], ['50', '200', "所有"] ],// change per page values here
		"iDisplayLength": 50,
		//"bFilter": false,
		"oLanguage": {
		   "oAria": {
			   "sSortAscending": " - click/return to sort ascending",
			   "sSortDescending": " - click/return to sort descending"
		   },
		   "sLengthMenu": "显示 _MENU_ 记录",
		   "sZeroRecords": "对不起，查询不到任何相关数据",
		   "sEmptyTable": "未有相关数据",
		   "sLoadingRecords": "正在加载数据-请等待...",
		   "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录。",
		   "sInfoEmpty": "当前显示0到0条，共0条记录",
		   "sInfoFiltered": "（数据库中共为 _MAX_ 条记录）",
		   "sProcessing": "<img src='../resources/user_share/row_details/select2-spinner.gif'/> 正在加载数据...",
		   "sSearch": "模糊查询：",
		   "sUrl": "",
		   //多语言配置文件，可将oLanguage的设置放在一个txt文件中，例：Javascript/datatable/dtCH.txt
		   "oPaginate": {
			   "sFirst": "首页",
			   "sPrevious": " 上一页 ",
			   "sNext": " 下一页 ",
			   "sLast": " 尾页 "
		   }
	   },
	} );
}

function exportLog(){
	var logString = "";
	for(var i=0;LOGS_TO_EXPORT!=null &&　i<LOGS_TO_EXPORT.length;i++){
		var realTime = LOGS_TO_EXPORT[i].realTime;
		while(realTime.length<20){
			realTime = realTime +" ";
		}
		var log = LOGS_TO_EXPORT[i].time + "  " + realTime + "  " + LOGS_TO_EXPORT[i].grade + "  " + LOGS_TO_EXPORT[i].tag + "  " + LOGS_TO_EXPORT[i].logContent +"\r\n";
		logString  = logString + log;
	}
	var blob = new Blob([logString], {type: "text/plain;charset=utf-8"});
	saveAs(blob, "utest_log.txt");

}


















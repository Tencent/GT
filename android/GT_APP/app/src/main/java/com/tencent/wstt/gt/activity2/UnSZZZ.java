package com.tencent.wstt.gt.activity2;

/**
 * Created by p_hongjcong on 2017/8/31.
 */

public class UnSZZZ {



//    private String packageName = null;
//    private long startTestTime = -1;
//    private int pid = 0;
//    private synchronized void refreshChartData(){
//        if(packageName==null
//                || !packageName.equals(GTRAnalysis.packageName)
//                || startTestTime!=GTRAnalysis.startTestTime
//                || pid!=GTRAnalysis.pid){
//            //清空数据，读取所有数据，统一刷新
//            packageName = GTRAnalysis.packageName;
//            startTestTime = GTRAnalysis.startTestTime;
//            pid = GTRAnalysis.pid;
//            bufferedReader = initBufferedReader();
//            final ArrayList<DetailPointData> pointDatas = new ArrayList<>();
//            DetailPointData nextPoint = getNextPointData();
//            while(nextPoint!=null){
//                pointDatas.add(nextPoint);
//                nextPoint = getNextPointData();
//            }
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("Elvisffa","setDatas:"+pointDatas.size());
//                    scrollLineChartView.setDatas(pointDatas);
//                }
//            });
//        }else {
//            //继续读取数据，刷新数据
//            DetailPointData nextPoint = getNextPointData();
//            while(nextPoint!=null){
//                Log.e("Elvisffa","getNextPointData:");
//                final DetailPointData finalNextPoint = nextPoint;
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("Elvisffa","addData:"+finalNextPoint.y);
//                        scrollLineChartView.addData(finalNextPoint);
//                    }
//                });
//                nextPoint = getNextPointData();
//            }
//        }
//    }
//
//    long lastcpuApp_noGTR=-1;
//    long lastcpuTotal =-1;
//    long lastFlowTime = -1;
//    long lastFlow =-1;
//    ArrayList<Long> framesInOneSecond = new ArrayList<>();//最近一秒内所有frame的时间
//    private DetailPointData getNextPointData(){
//        if (bufferedReader==null){
//            bufferedReader = initBufferedReader();
//        }
//        if (bufferedReader!=null){
//            try{
//                String line =bufferedReader.readLine();
//                while (line!=null){
//                    String lines[] = line.split(GTRConfig.separator);
//                    Log.e("Elvis,","type:"+type);
//                    switch (type){
//                        case TYPE_CPU:
//                            if(lines[2].equals("normalCollect")){
//                                long time = Long.parseLong(lines[3]);
//                                long cpuTotal = Long.parseLong(lines[4]);
//                                long cpuApp = Long.parseLong(lines[5]);
//                                String cpuThreads = lines[6];
//                                String gtrThreads = lines[10];
//                                long cpuApp_noGTR = cpuApp;//去掉GTR影响的cpu
//                                String[] gtrThs = gtrThreads.split(",");
//                                String[] cpuThs = cpuThreads.split(",");
//                                for(String s :cpuThs){
//                                    String[] sdsd = s.split(":");
//                                    for(String temp:gtrThs){
//                                        if (sdsd[0].equals(temp)){
//                                            cpuApp_noGTR = cpuApp_noGTR - Integer.parseInt(sdsd[1]);
//                                        }
//                                    }
//                                }
//                                DetailPointData pointData = null;
//                                if(lastcpuApp_noGTR!=-1){
//                                    pointData = new DetailPointData((time-startTestTime)/1000,(cpuApp_noGTR-lastcpuApp_noGTR)*100/(cpuTotal-lastcpuTotal));
//                                }
//                                lastcpuApp_noGTR = cpuApp_noGTR;
//                                lastcpuTotal = cpuTotal;
//                                if (pointData!=null){
//                                    return pointData;
//                                }
//                            }
//                            break;
//                        case TYPE_Memory:
//                            if(lines.length>=3 && lines[2].equals("normalCollect")){
//                                long time = Long.parseLong(lines[3]);
//                                long memory = Integer.parseInt(lines[7])/1024;
//                                Log.e("Elvisffa","getMemory:"+memory);
//                                return new DetailPointData((time-startTestTime)/1000,memory);
//                            }
//                            break;
//                        case TYPE_Flow:
//                            if(lines.length>=3 && lines[2].equals("normalCollect")){
//                                long time = Long.parseLong(lines[3]);
//                                long flowUpload = Long.parseLong(lines[8]);
//                                long flowDownload = Long.parseLong(lines[9]);
//                                DetailPointData pointData = null;
//                                if(lastFlow !=-1){
//                                    pointData = new DetailPointData((time-startTestTime)/1000,((flowUpload+flowDownload)- lastFlow)/1024*1000/(time-lastFlowTime));
//                                }
//                                lastFlow = flowUpload+flowDownload;
//                                lastFlowTime = time;
//                                if(pointData!=null){
//                                    return pointData;
//                                }
//                            }
//                            break;
//                        case TYPE_SM:
//                            Log.e("Elvis,","sdasdasdadasdadasd"+lines.length);
//                            if(lines.length>=3 && lines[2].equals("frameCollect")){
//                                Log.e("Elvis,","frameCollect");
//                                long time = Long.parseLong(lines[3]);
//                                framesInOneSecond.add(time);
//                                for(int i = 0;i<framesInOneSecond.size();i++){
//                                    long tempFrameTime = framesInOneSecond.get(i);
//                                    if (tempFrameTime<time-1000) {
//                                        framesInOneSecond.remove(i);
//                                        i--;
//                                    }
//                                }
//                                return new DetailPointData(((time-startTestTime)/100)/(10.0),framesInOneSecond.size());
//                            }
//                            break;
//                    }
//                    line =bufferedReader.readLine();
//                }
//            }catch (Exception e){}
//        }
//        return null;
//    }
//
//    //读取下一行数据
//    BufferedReader bufferedReader;
//    public BufferedReader initBufferedReader(){
//
//        String dataFilePath = GTRServerSave.getSaveFilePath(packageName,startTestTime,pid);
//        File dataFile = new File(dataFilePath);
//        try{
//            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile),"utf-8"));
//        }catch (Exception e){
//            bufferedReader = null;
//        }
//
//        return bufferedReader;
//    }
//



}

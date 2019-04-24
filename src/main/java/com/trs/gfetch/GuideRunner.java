package com.trs.gfetch;

import com.trs.gfetch.common.GuideDispatch;
import com.trs.gfetch.entity.Task;
import com.trs.gfetch.utils.AsdlIp;
import com.trs.gfetch.utils.HttpDeal;
import com.trs.gfetch.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component//被spring容器管理
@Slf4j
public class GuideRunner implements ApplicationRunner {
    @Value("${mqurl}")
    private String mqurl; //获取数据地址
    @Value("${asdlName}")
    private String asdlName;//宽带连接名 aaa
    @Value("${asdlUser}")
    private String asdlUser;//拨号账号
    @Value("${asdlPwd}")
    private String asdlPwd;//拨号密码
    @Value("${timesQieIP}")
    private Integer timesQieIP;

    private int receiveLv = 0; //多次没有数据后,修改读取数据频率

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("-------------->" + "项目启动，now="+new Date());
        System.out.println("mqurl--->"+mqurl);
        while(true){
            try {
                receiveLv++;
                String json = HttpDeal.get(mqurl);
                log.info("json=="+json);
                if(json != null && json.length()>10){
                    Task task = (Task)JsonHelper.parseJson2Object(json,Task.class);
                    GuideDispatch.guideDisp(task);
                    receiveLv = 0;
                }
                if(receiveLv>50) Thread.sleep(1000l * 25);
                else Thread.sleep(1000l * 6);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("-------------->" + "项目启动，now="+new Date());
//        System.out.println("mqurl--->"+mqurl);
//        while(true){
//            try {
//                if(!qieip){ //切换ip失败的话,30s轮询一次尝试切ip,不再接受任务
//                    qieIP(); Thread.sleep(1000*30); break;
//                }
//                receiveLv++;
//                String json = HttpDeal.get(mqurl);
//                log.info("json=="+json);
//                if(json != null && json.length()>10){
//                    Task task = (Task)JsonHelper.parseJson2Object(json,Task.class);
//                    GuideDispatch.guideDisp(task);
//                    qieipMark++;
//                    receiveLv = 0;
//                }
//                if(qieipMark>=timesQieIP){//满足发帖次数后切换一次ip
//                    qieipMark = 0;
//                    qieIP();
//                }
//                if(receiveLv>100) Thread.sleep(1000l * 20);
//                else Thread.sleep(1000l * 2);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 切换ip
     */
//    public void qieIP(){
//        boolean cut = false;
//        boolean conn = false;
//        try {
//            Thread.sleep(500);
//            for(int i=0;i<5;i++){
//                boolean has = AsdlIp.cutAdsl(asdlName);
//                if(has){
//                    log.info("cut成功!");
//                    cut = true;
//                    break;
//                }
//                Thread.sleep(1000);
//            }
//            for(int i=0;i<5;i++){
//                boolean cnt = AsdlIp.connAdsl(asdlName,asdlUser,asdlPwd);
//                if(cnt){
//                    log.info("conn成功!");
//                    conn = true;
//                    break;
//                }
//            }
//            Thread.sleep(5 * 1000l);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if(cut && conn) qieip = true;
//        else qieip = false;
//    }
}

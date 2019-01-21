package com.trs.gfetch.guidescript;

import com.trs.gfetch.common.GuideAbstract;
import com.trs.gfetch.entity.Task;

public class SinaNewsCommentDigg extends GuideAbstract {

    public static void main(String args[]){
        Task task = new Task();
        task.setAddress("http://bj.jjj.qq.com/a/20181114/002675.htm");

        task.setAccount("2598532239");
        task.setPassword("4211432a");
        task.setCorpus("果然厉害!!!");

        task.setAccount("502023904");
        task.setPassword("lilei516688");
    }

    @Override
    public void start(Task task) {
        //初始化一些参数
        init(task);
        run();
    }

    @Override
    public void run() {

    }


}

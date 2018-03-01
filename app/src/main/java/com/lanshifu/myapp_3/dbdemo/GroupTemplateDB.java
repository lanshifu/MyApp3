package com.lanshifu.myapp_3.dbdemo;

/**
 * 群聊模板
 * Created by lanxiaobin on 2017/10/11.
 */

public class GroupTemplateDB {

    private int id;
    private int count;
    private String member;//,成员逗号隔开
    private String name;
    private String thread_id;

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}

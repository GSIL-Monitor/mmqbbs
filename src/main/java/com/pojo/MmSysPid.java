package com.pojo;

import java.util.Date;

public class MmSysPid {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String pid;

    /**
     * 使用时间
     */
    private Date pidtime;

    /**
     * 
     */
    private String gzhname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Date getPidtime() {
        return pidtime;
    }

    public void setPidtime(Date pidtime) {
        this.pidtime = pidtime;
    }

    public String getGzhname() {
        return gzhname;
    }

    public void setGzhname(String gzhname) {
        this.gzhname = gzhname;
    }
}
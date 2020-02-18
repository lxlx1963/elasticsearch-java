package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/9 10:19
 */
public class SingleDeviceTimeAgeDTO {
    /**
     * 时间
     */
    private String time;
    /**
     * 年龄
     */
    private String age;
    /**
     * 人数
     */
    private Integer peopleNum;
    /**
     * 人次
     */
    private Integer peopleTime;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public Integer getPeopleTime() {
        return peopleTime;
    }

    public void setPeopleTime(Integer peopleTime) {
        this.peopleTime = peopleTime;
    }
}

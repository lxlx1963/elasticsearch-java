package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/9 10:29
 */
public class SingleDeviceTimeDTO {
    /**
     * 时间
     */
    private String time;
    /**
     * 人数
     */
    private Integer peopleNumSum;
    /**
     * 人次
     */
    private Integer peopleTimeSum;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getPeopleNumSum() {
        return peopleNumSum;
    }

    public void setPeopleNumSum(Integer peopleNumSum) {
        this.peopleNumSum = peopleNumSum;
    }

    public Integer getPeopleTimeSum() {
        return peopleTimeSum;
    }

    public void setPeopleTimeSum(Integer peopleTimeSum) {
        this.peopleTimeSum = peopleTimeSum;
    }
}

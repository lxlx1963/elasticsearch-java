package com.du.elasticsearch.model.vo;

/**
 * @author dxy
 * @date 2019/3/8 21:15
 */
public class SingleDeviceTimeSexVO {
    /**
     * 人数
     */
    private Integer peopleNumSum;
    /**
     * 人次
     */
    private Integer peopleTimeSum;
    /**
     * 男性人数
     */
    private Integer malePeopleNumSum;
    /**
     * 男性人次
     */
    private Integer malePeopleTimeSum;
    /**
     * 女性人数
     */
    private Integer femalePeopleNumSum;
    /**
     * 女性人次
     */
    private Integer femalePeopleTimeSum;

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

    public Integer getMalePeopleNumSum() {
        return malePeopleNumSum;
    }

    public void setMalePeopleNumSum(Integer malePeopleNumSum) {
        this.malePeopleNumSum = malePeopleNumSum;
    }

    public Integer getMalePeopleTimeSum() {
        return malePeopleTimeSum;
    }

    public void setMalePeopleTimeSum(Integer malePeopleTimeSum) {
        this.malePeopleTimeSum = malePeopleTimeSum;
    }

    public Integer getFemalePeopleNumSum() {
        return femalePeopleNumSum;
    }

    public void setFemalePeopleNumSum(Integer femalePeopleNumSum) {
        this.femalePeopleNumSum = femalePeopleNumSum;
    }

    public Integer getFemalePeopleTimeSum() {
        return femalePeopleTimeSum;
    }

    public void setFemalePeopleTimeSum(Integer femalePeopleTimeSum) {
        this.femalePeopleTimeSum = femalePeopleTimeSum;
    }
}

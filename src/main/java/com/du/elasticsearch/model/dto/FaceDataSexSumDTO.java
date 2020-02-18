package com.du.elasticsearch.model.dto;

/**
 * @author dxy
 * @date 2019/3/9 15:57
 */
public class  FaceDataSexSumDTO {
    /**
     * 性别
     */
    private String sex;
    /**
     * 人数总数
     */
    private Integer peopleNumSum;
    /**
     * 人次总数
     */
    private Integer peopleTimeSum;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

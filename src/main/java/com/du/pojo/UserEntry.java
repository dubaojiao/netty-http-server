package com.du.pojo;

/**
 * @Title
 * @ClassName UserEntry
 * @Author jsb_pbk
 * @Date 2018/10/1
 */
public class UserEntry {
    private Integer age;
    private String name;
    private String phone;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserEntry{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

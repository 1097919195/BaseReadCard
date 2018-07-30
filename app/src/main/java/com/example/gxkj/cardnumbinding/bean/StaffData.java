package com.example.gxkj.cardnumbinding.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/26 0026.
 */

public class StaffData {

    /**
     * _id : 5b08e5a49134ca0385715dc3
     * name : 张三
     * department : 中
     * gender : 1
     * mobile : 15988254531
     * position : 中
     * email : null
     * num : 110
     * qrcode : https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQE98TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyWUZ5RTk3c3VkVGsxMDAwMGcwN2kAAgSk5QhbAwQAAAAA
     * qrcode_content : http://weixin.qq.com/q/02YFyE97sudTk10000g07i
     * qrcode_with_name : third_staff_qrcodes/615db57aa314529aaa0fbe95b3e95bd3-92k8q2.jpeg
     */

    private String _id;
    private String name;//名字
    private String department;//部门
    private int gender;//性别 1男2女
    private String mobile;//手机
    private String position;//职位
    private String email;//邮箱
    private String num;//编号
    private String avatar;//头像图片地址   这里面不是一个数组不能用集合表示

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public static class Avatar {
        private String image_path;
        private String relative_image_path;

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }

        public String getRelative_image_path() {
            return relative_image_path;
        }

        public void setRelative_image_path(String relative_image_path) {
            this.relative_image_path = relative_image_path;
        }
    }
}

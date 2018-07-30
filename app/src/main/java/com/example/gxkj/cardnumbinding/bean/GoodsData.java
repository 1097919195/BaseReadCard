package com.example.gxkj.cardnumbinding.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/7/26 0026.
 */

public class GoodsData {
    /**
     * _id : 5b5153de9134ca1bb71e51d2
     * mtm_id : 5b4eb05e9134ca4e520acb52
     * name : 5
     * num : 5
     * remark : 5
     * updated_at : 2018-07-20 11:15:43
     * created_at : 2018-07-20 11:15:42
     * qrcode : http://ts.npclo.com/images/goods/5b5153de9134ca1bb71e51d2.png
     * qrcode_content : http://weixin.qq.com/q/02FThs8XsudTk10000M07q
     * qrcode_with_name : /images/goods/5b5153de9134ca1bb71e51d2.png
     */

    private String _id;
    private String mtm_id;
    private String name;
    private String num;
    private String remark;
    private String qrcode;
    private String qrcode_content;
    private String qrcode_with_name;
    private List<ImagesBean> images;

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMtm_id() {
        return mtm_id;
    }

    public void setMtm_id(String mtm_id) {
        this.mtm_id = mtm_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQrcode_content() {
        return qrcode_content;
    }

    public void setQrcode_content(String qrcode_content) {
        this.qrcode_content = qrcode_content;
    }

    public String getQrcode_with_name() {
        return qrcode_with_name;
    }

    public void setQrcode_with_name(String qrcode_with_name) {
        this.qrcode_with_name = qrcode_with_name;
    }

    public static class ImagesBean{
        private String path;
        private String relative_path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getRelative_path() {
            return relative_path;
        }

        public void setRelative_path(String relative_path) {
            this.relative_path = relative_path;
        }
    }
}

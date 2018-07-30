package com.example.gxkj.cardnumbinding.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/7/30 0030.
 */

public class StoreData {


    /**
     * _id : 5b5eb0029134ca75fa646b72
     * name : JJ
     * images : [{"path":"https://ts.npclo.com/images/goods/goods_1532932887_NvawgTHxqg.jpg","relative_path":"/images/goods/goods_1532932887_NvawgTHxqg.jpg"},{"path":"https://ts.npclo.com/images/goods/goods_1532932887_zTS6oN8svj.jpg","relative_path":"/images/goods/goods_1532932887_zTS6oN8svj.jpg"}]
     */

    private String _id;
    private String name;
    private boolean isSelected;
//    private List<ImagesBean> images;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

//    public List<ImagesBean> getImages() {
//        return images;
//    }
//
//    public void setImages(List<ImagesBean> images) {
//        this.images = images;
//    }

    public static class ImagesBean {
        /**
         * path : https://ts.npclo.com/images/goods/goods_1532932887_NvawgTHxqg.jpg
         * relative_path : /images/goods/goods_1532932887_NvawgTHxqg.jpg
         */

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

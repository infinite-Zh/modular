package com.infinite.annotation;

import javax.lang.model.element.Element;

public class RouterBean {

    private RouterBean(Builder builder){
        this.group=builder.group;
        this.path=builder.path;
        this.element=builder.element;
    }

    private RouterBean(Type type, Class<?> clazz, String group, String path) {
        this.type=type;
        this.clazz=clazz;
        this.group=group;
        this.path=path;
    }

    public static RouterBean create(Type type,Class<?> clazz,String group,String path){
        return new RouterBean(type,clazz,group,path);
    }

    public enum Type{
        ACTIVITY,
        CALL
    }

    private Type type;

    private Element element;

    //Arouter注解的类对象
    private Class<?> clazz;

    @Override
    public String toString() {
        return "RouterBean{" +
                "type=" + type +
                ", element=" + element +
                ", clazz=" + clazz +
                ", group='" + group + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 组名
    private String group;

    //path
    private String path;

    public Type getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }

    public final static class Builder{
        private Element element;

        // 组名
        private String group;
        //path

        private String path;


        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public RouterBean build(){
            if (path==null||path.length()==0){
                throw new IllegalArgumentException("path illegal");
            }
            return new RouterBean(this);
        }

    }
}

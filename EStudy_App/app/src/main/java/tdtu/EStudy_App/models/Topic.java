package tdtu.EStudy_App.models;


import com.google.firebase.Timestamp;

import java.time.LocalDateTime;

public class Topic {
    private String id;
    private String name;
    private String status;
    private String userid;
    private Timestamp createTime;
    private int numWord;

    public int getNumWord() {
        return numWord;
    }

    public void setNumWord(int numWord) {
        this.numWord = numWord;
    }

    public Topic(String id, String name, String status, String userid, Timestamp createTime, int numWord) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.userid = userid;
        this.createTime = createTime;
        this.numWord = numWord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

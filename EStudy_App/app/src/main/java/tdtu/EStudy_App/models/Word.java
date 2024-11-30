package tdtu.EStudy_App.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Word implements Parcelable {
    private int id;
    private String name;
    private String meaning;
    private String pronunciation;
    private boolean isMarked;
    private String topic;
    private String state;
    public Word() {
    }


    protected Word(Parcel in) {
        id = in.readInt();
        name = in.readString();
        meaning = in.readString();
        pronunciation = in.readString();
        isMarked = in.readByte() != 0;
        topic = in.readString();
        state = in.readString();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(meaning);
        parcel.writeString(pronunciation);
        parcel.writeByte((byte) (isMarked ? 1 : 0));
        parcel.writeString(topic);
        parcel.writeString(state);
    }
}

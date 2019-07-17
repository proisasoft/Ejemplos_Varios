package com.mobile.proisa.fotosvehiculoslibrary.Clases;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuOption implements Parcelable{
    private String title;
    private String description;
    private int id;
    private int resource;

    public MenuOption(String title, int id, int resource) {
        this.title = title;
        this.id = id;
        this.resource = resource;
    }

    public MenuOption(String title, String description, int id, int resource) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.resource = resource;
    }

    protected MenuOption(Parcel in) {
        title = in.readString();
        description = in.readString();
        id = in.readInt();
        resource = in.readInt();
    }

    public static final Creator<MenuOption> CREATOR = new Creator<MenuOption>() {
        @Override
        public MenuOption createFromParcel(Parcel in) {
            return new MenuOption(in);
        }

        @Override
        public MenuOption[] newArray(int size) {
            return new MenuOption[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("title=%s, id=%d",getTitle(),getId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(id);
        parcel.writeInt(resource);
    }
}
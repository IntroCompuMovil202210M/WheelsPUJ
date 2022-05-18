package Models;

public class Message {
    private String name;
    private String imageLocation;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message(String name, String imageLocation, String content) {
        this.name=name;
        this.imageLocation=imageLocation;
        this.content=content;
    }
}

package tournesol.models;

public class Champion {
    private String name;
    private String subtitle;
    private String positionUrl;
    private String imageUrl;
    private Ability passive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPositionUrl() {
        return positionUrl;
    }

    public void setPositionUrl(String positionUrl) {
        this.positionUrl = positionUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Ability getPassive() {
        return passive;
    }

    public void setPassive(Ability passive) {
        this.passive = passive;
    }
}

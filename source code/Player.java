import java.io.Serializable;

public class Player implements Serializable {
    String username;
    String mark;
    int points;
    boolean isPlaying;
    boolean active;

    public Player(String username, String mark) {
        this.username = username;
        this.mark = mark;
        this.isPlaying = false;
        this.points = 0;
        this.active = false;
    }
}

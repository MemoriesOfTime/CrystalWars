package cn.lanink.bedwars.utils.scoreboard.ltname;

/**
 * @author lt_name
 */
public class ScoreboardData {

    public enum DisplaySlot{
        LIST,
        SIDEBAR,
        BELOWNAME
    }

    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

    public static class ScoreboardLine {

        private String message;
        private int score;

        public ScoreboardLine(String message, int score) {
            this.message = message;
            this.score = score;
        }

        public String getMessage() {
            return this.message;
        }

        public int getScore() {
            return this.score;
        }

    }

}

package s1finalproject;

public class Result {
    public String username;
    public int score;
    public double accuracy;
    public double wps;
    public String date;

    public Result(String username, int score, double accuracy, double wps, String date) {
        this.username = username;
        this.score = score;
        this.accuracy = accuracy;
        this.wps = wps;
        this.date = date;
    }

    public int getScore() { return score; }
    public double getAccuracy() { return accuracy; }
    public double getWps() { return wps; }
}
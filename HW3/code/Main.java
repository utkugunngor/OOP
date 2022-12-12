import java.io.IOException;
public class Main{
    public static void main(String[] args) throws IOException{
        SIS informationSystem = new SIS();
        informationSystem.createTranscript(2534723);
        informationSystem.findCourse(6390101);
        informationSystem.createHistogram(6390101, 20101);
    }

}

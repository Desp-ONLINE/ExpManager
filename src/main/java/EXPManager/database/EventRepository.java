package EXPManager.database;

import lombok.Getter;
import lombok.Setter;

public class EventRepository {

    private static EventRepository instance;

    @Getter@Setter
    private static int duration;
    @Getter@Setter
    private static int multiply;

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    public void startEvent(int multiply, int duration){
        this.duration = duration;
        this.multiply = multiply;
    }


    public void reduceDuration() {
        duration-=1;
    }
    public void stopEvent(){
        multiply=0;
    }
}

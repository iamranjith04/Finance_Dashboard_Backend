package backend.dto;

import java.time.LocalDateTime;

public class AddRecordRequest {
    public String AccountNo;
    public float Amount;
    public String Type;
    LocalDateTime Timestamp;
    public String Category;
    public String Description;

    public LocalDateTime getTimestamp(){
        if(Timestamp==null){
            return LocalDateTime.now();
        }
        return Timestamp;
    }
}

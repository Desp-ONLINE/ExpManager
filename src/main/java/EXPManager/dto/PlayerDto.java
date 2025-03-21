package EXPManager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PlayerDto {
    private String user_id;
    private String uuid;
    private int leftDuration;
    private int multiply;
    private String latestUsedItem;
}

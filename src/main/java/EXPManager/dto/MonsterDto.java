package EXPManager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MonsterDto {
    private String mythicMobId;
    private int exp;
}

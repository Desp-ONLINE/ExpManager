package EXPManager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ElixirDto {
    private String mmoItemId;
    private int duration;
    private int multiply;
}

package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterContentPojo {
    @JsonProperty("timestamp")
    private int timestamp;

    @JsonProperty("id")
    private int id;

    @JsonProperty("value")
    private float value;
}

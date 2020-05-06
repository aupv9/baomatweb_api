package ws_api.demo_api_ws.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "codeconfirm")
@Data
public class CodeConfirm {
    private String id;
    private String code;
    private String time;

}

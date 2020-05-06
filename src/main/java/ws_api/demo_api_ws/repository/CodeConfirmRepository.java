package ws_api.demo_api_ws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ws_api.demo_api_ws.document.CodeConfirm;

public interface CodeConfirmRepository extends MongoRepository<CodeConfirm,String> {
    CodeConfirm findByCode(String code);
}

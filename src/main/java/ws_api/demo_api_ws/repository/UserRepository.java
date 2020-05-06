package ws_api.demo_api_ws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ws_api.demo_api_ws.document.User;

public interface UserRepository extends MongoRepository<User,String> {
    User findUserByEmail(String email);
    User findUserById(String id);
    User findUserByCodeSendEmail(String code);
}

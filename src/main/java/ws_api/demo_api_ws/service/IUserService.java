package ws_api.demo_api_ws.service;

import ws_api.demo_api_ws.document.LoginEntity;
import ws_api.demo_api_ws.document.User;

import java.util.List;

public interface IUserService {
    List<User> findAllUser();
    boolean insertUser(User user);
    User loginUser(LoginEntity email);
    boolean updateUser(User user);
    User loadUserByEmail(String email);
    boolean deleteUser(String id);
    boolean editRoleUser(String id,String role);
    boolean updateActive(String id);
    boolean sendCodeToEmail(String email);
    boolean insertUserByAdmin(User user);
    User getUserByEmail(String email);
}

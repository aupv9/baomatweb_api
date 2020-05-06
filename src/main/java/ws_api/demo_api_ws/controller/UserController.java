package ws_api.demo_api_ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ws_api.demo_api_ws.document.LoginEntity;
import ws_api.demo_api_ws.document.User;
import ws_api.demo_api_ws.service.JwtService;
import ws_api.demo_api_ws.service.UserService;

import java.util.List;

@RestController
public class UserController {

    UserService userService;
    JwtService jwtService;
    @Autowired
    public UserController(UserService userService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    //Admin function view all member
    @GetMapping(value = "/v1/admin/users",produces = "application/json")
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<>(this.userService.findAllUser(), HttpStatus.OK);
    }
    //guess function register
    @PostMapping(value = "/v1/users",produces = "application/json")
    public ResponseEntity<Boolean> insertUser(@RequestBody User user){
        return  this.userService.insertUser(user)?
                new ResponseEntity<>(true,HttpStatus.OK) :
                new ResponseEntity<>(false,HttpStatus.NOT_IMPLEMENTED);
    }
    //admin function add member
    @PostMapping(value = "/v1/admin/users",produces = "application/json")
    public ResponseEntity<Boolean> insertUserByAdmin(@RequestBody User user){
        return  this.userService.insertUserByAdmin(user)?
                new ResponseEntity<>(true,HttpStatus.OK) :
                new ResponseEntity<>(false,HttpStatus.NOT_IMPLEMENTED);
    }

    /*
    * Login user
    * @param LoginEntity(email,pass)
    * return jwt
    * */
    @PostMapping(value = "/v1/users-management",produces = "application/json")
    public ResponseEntity<String> loginUser(@RequestBody LoginEntity user){
        HttpStatus httpStatus = null;
        HttpHeaders httpHeaders=new HttpHeaders();
        String result="";
        User nguoiDung=this.userService.loginUser(user);
        try {
            if (!nguoiDung.getId().isEmpty()) {
                result=jwtService.generateTokenLogin(user.getEmail());
                httpStatus = HttpStatus.OK;
            } else {
                result="Wrong userId or password";
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception ex) {
            result="Server Error";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<String>(result,httpHeaders, httpStatus);
    }

    //Send code to email confirm register
    @PostMapping("/v1/users-code")
    public ResponseEntity<Boolean> sendCodeToEmail(@RequestBody User user){
        return this.userService.sendCodeToEmail(user.getEmail())? new ResponseEntity<>(true,HttpStatus.OK):
                new ResponseEntity<>(false,HttpStatus.NOT_MODIFIED);
    }

    @GetMapping(value = "/v1/users/{code}",produces = "application/json")
    public ResponseEntity<Boolean> getUser(@PathVariable("code") String codeMail){
        return this.userService.updateActive(codeMail) ?
                new ResponseEntity<>(true, HttpStatus.OK)
                :new ResponseEntity<>(false,HttpStatus.NOT_MODIFIED);
    }

    /*
    * Edit user
    * @param User
    * */
    @PutMapping(value = "/v1/admin/users",produces = "application/json")
    public boolean editUser(@RequestBody User user){
        return this.userService.updateUser(user);
    }

    @DeleteMapping(value = "/v1/admin/users/{id}",produces = "application/json")
    public ResponseEntity<Boolean> deleteUser(@PathVariable("id")String id){
        return this.userService.deleteUser(id) ? new ResponseEntity<>(true,HttpStatus.OK)
                :new ResponseEntity<>(false,HttpStatus.NOT_MODIFIED);
    }



}

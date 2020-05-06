package ws_api.demo_api_ws.service;

import com.mongodb.MongoException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ws_api.demo_api_ws.document.CodeConfirm;
import ws_api.demo_api_ws.document.LoginEntity;
import ws_api.demo_api_ws.document.User;
import ws_api.demo_api_ws.repository.CodeConfirmRepository;
import ws_api.demo_api_ws.repository.UserRepository;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService{

    private static final Log log = LogFactory.getLog(UserService.class);

    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    MailService mailService;
    CodeConfirmRepository codeConfirmRepository;
    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       MailService mailService,
                       CodeConfirmRepository codeConfirmRepository) {
        this.userRepository=userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.mailService = mailService;
        this.codeConfirmRepository = codeConfirmRepository;
    }


    @Override
    public List<User> findAllUser() {
        return this.userRepository.findAll();
    }

    @Override
    public boolean insertUser(User user) {
        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new ArrayList<String>(){{add("ROLE_USER");}});
        try {
//            CodeConfirm codeConfirm=this.codeConfirmRepository.findByCode(user.getCodeSendEmail());
//            if(codeConfirm != null){
//                long diffInSeconds = Duration.between(LocalDateTime.parse(codeConfirm.getTime()),LocalDateTime.now()
//                        ).getSeconds();
//                System.out.println(diffInSeconds);
//                if(diffInSeconds <= 60){
//                    this.userRepository.save(user);
//                    return true;
//                }else{
//                    return false;
//                }
//            }
            this.userRepository.save(user);
        }catch (MongoException  mongoException){
            return false;
        }
        return false;
    }

   @Override
    public User loginUser(LoginEntity email) {
        User userInDB =  this.userRepository.findUserByEmail(email.getEmail());
        if(userInDB != null && this.bCryptPasswordEncoder.matches(email.getPassword(),userInDB.getPassword())){
            return userInDB;
        }
        return new User();
    }

    @Override
    public boolean updateUser(User user) {
        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
        return true;
    }

    @Override
    public User loadUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    @Override
    public boolean deleteUser(String id) {
        User user = this.userRepository.findUserById(id);
        user.setActive(false);
        this.userRepository.save(user);
        return true;
    }

    @Override
    public boolean editRoleUser(String id, String role) {
        return false;
    }

    @Override
    public boolean updateActive(String code) {
        try {
            // Search User by code send from email
            User user = this.userRepository.findUserByCodeSendEmail(code);
            if(user != null){
                user.setActive(!user.isActive());
                try {
                    // update field active == true
                    this.userRepository.save(user);
                    return true;

                }catch (MongoException mongoException){
                    log.error("Update erorr"+ mongoException,mongoException);
                }
            }
        }catch (MongoException mongoException){
            log.error("Find User Erorr  "+mongoException,mongoException);
            return false;
        }
        return false;
    }

    @Override
    public boolean sendCodeToEmail(String email) {
        int length = 6;
        String generatedString = RandomStringUtils.random(length, true, false);
        CodeConfirm codeConfirm=new CodeConfirm();
        try {
            //Create object code send to email and insert to database
            codeConfirm.setCode(generatedString);
            LocalDateTime dateCreate=LocalDateTime.now();
            codeConfirm.setTime(dateCreate.toString());
            if(this.mailService.sendEmail(email,generatedString)){
                //insert to database
                this.codeConfirmRepository.save(codeConfirm);
                return true;
            }
            return false;
        } catch (MongoException | MessagingException e) {
            e.printStackTrace();
            log.error("Error",e);
            return false;
        }
    }

    @Override
    public boolean insertUserByAdmin(User user) {
        try {
            user.setCodeSendEmail(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setActive(true);
            this.userRepository.save(user);
            return true;
        }catch (MongoException mongoException){
            log.error("Exception",mongoException);
            mongoException.getStackTrace();
        }
        return false;
    }

    @Override
    public User getUserByEmail(String email) {

        return null;
    }


}

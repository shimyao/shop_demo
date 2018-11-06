package com.service.impl;

import com.Utils.MD5Utils;
import com.Utils.TokenCache;
import com.common.Const;
import com.common.ServerResponse;
import com.dao.UserInfoMapper;
import com.pojo.UserInfo;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class UserServiceImpl implements IUserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    public ServerResponse login(String username, String password) {
        //1.参数的非空校验
        if(username==null|| username.equals(" ")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        if(password==null|| password.equals(" ")){
            return ServerResponse.serverResponseByError("密码不能为空");
        }
        //2.检查用户名是否存在
       int result = userInfoMapper.checkUsername(username);
        if (result==0){
            return ServerResponse.serverResponseByError("用户名不存在");
        }
        //3.根据用户名和密码查找用户信息
         UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(password));
        if (userInfo==null){
            return ServerResponse.serverResponseByError("密码错误");
        }
        //4.返回结果
        userInfo.setPassword("");
        return ServerResponse.serverResponseBySuccess(userInfo);
    }

    @Override
    public ServerResponse register(UserInfo userInfo) {

        //1.参数的非空校验
          if (userInfo==null){
              return ServerResponse.serverResponseByError("参数必需");
          }
        //2.校验用户名是否存在
        int result = userInfoMapper.checkUsername(userInfo.getUsername());
        if (result>0){
            return ServerResponse.serverResponseByError("用户名已经存在");
        }
        //3.校验邮箱
        int result_email = userInfoMapper.checkEmail(userInfo.getEmail());
        if (result_email>0){
            //邮箱存在
            return ServerResponse.serverResponseByError("邮箱已存在");
        }
        //4.注册
        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
         int count = userInfoMapper.insert(userInfo);
          if (count>0){
              return ServerResponse.serverResponseBySuccess("注册成功");
          }
        //5.返回结果
        return ServerResponse.serverResponseByError("注册失败");
    }

    @Override
    public ServerResponse forget_get_question(String username) {
        //1.参数校验
          if (username==null||username.equals(" ")){
              return ServerResponse.serverResponseByError("用户名不能为空");
          }
        //2.校验用户名
         int result = userInfoMapper.checkUsername(username);
          if (result==0){
              //用户名不存在
              return ServerResponse.serverResponseByError("用户名不存在，请重新输入");
          }
        //3.查找密保问题
        String question = userInfoMapper.selectQuestionByUsername(username);
        if (question==null||question.equals(" ")){
            return ServerResponse.serverResponseByError("密保问题为空");
        }
        return ServerResponse.serverResponseBySuccess(question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {
        //1.参数校验
        if (username==null||username.equals(" ")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        if (question==null||question.equals(" ")){
            return ServerResponse.serverResponseByError("密保问题不能为空");
        }
        if (answer==null||answer.equals(" ")){
            return ServerResponse.serverResponseByError("答案不能为空");
        }
        //2.根据用户名，问题，答案查询
          int result = userInfoMapper.selectByUsernameAndQuestionAndAnswer(username,question,answer);
          if (result==0){
              //答案错误
              return ServerResponse.serverResponseByError("答案错误");
          }
        //3.服务端生成一个token保存并将token返回到客户端
         String forgetToken = UUID.randomUUID().toString();
          //guava cache缓存
         TokenCache.set(username,forgetToken);
         return ServerResponse.serverResponseBySuccess(forgetToken);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String passwordNew, String forgetToken) {
        //1.参数校验
        if (username==null||username.equals(" ")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        if (passwordNew==null||passwordNew.equals(" ")){
            return ServerResponse.serverResponseByError("密码不能为空");
        }
        if (forgetToken==null||forgetToken.equals(" ")){
            return ServerResponse.serverResponseByError("token不能为空");
        }
        //2.token校验
        String token = TokenCache.get(username);
        if (token==null){
            return ServerResponse.serverResponseByError("token过期");
        }
        if (!token.equals(forgetToken)){
            return ServerResponse.serverResponseByError("无效的token");
        }
        //3.修改密码
        int result = userInfoMapper.updateUserPassword(username,MD5Utils.getMD5Code(passwordNew));
        if (result>0){
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("密码修改失败");
    }

    @Override
    public ServerResponse check_valid(String str, String type) {
        //1.参数校验
        if (str==null||str.equals(" ")){
            return ServerResponse.serverResponseByError("用户名或邮箱不能为空");
        }
        if (type==null||type.equals(" ")){
            return ServerResponse.serverResponseByError("校验类型参数不能为空");
        }
        //2.type：username-->校验用户名str
        //type:email -->校验邮箱str
         if (type.equals("username")){
            int result = userInfoMapper.checkUsername(str);
            if (result>0){
                //用户存在
                return ServerResponse.serverResponseByError("用户名已存在");
            }else {
                return  ServerResponse.serverResponseBySuccess();
            }
         }else if (type.equals("email")){
            int result = userInfoMapper.checkEmail(str);
             if (result>0){
                 //邮箱已存在
                 return ServerResponse.serverResponseByError("邮箱已存在");
             }else {
                 return  ServerResponse.serverResponseBySuccess();
             }
         }else {
            return ServerResponse.serverResponseByError("参数类型错误");
         }
        //3.返回结果

    }

    @Override
    public ServerResponse reset_password(String username,String passwordOld, String passwordNew) {
        //1.参数非空校验
        if (passwordOld==null||passwordOld.equals(" ")){
            return ServerResponse.serverResponseByError("用户旧密码不能为空");
        }
        if (passwordNew==null||passwordNew.equals(" ")){
            return ServerResponse.serverResponseByError("新密码不能为空");
        }
        //2.根据用户名和旧密码查询
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(passwordOld));
        if (userInfo==null){
            return ServerResponse.serverResponseByError("旧密码错误");
        }
        //3.修改密码
        userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
        int result =  userInfoMapper.updateByPrimaryKey(userInfo);
         if (result>0){
             return ServerResponse.serverResponseBySuccess();
         }
        return ServerResponse.serverResponseByError("密码修改失败");
    }

    @Override
    public ServerResponse update_information(UserInfo user) {
       //1.参数校验
        if (user==null){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        //2.更新用户信息
        int result = userInfoMapper.updateUserBySelectActive(user);
        if (result>0){
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("更新个人信息失败");
    }

    @Override
    public UserInfo findUserInfoByUserid(Integer userId) {
        return userInfoMapper.selectByPrimaryKey(userId);
    }


}

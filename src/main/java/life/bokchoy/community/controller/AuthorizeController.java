package life.bokchoy.community.controller;

import life.bokchoy.community.Provider.GithubProvider;
import life.bokchoy.community.dto.AccessTokenDTO;
import life.bokchoy.community.dto.GithubUser;
import life.bokchoy.community.mapper.UserMapper;
import life.bokchoy.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired//将该变量加入上下文中
    private GithubProvider githubprovider;


    @Autowired
    private UserMapper userMapper;

    @Value("${github.client.id}")//赋值注解将配置文件里面的对应名称后的值赋值给clientid
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")//处理callback页面资源即处理http://localhost:8887/callback，与登录界面的输入参数相同
    public String callback(@RequestParam(name = "code") String code,//获取返回的参数
                           @RequestParam(name = "state",defaultValue = "authorization_code") String state,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setGrant_type(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = githubprovider.getAccessToken(accessTokenDTO);//POST请求
        GithubUser githubUser = githubprovider.getUser(accessToken);
        //上面与github服务器交互
        System.out.println(githubUser.getName());
        if(githubUser!=null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userMapper.insert(user);
            //登陆成功写cookie和session
            response.addCookie(new Cookie("token",token));//token 写入token

//            request.getSession().setAttribute("user",githubUser);//在indexController设置
            return "redirect:/";//重定向

        }else{
            //登陆失败
            return "redirect:/";
        }

//        return "index";//还是跳转到index
    }
}

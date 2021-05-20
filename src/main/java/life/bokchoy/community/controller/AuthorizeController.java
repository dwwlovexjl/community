package life.bokchoy.community.controller;

import life.bokchoy.community.controller.Provider.GithubProvider;
import life.bokchoy.community.controller.dto.AccessTokenDTO;
import life.bokchoy.community.controller.dto.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

    @Autowired//将该变量加入上下文中
    private GithubProvider githubprovider;

    @Value("${github.client.id}")//赋值注解将配置文件里面的对应名称后的值赋值给clientid
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")//处理callback页面资源即处理http://localhost:8887/callback，与登录界面的输入参数相同
    public String callback(@RequestParam(name = "code") String code,//获取返回的参数
                           @RequestParam(name = "state") String state){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        String accessToken = githubprovider.getAccessToken(accessTokenDTO);//POST请求
        GithubUser user = githubprovider.getUser(accessToken);
        System.out.println(user.getName());
        return "index";//还是跳转到index
    }
}

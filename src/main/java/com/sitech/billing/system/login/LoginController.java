package com.sitech.billing.system.login;

import com.sitech.billing.common.bean.JsonResult;
import com.sitech.billing.common.enums.ErrorMsgEnum;
import com.sitech.billing.common.exception.IopException;
import com.sitech.billing.system.base.BaseController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static long TIMEOUT = 30 * 60 * 1000;

    @GetMapping("/login")
    public Object login(HttpServletRequest request) {
        Session session = SecurityUtils.getSubject().getSession();
        Object obj = session.getAttribute(DefaultSubjectContext.AUTHENTICATED_SESSION_KEY);
        if (obj != null) {
            return new ModelAndView("redirect:/");
        } else {
            String requestType = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equalsIgnoreCase(requestType)) {
                return JsonResult.error(ErrorMsgEnum.LOGIN_TIMEOUT);
            } else {
                return new ModelAndView("login/login");
            }
        }
    }

    @GetMapping("/loginout")
    public ModelAndView loginOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new ModelAndView("login/login");
    }

    @GetMapping("/sublogin")
    public ModelAndView sublogin(String username, String password, HttpServletRequest request) {

        if ("".equals(username) || "".equals(password)) {
            throw new IopException(ErrorMsgEnum.USERNAME_OR_PASSWORD_IS_EMPTY);
        }

        Subject subject = SecurityUtils.getSubject();
        SecurityUtils.getSubject().getSession().setTimeout(TIMEOUT);
        UsernamePasswordToken token = new UsernamePasswordToken(username, new Md5Hash(password).toString());
        subject.login(token);

        SavedRequest savedRequest = WebUtils.getSavedRequest(request);
        String url = "/";
        if (savedRequest != null) {
            url = savedRequest.getRequestUrl().substring(request.getContextPath().length());
        }
        if ("/favicon.ico".equalsIgnoreCase(url)) {
            url = "/";
        }
        return new ModelAndView("redirect:" + url);
    }

    @GetMapping("/registe")
    @ResponseBody
    public JsonResult registe(String username, String password, String ensurePassword) {
        System.out.println(username);
        System.out.println(password);
        System.out.println(ensurePassword);
        return JsonResult.success("");
    }
}

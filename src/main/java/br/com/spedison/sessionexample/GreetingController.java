package br.com.spedison.sessionexample;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class GreetingController {

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    private String getClientIpAddress(HttpServletRequest request) {
        StringBuffer ret = new StringBuffer();
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                ret.append(ip+" ; ");
            }
        }
        ret.append(request.getRemoteAddr());
        return ret.toString();
    }

    @GetMapping("/")
    public @ResponseBody
    ResponseEntity<String[]> getMessage(Model model, HttpSession session, HttpServletRequest request) {
        String greetings = (String) session.getAttribute("GREETING_MESSAGES");
        if(greetings == null) {
            greetings = "";
        }

        session.setAttribute("IP", getClientIpAddress(request));

        return new ResponseEntity<String[]>(greetings.split("[;]"), HttpStatus.OK);
    }

    @GetMapping("/messages")
    public @ResponseBody ResponseEntity<String[]> saveMessage(String msg, HttpServletRequest request, HttpSession session)
    {
        String greetings = (String) session.getAttribute("GREETING_MESSAGES");
        if(greetings == null) {
            greetings = msg;
            session.setAttribute("GREETING_MESSAGES", greetings);
        } else {
            greetings += ";" + msg;
        }
        session.setAttribute("GREETING_MESSAGES", greetings);

        session.setAttribute("IP", getClientIpAddress(request));

        return new ResponseEntity<String[]>(greetings.split("[;]"),HttpStatus.OK);
    }
}

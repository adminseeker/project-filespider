package com.aravindweb.authservice.logging;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.aravindweb.authservice.clients.dto.UserRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;

@Aspect
@Component
public class LoggerComponent {

    private static final Logger log = LoggerFactory.getLogger(LoggerComponent.class);

    private static final ObjectMapper mapper = createObjectMapper();

    @Autowired
    private HttpServletRequest request;

    @Pointcut("within(com.aravindweb.authservice.controllers..*)")
    private void controller() {}

    @Pointcut("within(com.aravindweb.authservice..*)")
    private void exceptions() {}

    @Around("controller()")
    public Object logController(ProceedingJoinPoint pjp) throws Throwable {
        final long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        
        Object[] args = pjp.getArgs();
        Parameter[] parameters = method.getParameters();

        Object headers=null;
        Object requestBody=null;
        
        for(int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(RequestBody.class)) {
                requestBody = args[i];
            }
            else if (param.isAnnotationPresent(RequestHeader.class)) {
                headers = args[i];
            }
        }

        String requestLog="";
        try {
            String reqBodyString = mapper.writeValueAsString(requestBody);
            if(request.getRequestURI().equals("/api/v1/auth/register") || request.getRequestURI().contains("/api/v1/auth/login")){
                UserRequest user = mapper.readValue(reqBodyString,UserRequest.class);
                user.setPassword("***********");
                reqBodyString = mapper.writeValueAsString(user);
            }
    
            requestLog = "Request = ";
            requestLog=headers!=null ? requestLog.concat("Headers: ").concat(JSONObject.escape(mapper.writeValueAsString(headers))) : requestLog;
            requestLog=requestLog.concat(",Method: ").concat(request.getMethod());
            requestLog=requestLog.concat(",URI: ").concat(request.getRequestURI());
            requestLog = requestBody!=null ?  requestLog.concat(",Body: ").concat(JSONObject.escape(reqBodyString)) : requestLog;
        } catch (Exception e) {
            log.error("Error in Logging Component Request Logging: "+e.getMessage());
        }
        
        Object result = pjp.proceed(args);

        try {
            String respBody = mapper.writeValueAsString(result);
            String responseLog=",Response = ";
            Long time = System.currentTimeMillis()-start;
            responseLog=responseLog.concat("Time: ").concat(Long.toString(time)).concat("ms");
            responseLog=responseLog.concat(",").concat(JSONObject.escape(respBody));
            log.info(requestLog.concat(responseLog));
        } catch (Exception e) {
            log.error("Error in Logging Component Response Logging: "+e.getMessage());
        }
        
        
        
        return result;
       
    }

    @AfterThrowing(pointcut = "exceptions()", throwing = "e")
    public void logAfterException(JoinPoint jp, Exception e) {
        log.error("Exception during: {} with ex: {}", constructErrorLogMsg(jp),  e.toString());
    }

    private String constructErrorLogMsg(JoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        var sb = new StringBuilder("@");
        sb.append(method.getName());
        return sb.toString();
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return m;
    }

}

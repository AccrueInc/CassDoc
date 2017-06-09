package cwdrg.spring.annotation

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.core.MethodParameter
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.bind.support.WebArgumentResolver
import org.springframework.web.context.request.NativeWebRequest

import java.lang.annotation.Annotation
import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import java.lang.reflect.Type

/**
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMethodAdapter
 * @see org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface RequestParamJSON {
    /**
     * The name of the request parameter to bind to.
     */
    String value() default ""

    /**
     * Whether the parameter is required.
     * <p>Default is <code>true</code>, leading to an exception thrown in case
     * of the parameter missing in the request. Switch this to <code>false</code>
     * if you prefer a <code>null</value> in case of the parameter missing.
     *  <p>Alternatively, provide a {@link #defaultValue() defaultValue},
     * which implicitly sets this flag to <code>false</code>.
     */
    boolean required() default true

    /**
     * The default value to use as a fallback. Supplying a default value implicitly
     * sets {@link #required()} to false.
     */
    String defaultValue() default ValueConstants.DEFAULT_NONE
}

/*
http://scottfrederick.blogspot.com/2011/03/customizing-spring-3-mvcannotation.html
http://impalablog.blogspot.com/2009/05/extending-spring-mvcs-annotation.html
http://blog.springsource.org/2011/02/21/spring-3-1-m1-mvc-namespace-enhancements-and-configuration/
 */
class RequestParameterJSONArgumentResolver implements WebArgumentResolver
{
    private ObjectMapper objectMapper = new ObjectMapper()

    Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception
    {
        String httpParamName
        Annotation paramAnnotation
        Annotation[] paramAnnotations

        try {
            paramAnnotations = methodParameter.getParameterAnnotations()
        } catch (Exception e) {
            throw new IllegalStateException("Failed to introspect method parameter annotations", e)
        }

        for (int j = 0; j < paramAnnotations.length; j++) {
            paramAnnotation = paramAnnotations[j]
            if (RequestParamJSON.class.isInstance(paramAnnotation)) {
                RequestParamJSON attribute = (RequestParamJSON) paramAnnotation
                httpParamName = attribute.value()
            }
            if (httpParamName != null) {
                break
            }
        }

        if (httpParamName == null) {
            return UNRESOLVED
        }

        String httpParamValue = webRequest.getParameter(httpParamName)
        // jackson deserialize
        Type paramtype = methodParameter.getParameterType()
        JavaType javaType = TypeFactory.type(paramtype)
        try {
            Object value = this.objectMapper.readValue(httpParamValue, javaType)
            return value
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("For http parameter "+httpParamName+", could not read JSON: " + ex.getMessage(), ex)
        }

    }
    
    ObjectMapper getObjectMapper() {
        return objectMapper
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }
}

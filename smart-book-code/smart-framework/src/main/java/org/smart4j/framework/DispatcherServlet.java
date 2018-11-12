package org.smart4j.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smart4j.framework.bean.Data;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Param;
import org.smart4j.framework.bean.View;
import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ConfigHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.helper.RequestHelper;
import org.smart4j.framework.helper.ServletHelper;
import org.smart4j.framework.helper.UploadHelper;
import org.smart4j.framework.util.JsonUtil;
import org.smart4j.framework.util.ReflectionUtil;
import org.smart4j.framework.util.StringUtil;

/**
 * 请求转发器
 *
 * @author huangyong
 * @since 1.0.0
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        /**
         * 初始化相关Helper类
         */
        HelperLoader.init();
        /**
         * 获取ServletContext对象（用于注册Servlet）
         */
        ServletContext servletContext = servletConfig.getServletContext();
        /**
         * 注册处理JSP的Servlet
         */
        registerServlet(servletContext);
        /**
         * 配置文件上传
         */
        UploadHelper.init(servletContext);
    }

    private void registerServlet(ServletContext servletContext) {
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping("/index.jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");

        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping("/favicon.ico");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletHelper.init(request, response);
        try {
            // 获取请求方法与请求路径
            String requestMethod = request.getMethod().toLowerCase();
            String requestPath = request.getPathInfo();
            // 获取Action处理器
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
            if (handler != null) {
                // 获取Controller类及其Bean实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
                // 创建请求参数对象
                Param param;
                if (UploadHelper.isMultipart(request)) {
                    param = UploadHelper.createParam(request);
                } else {
                    param = RequestHelper.createParam(request);
                }

                // 调用Action方法
                Method actionMethod = handler.getActionMethod();
                Object result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);

                // 处理Action方法返回值
                if (result instanceof View) {
                    // 返回JSP页面
                    handleViewResult((View) result, request, response);
                } else if (result instanceof Data) {
                    // 返回JSON数据
                    handleDataResult((Data) result, response);
                }
            }
        } finally {
            ServletHelper.destroy();
        }
    }

    private void handleViewResult(View view, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)) {
            if (path.startsWith("/")) {
                response.sendRedirect(request.getContextPath() + path);
            } else {
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
            }
        }
    }

    private void handleDataResult(Data data, HttpServletResponse response) throws IOException {
        Object model = data.getModel();
        if (model != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }
}

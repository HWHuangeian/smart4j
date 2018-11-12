package org.smart4j.framework.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理链
 *
 * @author huangyong
 * @since 1.0.0
 */
public class ProxyChain {

    /**
     * 目标类
     */
    private final Class<?> targetClass;
    /**
     * 目标对象
     */
    private final Object targetObject;
    /**
     * 目标方法
     */
    private final Method targetMethod;
    /**
     * 方法代理
     */
    private final MethodProxy methodProxy;
    /**
     * 方法参数
     */
    private final Object[] methodParams;
    /**
     * 代理列表
     */
    private List<Proxy> proxyList;
    /**
     * 代理索引
     */
    private int proxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy, Object[] methodParams, List<Proxy> proxyList) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methodParams;
        this.proxyList = proxyList;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object doProxyChain() throws Throwable {
        Object methodResult;
        if (proxyIndex < proxyList.size()) {
            // 根据调用链中定义的代理对象顺序进行调用
            methodResult = proxyList.get(proxyIndex++).doProxy(this);
        } else {
            // 代理对象调用完毕，可以执行业务方法
            methodResult = methodProxy.invokeSuper(targetObject, methodParams);
        }
        return methodResult;
    }
}
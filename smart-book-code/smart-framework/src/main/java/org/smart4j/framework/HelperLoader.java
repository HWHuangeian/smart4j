package org.smart4j.framework;

import org.smart4j.framework.helper.AopHelper;
import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ClassHelper;
import org.smart4j.framework.helper.ControllerHelper;
import org.smart4j.framework.helper.IocHelper;
import org.smart4j.framework.util.ClassUtil;

/**
 * 加载相应的 Helper 类
 *
 * @author huangyong
 * @since 1.0.0
 */
public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
            // 加载配置信息，获取配置信息中的指定包名，然后获取指定包名下的所有类的Class类型
            ClassHelper.class,
            // 为每个Class类型创建一个单例对象，放入IOC容器
            BeanHelper.class,
            // 为每个Class类型创建代理对象集，放入IOC容器
            AopHelper.class,
            // 为容器里的每个对象注入依赖
            IocHelper.class,
            // 解析Controller的Action注解，生成Request和Handler
            ControllerHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}